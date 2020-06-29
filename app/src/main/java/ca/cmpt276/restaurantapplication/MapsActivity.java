package ca.cmpt276.restaurantapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.getdata.DownloadDataFromURL;
import ca.cmpt276.getdata.GetMetaData;
import ca.cmpt276.model.Inspection;
import ca.cmpt276.model.Restaurant;
import ca.cmpt276.model.RestaurantManager;
import ca.cmpt276.model.SavedData;
import ca.cmpt276.restaurantapplication.adapters.ClusterItemAdapter;
import ca.cmpt276.restaurantapplication.markercluster.MarkerClusterItem;
import ca.cmpt276.restaurantapplication.markercluster.MarkerClusterRenderer;


// How to use Google Maps API
// https://www.youtube.com/watch?v=fPFr0So1LmI&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=6&t=0s

// Clusters
// https://developers.google.com/maps/documentation/android-sdk/utility/marker-clustering#info-window
// http://mobiledevhub.com/2018/11/16/android-how-to-group-and-cluster-markers-in-google-maps/

// Setting Custom Markers:
// https://developers.google.com/maps/documentation/android-sdk/marker

//  Red Map Marker
// https://www.flaticon.com/free-icon/placeholder_119065
// https://www.flaticon.com/free-icon/alert_1476778

// Yellow Map Marker
// https://www.flaticon.com/free-icon/pin_1705666
// https://www.flaticon.com/free-icon/placeholder_1180049?term=location%20minus&page=1&position=5#

// Green Map Marker
// https://www.flaticon.com/free-icon/placeholder_1180036?term=Map%20pin%20check%20mark&page=1&position=18
// https://www.flaticon.com/free-icon/arrived_1476776#

// Setup info window for cluster items.
// https://stackoverflow.com/questions/25968486/how-to-add-info-window-for-clustering-marker-in-android

// Working with Google Map API as fragment
// https://demonuts.com/android-google-map-in-fragment/

/**
 * This activity displays Google Maps using Google's API
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, Filterable {

    private static final String FILTER_HAZARD_LEVEL_KEY = "ca.cmpt276.maps.hazardLevel";
    private static final String FILTER_IS_FAVOURITE_CHECKED_KEY = "ca.cmpt276.maps.isFavouriteChecked";
    private static final String FILTER_NUM_CRIT_ISSUES_KEY = "ca.cmpt276.maps.numCritIssues";
    private static final String FILTER_COMPARISON_NUM_CRIT_KEY = "ca.cmpt276.maps.comparisonNumCrit";
    private static final String APP_PREFS = "AppPrefsMaps";

    private GoogleMap mMap;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private LatLng myPosition;
    private static String filterHazardLevel = "";
    private static boolean isFilterCheckBoxFavouriteChecked = false;
    private static int filterCritIssues = -1;
    private static String filterComparison = "<=";
    private RestaurantManager manager_restaurant;
    private ClusterManager<MarkerClusterItem> clusterManager;
    private MarkerClusterItem clickedClusterItem;
    private ImageView mRestaurantListsView;
    private ImageView mGpsCurrentLocation;
    private Button mStopUpdateButton;
    private Button mViewFavouriteRestaurants;
    private boolean isInspectionGPSCoordinatesClick = false;

    private static final String TAG = "MapActivity";
    private static final String EXTRA_RESTAURANT = "EXTRA_RESTAURANT";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 12f;

    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Location lastCurrentLocation;
    private List<Restaurant> restaurantList;
    private List<Restaurant> restaurantListFull;
    private boolean isTextInSearchQuery = false;
    private boolean isFiltersApplied = false;
    private Menu menu;
    // Used the tutorial playlist below to learn how to use Google Maps' API
    // https://www.youtube.com/watch?v=fPFr0So1LmI&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=6&t=0s
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        RestaurantManager.setStaticContext(this);
        manager_restaurant = RestaurantManager.getInstance();
        boolean isDataLoaded = manager_restaurant.checkDataLoaded();

        final String defaultFilterHazardLevel = "";
        final String defaultFilterComparison = "<=";
        final String defaultFilterNumCritIssues = "";
        final boolean defaultIsFilterCheckBoxFavouriteChecked = false;

        // Inside the if statement, the functions should only run once
        // when the application starts, the data would be loaded
        // when an activity starts a new MapActivity intent, this should not run.
        // this prevents the csv files being loaded more than once.
        if (!isDataLoaded) {
            // https://developer.android.com/reference/java/io/BufferedReader
            // https://stackoverflow.com/questions/43055661/reading-csv-file-in-android-app
            // Reads the inspectionreports_itr1 CSV file and initializes the data
            InputStream Inspection_is = getResources().openRawResource(R.raw.inspectionreports_itr1);
            BufferedReader reader_inspection = new BufferedReader(new InputStreamReader(Inspection_is, StandardCharsets.UTF_8));
            manager_restaurant.readInspectionData(reader_inspection);

            // Reads the restaurants_itr1 CSV file and initializes the data
            InputStream Restaurant_is = getResources().openRawResource(R.raw.restaurants_itr1);
            BufferedReader reader_restaurant = new BufferedReader(new InputStreamReader(Restaurant_is, StandardCharsets.UTF_8));
            manager_restaurant.readRestaurantData(reader_restaurant);
            manager_restaurant.sortRestaurantsAlphabetically(manager_restaurant.getRestaurantList());

            // May not need this, but this reads all the possible violations
            InputStream Issue_is = getResources().openRawResource(R.raw.all_violations);
            BufferedReader reader_issue = new BufferedReader(new InputStreamReader(Issue_is, StandardCharsets.UTF_8));
            manager_restaurant.readViolationData(reader_issue);
            manager_restaurant.setInspectionStaticIssueList();
            manager_restaurant.setIsDataLoaded(true);
        }

        SavedData.setActivityParent(this);
        Log.d("MapsActivity Check", "data updated: " + manager_restaurant.isDataUpdated()
                + "  --  permissions: " + SavedData.isLocationPermissionGranted());
        if(!manager_restaurant.isDataUpdated() && SavedData.isLocationPermissionGranted()){
            checkAndLoadData();
        }

        setupButtons();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
        revealDisplayFavoruiteRestaurantsInspectionBtn();

        // Clear the filter settings when a MapsActivity is created.
        saveFilterSettings(defaultFilterHazardLevel, defaultFilterComparison,
                defaultFilterNumCritIssues,defaultIsFilterCheckBoxFavouriteChecked);
    }

    private void displayFavouriteRestaurantsActivity() {
        if(RestaurantManager.getInstance().getFavouriteRestaurants().size() != 0) {
            Intent intent = RecentInspectionsFavouriteRestaurants.makeIntent(this);
            startActivity(intent);
        }
    }

    private void revealDisplayFavoruiteRestaurantsInspectionBtn() {
        if(RestaurantManager.getInstance().getFavouriteRestaurants().size() != 0) {
            Button viewFavourites = findViewById(R.id.mapsViewFavourites);
            viewFavourites.setVisibility(View.VISIBLE);
            String text = getResources().getString(R.string.toast_msg_recent_inspections_fav_restaurants);
            Toast.makeText(MapsActivity.this,
                    text, Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkNetworkConnectivity(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void checkAndLoadData(){
        DownloadDataFromURL.setMapsActivity(this);
        Thread threadGetData = new Thread(){
            public void run(){
                Log.d("MainActivity SavedData", "Thread start");
                manager_restaurant.getSavedData().loadLastDownloadInfo();
                loadSavedData();
                checkForNewDataAndDownload();
            }
        };
        threadGetData.start();
    }

    private void loadSavedData(){
        manager_restaurant.getSavedData().setArrow();
        Log.d("MainActivity SavedData","Start saving data");
        manager_restaurant.getSavedData().execute();
    }

    private void checkForNewDataAndDownload(){
        GetMetaData newData = new GetMetaData(this,
                MapsActivity.this, checkNetworkConnectivity());
        Log.d("MainActivity SavedData","Starting loading data");
        newData.execute();
    }


    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapsActivity.this);
    }


    private void getDeviceLocation(){
        Log.d("MapActivity", "getDeviceLocation: getting the devices current location");

        try{
            if(mLocationPermissionsGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            if(currentLocation != null) {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM);

                                startLocationUpdates();


                            }

                        } else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        clusterManager = new ClusterManager<>(this, mMap);
        if (mLocationPermissionsGranted) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Intent intent = getIntent();

            isInspectionGPSCoordinatesClick = InspectionActivity.getExtraInspectionMapsCoordinates(intent);

            if(isInspectionGPSCoordinatesClick) {
                onClickGPSCoordinatesInInspection();
            }
            else {
                getDeviceLocation();
            }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            setupClusterManager();
            setLocationUpdate();
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /**
     * Creating app buttons for action bar in the issue activity
     * Code adapted from 'John's Android Studio Tutorials':
     * https://www.youtube.com/watch?v=5MSKuVO2hV4
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map_activity, menu);
        this.menu = menu;

        // Acquires search bar
        MenuItem searchItem = menu.findItem(R.id.action_search_in_maps);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();

        //https://stackoverflow.com/questions/7409288/how-to-dismiss-keyboard-in-android-searchview
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!query.equals("")) {
                    isTextInSearchQuery = true;
                }
                else {
                    isTextInSearchQuery = false;
                }
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.equals("")) {
                    isTextInSearchQuery = true;
                }
                else {
                    isTextInSearchQuery = false;
                }
                getFilter().filter(newText);
                return false;
            }
        });

        // Disable the search feature if InspectionsGpsClicked
        setItemsVisibility(menu, isInspectionGPSCoordinatesClick);
        return super.onCreateOptionsMenu(menu);
    }

    private boolean isFiltersChanged(String filterHazardLevel, int filterNumCritIssues, boolean
            isFilterFavouriteChecked)
    {
        final String defaultFilterHazardLevel = "";
        final int defaultFilterCritIssues = -1;

        // No filters set
        if(filterHazardLevel.equals(defaultFilterHazardLevel)
                && filterNumCritIssues == defaultFilterCritIssues
                && !isFilterFavouriteChecked) {
            return false;
        }

        else {
            return true;
        }
    }

    //https://stackoverflow.com/questions/42295164/how-to-hide-menu-item-from-toolbar-when-click-on-search-icon-in-android
    private void setItemsVisibility(Menu menu, boolean isInspectionGPSCoordinatesClick) {
        for (int i=0; i<menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            // Sets the items in the actionbar visible if the inspectionGPS coords weren't clicked
            if(item.getItemId() == R.id.action_settings_in_maps
                    || item.getItemId() == R.id.action_search_in_maps)
            {
                item.setVisible(!isInspectionGPSCoordinatesClick);
            }

            else if (item.getItemId() == R.id.actionbar_back_maps_inspection) {
                item.setVisible(isInspectionGPSCoordinatesClick);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // Acquires the item selected in Action Bar.
        int id = item.getItemId();
        if(id == R.id.action_settings_in_maps) {
            displayFilterSettingsDialog();
        }
        else if (id == R.id.actionbar_back_maps_inspection) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    // Used the link below to learn how to clear the activity stack.
    // https://stackoverflow.com/questions/34195828/how-to-come-back-to-first-activity-without-its-oncreate-called/43466522
    private void setupButtons() {
        mRestaurantListsView = findViewById(R.id.mapsReturnRestaurantList);
        mGpsCurrentLocation = findViewById(R.id.mapsGetCurrentLocation);
        mStopUpdateButton = findViewById(R.id.mapsStopFollow);
        mViewFavouriteRestaurants = findViewById(R.id.mapsViewFavourites);
        //https://www.youtube.com/watch?v=iWYsBDCGhGw&t=351s
        // Stop update button should be visible first when MapsActivity is launched.
        mGpsCurrentLocation.setVisibility(View.INVISIBLE);
        mStopUpdateButton.setVisibility(View.VISIBLE);

        mRestaurantListsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setResult(Activity.RESULT_OK, getIntent);
                //finish();
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                if(isFiltersApplied || isTextInSearchQuery) {
                    final String text = getResources().getString(R.string.mapsactivity_toast_text);
                    Toast.makeText(MapsActivity.this, text, Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = MainActivity.makeIntent(MapsActivity.this);
                    startActivity(intent);
                    finish();
                }

            }
        });

        mGpsCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
                // Current Location Button is clicked:
                // Set current location button to be invisible, but set stop update button to visible
                mGpsCurrentLocation.setVisibility(View.INVISIBLE);
                mStopUpdateButton.setVisibility(View.VISIBLE);
            }
        });

        mStopUpdateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                // Stop Update Button is clicked:
                // Set stop update button to be invisible, but set current location button to visible
                mGpsCurrentLocation.setVisibility(View.VISIBLE);
                mStopUpdateButton.setVisibility(View.INVISIBLE);
            }
        });
        // Favourite Restaurants only appear when there are recent inspections for your favourites.
        mViewFavouriteRestaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayFavouriteRestaurantsActivity();
            }
        });

    }

    // Inside single restaurant activity (InspectionActivity.java)
    // when gps coordinates clicked, the MapActivity launches and this function displays
    // that single restaurant as a cluster item on the map
    private void onClickGPSCoordinatesInInspection() {

            // Need to set the correct buttons to be visible when MapActivity is called from
            // InspectionsActivity from clicking the GPS coordinates.
            mGpsCurrentLocation.setVisibility(View.VISIBLE);
            mStopUpdateButton.setVisibility(View.INVISIBLE);
            mRestaurantListsView.setVisibility(View.INVISIBLE);
            Intent intent = getIntent();
            String restaurantName = InspectionActivity.getExtraInspectionMapsRestaurantName(intent);
            String restaurantAddress = InspectionActivity.getExtraInspectionMapsRestaurantAddress(intent);
            String hazard = InspectionActivity.getExtraInspectionMapsRestaurantHazard(intent);
            double latitude = InspectionActivity.getExtraInspectionMapsCoordinatesLatitude(intent);
            double longitude = InspectionActivity.getExtraInspectionMapsCoordinatesLongitude(intent);

            // Needed to get the inspection of that restaurant when the info window is clicked
            int restaurantPositionInRecyclerView = InspectionActivity.getExtraInspectionMapsRestaurantPosition(intent);
            int hazardIcon = setRestaurantHazardPegs(hazard);
            LatLng latLng = new LatLng(latitude, longitude);

            // Description for the info box when the marker is shown.
            String restaurantAddressHazard =
                    getString(R.string.inspectionAddress)+ restaurantAddress +"\n"+
                    getString(R.string.hazard_level) + hazard;

            moveCamera(latLng, DEFAULT_ZOOM);


        // Set the properties of the cluster item.
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(restaurantName)
                .snippet(restaurantAddressHazard)
                .icon(BitmapDescriptorFactory.fromResource(hazardIcon));

        MarkerClusterItem clusterItem = new MarkerClusterItem(options.getPosition(),
                options.getTitle(), options.getIcon(),
                options.getSnippet(), restaurantPositionInRecyclerView);
        clusterManager.addItem(clusterItem);
    }

    // Sets the restaurant's hazard peg icon
    private int setRestaurantHazardPegs(String hazardLevel) {
        int hazardColour;

        final String MODERATE_HAZARD = getResources().getString(R.string.hazard_level_moderate);
        final String HIGH_HAZARD = getResources().getString(R.string.hazard_level_high);

        if(hazardLevel.equals(HIGH_HAZARD)) {
            hazardColour = R.drawable.red_map_marker_2;
        }
        else if (hazardLevel.equals(MODERATE_HAZARD)) {
            hazardColour = R.drawable.yellow_map_marker_2;
        }
        else {
            hazardColour = R.drawable.green_map_marker_2;
        }
        return hazardColour;
    }

    // General case where all pegs are displayed.
    private void placeRestaurantPegs() {
        restaurantList = manager_restaurant.getRestaurantList();
        restaurantListFull = new ArrayList<>(restaurantList);
        int restaurantPosition = 0;
        for (Restaurant restaurant:manager_restaurant.getRestaurantList()) {
            Inspection inspection = restaurant.getRecentInspection();
            restaurant.setRestaurantPosition(restaurantPosition);
            String hazardLevel = "";

            if (inspection != null) {
                hazardLevel = inspection.getHazardRating();
            }

            int hazardColour = setRestaurantHazardPegs(hazardLevel);
            LatLng restaurantLocation = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());

            // Description for the info box when a clusterItem is clicked.
            String restaurantAddressHazard =
                    getString(R.string.inspectionAddress)+ restaurant.getPhysicalAddress() +"\n"+
                    getString(R.string.hazard_level) + hazardLevel;

            // Set the properties of the cluster item.
            MarkerOptions options = new MarkerOptions()
                    .position(restaurantLocation)
                    .title(restaurant.getRestaurantName())
                    .snippet(restaurantAddressHazard)
                    .icon(BitmapDescriptorFactory.fromResource(hazardColour));
            // Needed to get the inspection of that restaurant when the info window is clicked
            int restaurantPositionInRecyclerView = restaurant.getRestaurantPosition();

            // Add item to cluster
            MarkerClusterItem clusterItem = new MarkerClusterItem(options.getPosition(),
                    options.getTitle(), options.getIcon(),
                    options.getSnippet(), restaurantPositionInRecyclerView);

            clusterManager.addItem(clusterItem);
            restaurantPosition++;
            //clusterManager.clearItems();
        }
        restaurantPosition = 0;
    }

    // Re-adding pegs back in search
    private void addRestaurantPegsOnSearch() {
        clusterManager.clearItems();
        int restaurantPosition = 0;
        for (Restaurant restaurant:restaurantList) {
            Inspection inspection = restaurant.getRecentInspection();
            restaurant.setRestaurantPosition(restaurantPosition);
            String hazardLevel = "";

            if (inspection != null) {
                hazardLevel = inspection.getHazardRating();
            }

            int hazardColour = setRestaurantHazardPegs(hazardLevel);
            LatLng restaurantLocation = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());

            // Description for the info box when a clusterItem is clicked.
            String restaurantAddressHazard =
                    getString(R.string.inspectionAddress)+ restaurant.getPhysicalAddress() +"\n"+
                    getString(R.string.hazard_level) + hazardLevel;

            // Set the properties of the cluster item.
            MarkerOptions options = new MarkerOptions()
                    .position(restaurantLocation)
                    .title(restaurant.getRestaurantName())
                    .snippet(restaurantAddressHazard)
                    .icon(BitmapDescriptorFactory.fromResource(hazardColour));
            // Needed to get the inspection of that restaurant when the info window is clicked
            int restaurantPositionInRecyclerView = restaurant.getRestaurantPosition();

            // Add item to cluster
            MarkerClusterItem clusterItem = new MarkerClusterItem(options.getPosition(),
                    options.getTitle(), options.getIcon(),
                    options.getSnippet(), restaurantPositionInRecyclerView);

            clusterManager.addItem(clusterItem);
            restaurantPosition++;
        }
        restaurantPosition = 0;
    }

    // This gets called when request for location is asked for the first time.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    SavedData.setActivityParent(this);
                    SavedData.setAndSaveLocationPermission(true);
                    // Refresh activity after getting permissions to get the blue circle dot
                    // current location
                    finish();
                    startActivity(getIntent());
                }
            }
        }
    }

    /**
     * Current location update functions below:
     * Used the link below to learn how to update a user's current location in real-time.
     * https://developer.android.com/training/location/request-updates?fbclid=IwAR3ZJw-unOhwSJaYcmylNCFh2sA4u7CDpW3Hhm5SaspNPxY3MPiGz9R3Xdk
     */
    private void setLocationUpdate() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                lastCurrentLocation = locationResult.getLastLocation();
                if (lastCurrentLocation != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(lastCurrentLocation.getLatitude(),
                                    lastCurrentLocation.getLongitude()), DEFAULT_ZOOM));
                }
            }
        };
    }

    private void startLocationUpdates() {
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    /**
     * Cluster functions below:
     * Used the links below to learn how to create a clusterManager and renderer
     * https://developers.google.com/maps/documentation/android-sdk/utility/marker-clustering#info-window
     * http://mobiledevhub.com/2018/11/16/android-how-to-group-and-cluster-markers-in-google-maps/
     */
    private void setupClusterRenderer() {
        MarkerClusterRenderer<MarkerClusterItem> clusterRenderer = new MarkerClusterRenderer<>(this, mMap, clusterManager, isInspectionGPSCoordinatesClick);
        clusterManager.setRenderer(clusterRenderer);
    }

    private void setupClusterManager() {

        if(!isInspectionGPSCoordinatesClick) {
            placeRestaurantPegs();
        }

        setupClusterRenderer();
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
        mMap.setOnMarkerClickListener(clusterManager);

        // https://stackoverflow.com/questions/25395357/android-how-to-uncluster-on-single-tap-on-a-cluster-marker-maps-v2
        // View all markers in the clicked cluster
        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MarkerClusterItem>() {
            @Override
            public boolean onClusterClick(Cluster<MarkerClusterItem> cluster) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        cluster.getPosition(), (float) Math.floor(mMap
                                .getCameraPosition().zoom + 1)), 300,
                        null);
                return true;
            }
        });

        clusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<MarkerClusterItem>() {
            @Override
            public void onClusterItemInfoWindowClick(MarkerClusterItem markerClusterItem) {
                if(isInspectionGPSCoordinatesClick) {
                    finish();
                }
                else {
                    Intent intent = InspectionActivity.makeIntent(MapsActivity.this);
                    intent.putExtra(EXTRA_RESTAURANT, markerClusterItem.getPositionInRecyclerView());
                    startActivity(intent);
                }
        }
        });

        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MarkerClusterItem>() {
            @Override
            public boolean onClusterItemClick(MarkerClusterItem markerClusterItem) {
                clickedClusterItem = markerClusterItem;
                return  false;
            }
        });

        clusterManager.getMarkerCollection().setInfoWindowAdapter(new ClusterItemAdapter
                (this, clickedClusterItem, isInspectionGPSCoordinatesClick));
        clusterManager.cluster();
    }

    // When back button on android clicked exit application.
    //https://stackoverflow.com/questions/4778754/how-do-i-kill-an-activity-when-the-back-button-is-pressed
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keycode, event);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, MapsActivity.class);
    }

    public static String getFilterHazardLevel() {
        return filterHazardLevel;
    }

    public static int getFilterCritIssues() {
        return filterCritIssues;
    }

    public static boolean getFilterFavouriteChecked (){
        return isFilterCheckBoxFavouriteChecked;
    }

    public static String getFilterComparison() {
        return filterComparison;
    }
    // Used the two videos below to learn how to use check boxes and spinners
    // https://www.youtube.com/watch?v=QFCRqyWA6zg
    // https://www.youtube.com/watch?v=nlqtyfshUkc
    private void displayFilterSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.filter_search_dialog_maps, null);

        String alertDialogTitle = getResources().getString(R.string.alert_filter_dialog_title);
        builder.setTitle(alertDialogTitle);

        CharSequence filterNumCritIssuesText = getSharedFilterNumCritIssues(this);
        EditText editTextNumCritIssues = view.findViewById(R.id.filter_editText_num_critical_violations_maps);
        editTextNumCritIssues.setText(filterNumCritIssuesText);


        CheckBox checkBoxFavourites = view.findViewById(R.id.filter_checkBox_tag_favourite_maps);
        checkBoxFavourites.setChecked(getSharedIsFilterFavouriteChecked(this));

        Spinner spinnerHazardLevels = view.findViewById(R.id.filter_spinner_hazard_levels_maps);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.hazard_levels));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHazardLevels.setAdapter(arrayAdapter);

        int spinnerPosition = getSpinnerHazardPosition();
        spinnerHazardLevels.setSelection(spinnerPosition);

        RadioGroup radioGroup = view.findViewById(R.id.radio_group_comparisons_maps);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton btn = group.findViewById(checkedId);
                filterComparison = btn.getText().toString();
            }
        });
        //https://stackoverflow.com/questions/9842516/set-selected-index-of-an-android-radiogroup
        int radioGroupPosition = getRadioGroupNumIssuesPosition();
        ((RadioButton)radioGroup.getChildAt(radioGroupPosition)).setChecked(true);

        String alertDialogPositiveBtnTxt = getResources().getString(R.string.alert_filter_dialog_positive);
        builder.setPositiveButton(alertDialogPositiveBtnTxt, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String itemFromSpinerSelectedHazardLevel = spinnerHazardLevels.getSelectedItem().toString();
                String numCritIssuesText = editTextNumCritIssues.getText().toString();
                boolean isCheckBoxFavouritesChecked = checkBoxFavourites.isChecked();
                setFilterData(itemFromSpinerSelectedHazardLevel, numCritIssuesText, isCheckBoxFavouritesChecked);
            }
        });

        String alertDialogNegativeBtnTxt = getResources().getString(R.string.alert_filter_dialog_negative);
        builder.setNegativeButton(alertDialogNegativeBtnTxt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String defaultFilterHazardLevel = "";
                final String defaultFilterCritIssues = "-1";
                boolean defaultCheckedFavourites = false;
                setFilterData(defaultFilterHazardLevel, defaultFilterCritIssues, defaultCheckedFavourites);
                dialog.dismiss();
            }
        });
        builder.setView(view);

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    /**
     * https://www.youtube.com/watch?v=fJEFZ6EOM9o&t=611s
     * SharedPreferences methods below:
     */
    private void saveFilterSettings(String filterHazardLevel, String filterComparison,
                                    String filterNumCritIssues, boolean isFilterCheckBoxFavouriteChecked) {
        String defaultFilterHazardLevel = "";
        String defaultFilterComparison = "<=";
        String defaultFilterCritIssues = "-1";
        boolean defaultCheckedFavourites = false;
        boolean isDefaultHazardLevelSelected = filterHazardLevel.toLowerCase()
                .equalsIgnoreCase(getResources()
                        .getString(R.string.filter_stringarray_hazard_default_level));

        if(isDefaultHazardLevelSelected) {
            filterHazardLevel = "";
        }
        SharedPreferences prefs = this.getSharedPreferences(APP_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(FILTER_HAZARD_LEVEL_KEY, filterHazardLevel);
        editor.putString(FILTER_COMPARISON_NUM_CRIT_KEY, filterComparison);
        editor.putString(FILTER_NUM_CRIT_ISSUES_KEY, filterNumCritIssues);
        editor.putBoolean(FILTER_IS_FAVOURITE_CHECKED_KEY, isFilterCheckBoxFavouriteChecked);
        editor.apply();
    }

    private String getSharedFilterNumCritIssues(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFS, MODE_PRIVATE);
        final String defaultFilterCritIssues = "";
        return prefs.getString(FILTER_NUM_CRIT_ISSUES_KEY, defaultFilterCritIssues);
    }

    private boolean getSharedIsFilterFavouriteChecked(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFS, MODE_PRIVATE);
        boolean defaultCheckedFavourites = false;
        return prefs.getBoolean(FILTER_IS_FAVOURITE_CHECKED_KEY, defaultCheckedFavourites);
    }

    private int getSpinnerHazardPosition() {
        final String LOW_HAZARD = getResources().getString(R.string.hazard_level_low);
        final String MODERATE_HAZARD = getResources().getString(R.string.hazard_level_moderate);
        final String HIGH_HAZARD = getResources().getString(R.string.hazard_level_high);

        String hazardLevel = getSharedHazardLevel(this);
        int position = 0;
        if(hazardLevel.equals(LOW_HAZARD)) {
            position = 1;
        }
        else if (hazardLevel.equals(MODERATE_HAZARD)){
            position = 2;
        }
        else if (hazardLevel.equals(HIGH_HAZARD)) {
            position = 3;
        }
        return position;
    }

    private String getSharedHazardLevel(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFS, MODE_PRIVATE);
        String defaultFilterHazardLevel = "";
        return prefs.getString(FILTER_HAZARD_LEVEL_KEY, defaultFilterHazardLevel);
    }

    private int getRadioGroupNumIssuesPosition() {
        String comparison = getSharedFilterComparison(this);
        int position = 0;
        if(!comparison.equals("<=")) {
            position = 1;
        }
        return position;
    }

    private String getSharedFilterComparison(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFS, MODE_PRIVATE);
        String defaultFilterComparison = "<=";
        return prefs.getString(FILTER_COMPARISON_NUM_CRIT_KEY, defaultFilterComparison);
    }

    // Sets the filter data from the filter settings when search is prompted
    private void setFilterData(String itemFromSpinerSelected,  String numCritIssuesText,
                               boolean isFavouriteCheckBoxSelected) {
        boolean isDefaultHazardLevelSelected = itemFromSpinerSelected.toLowerCase()
                .equalsIgnoreCase(getResources()
                        .getString(R.string.filter_stringarray_hazard_default_level));
        // Default values
        String defaultFilterHazardLevel = "";

        // Set an arbitary value of -1 to use as a check when displaying filtered data
        int defaultFilterCritIssues = -1;

        boolean  defaultFavouriteSelected = false;

        if (isDefaultHazardLevelSelected) {
            filterHazardLevel = defaultFilterHazardLevel;
        }
        else {
            filterHazardLevel = itemFromSpinerSelected;
        }

        if(!numCritIssuesText.equals("")) {
            filterCritIssues = Integer.parseInt(numCritIssuesText);
        }
        else {
            filterCritIssues = defaultFilterCritIssues;
        }

        if(isFavouriteCheckBoxSelected) {
            isFilterCheckBoxFavouriteChecked = isFavouriteCheckBoxSelected;
        }
        else {
            isFilterCheckBoxFavouriteChecked = defaultFavouriteSelected;
        }

        // Since -1 is the default value, set it blank in the saveFilter settings
        if(numCritIssuesText.equals("-1")) {
            saveFilterSettings(filterHazardLevel, filterComparison,"", isFilterCheckBoxFavouriteChecked);
        }
        else {
            saveFilterSettings(filterHazardLevel, filterComparison,numCritIssuesText, isFilterCheckBoxFavouriteChecked);
        }

        isFiltersApplied = isFiltersChanged(filterHazardLevel, filterCritIssues, isFilterCheckBoxFavouriteChecked);
        // Refreshes the adapter to display the search with filters.
        MenuItem searchItem = menu.findItem(R.id.action_search_in_maps);
        SearchView searchView = (SearchView) searchItem.getActionView();
        String query = searchView.getQuery().toString();

        // Refreshes the adapter to display the search with filters.
        getFilter().filter(query);
    }


    /**
     * Filter class for MapsActivity
     * Used the videos below to learn how to filter results (SearchView) in recyclerView
     * https://www.youtube.com/watch?v=sJ-Z9G0SDhc&t=693s
     * https://www.youtube.com/watch?v=CTvzoVtKoJ8&t=45s
     */
    @Override
    public Filter getFilter() {
        return mapsFilter;
    }

    private Filter mapsFilter = new Filter() {
        final String defaultFilterHazardLevel = "";
        final int defaultFilterCritIssues = -1;
        final boolean defaultFilterCheckBoxFavouriteChecked = false;
        // Checks if any filters are applied to the search
        private boolean isFiltersChanged(String filterHazardLevel, int filterNumCritIssues, boolean
                isFilterFavouriteChecked)
        {
            // No filters set
            if(filterHazardLevel.equals(defaultFilterHazardLevel)
                    && filterNumCritIssues == defaultFilterCritIssues
                    && isFilterFavouriteChecked == defaultFilterCheckBoxFavouriteChecked) {
                return false;
            }

            else {
                return true;
            }
        }

        private FilterResults filterData(CharSequence constraint) {

            String filterHazardLevel = MapsActivity.getFilterHazardLevel();
            int filterNumCritIssues = MapsActivity.getFilterCritIssues();
            boolean isFilterFavouriteChecked = MapsActivity.getFilterFavouriteChecked();
            boolean isFiltersChanged = isFiltersChanged(filterHazardLevel, filterNumCritIssues,
                    isFilterFavouriteChecked);
            String filterComparison = MapsActivity.getFilterComparison();
            final String LOW_HAZARD = getResources().getString(R.string.hazard_level_low);

            List<Restaurant> filteredRestaurants = new ArrayList<>();

            // Nothing typed in the textBox
            if(constraint == null || constraint.length() == 0 && !isFiltersChanged) {
                filteredRestaurants.addAll(restaurantListFull);
            }
            // Nothing typed in the textBox, but filters were applied.
            else if (constraint == null || constraint.length() == 0 && isFiltersChanged) {
                for (Restaurant restaurant:restaurantListFull) {
                    // Settings up variables for filter check
                    Inspection recentInspection = restaurant.getRecentInspection();
                    boolean isRestaurantFavourite = restaurant.getFavourite();

                    String recentInspectionHazardLevel = "";
                    int recentInspectionNumCritIssues = -1;
                    boolean isRecentInspectionWithinYear = true;

                    if (recentInspection != null) {
                        recentInspectionHazardLevel = recentInspection.getHazardRating();
                        recentInspectionNumCritIssues = recentInspection.getNumCritical();
                        isRecentInspectionWithinYear = recentInspection.getInspectionWithinYear();
                        if (recentInspectionHazardLevel.equals("")) {
                            recentInspectionHazardLevel = LOW_HAZARD;
                        }
                    }
                    else {
                        recentInspectionHazardLevel = LOW_HAZARD;
                        recentInspectionNumCritIssues = 0;
                    }

                    // filterHazardLevel, filterNumCritIssues <=, filterFavourite were set
                    if (recentInspectionHazardLevel.equals(filterHazardLevel)
                            && filterComparison.equals("<=")
                            && recentInspectionNumCritIssues <= filterNumCritIssues
                            && isRecentInspectionWithinYear
                            && isFilterFavouriteChecked && isRestaurantFavourite)
                    {
                        filteredRestaurants.add(restaurant);
                    }
                    // filterHazardLevel, filterNumCritIssues >=, filterFavourite were set
                    else if (recentInspectionHazardLevel.equals(filterHazardLevel)
                            && filterComparison.equals(">=")
                            && recentInspectionNumCritIssues >= filterNumCritIssues
                            && isRecentInspectionWithinYear
                            && isFilterFavouriteChecked && isRestaurantFavourite)
                    {
                        filteredRestaurants.add(restaurant);
                    }

                    // Two filters were set
                    // fc = filterNumCritIssues
                    // hl = filterHazardLevel
                    // ff = filterFavourites
                    //fc <=,hl
                    else if (recentInspectionHazardLevel.equals(filterHazardLevel)
                            && filterComparison.equals("<=")
                            && recentInspectionNumCritIssues <= filterNumCritIssues
                            && isRecentInspectionWithinYear
                            && !isFilterFavouriteChecked) {
                        filteredRestaurants.add(restaurant);
                    }
                    //ff,fc <=
                    else if (filterHazardLevel.equals("")
                            && filterComparison.equals("<=")
                            && recentInspectionNumCritIssues <= filterNumCritIssues
                            && isRecentInspectionWithinYear
                            && isRestaurantFavourite) {
                        filteredRestaurants.add(restaurant);
                    }
                    //fc >=,hl
                    else if (recentInspectionHazardLevel.equals(filterHazardLevel)
                            && filterComparison.equals(">=")
                            && recentInspectionNumCritIssues >= filterNumCritIssues
                            && isRecentInspectionWithinYear
                            && !isFilterFavouriteChecked) {
                        filteredRestaurants.add(restaurant);
                    }
                    //ff,fc >=
                    else if (filterHazardLevel.equals("")
                            && filterComparison.equals(">=")
                            && recentInspectionNumCritIssues >= filterNumCritIssues
                            && isRecentInspectionWithinYear
                            && isRestaurantFavourite) {
                        filteredRestaurants.add(restaurant);
                    }
                    //ff,hl
                    else if (recentInspectionHazardLevel.equals(filterHazardLevel) &&
                            filterNumCritIssues == -1
                            && isRestaurantFavourite) {
                        filteredRestaurants.add(restaurant);
                    }


                    // Only one of the filters were set
                    else {
                        // Only HazardLevel filter set
                        if (recentInspectionHazardLevel.equals(filterHazardLevel) && filterNumCritIssues == -1 && !isFilterFavouriteChecked) {
                            filteredRestaurants.add(restaurant);
                        }
                        // Only numCritIssues <= filter set
                        else if (recentInspectionNumCritIssues <= filterNumCritIssues
                                && filterComparison.equals("<=")
                                && filterHazardLevel.equals("")
                                && !isFilterFavouriteChecked
                                && isRecentInspectionWithinYear) {
                            filteredRestaurants.add(restaurant);
                        }
                        // Only numCritIssues >= filter set
                        else if (recentInspectionNumCritIssues >= filterNumCritIssues
                                && filterComparison.equals(">=")
                                && filterHazardLevel.equals("")
                                && !isFilterFavouriteChecked
                                && isRecentInspectionWithinYear) {
                            filteredRestaurants.add(restaurant);
                        }
                        // Only favourites set
                        else if (isRestaurantFavourite == isFilterFavouriteChecked && filterNumCritIssues == -1 && filterHazardLevel.equals("")) {
                            filteredRestaurants.add(restaurant);
                        }
                    }
                }
            }



            else {
                // Removes case sensitive, and removes empty spaces
                String filteredPattern = constraint.toString().toLowerCase().trim();

                for (Restaurant restaurant:restaurantListFull) {
                    String restaurantName = restaurant.getRestaurantName().toLowerCase();

                    if (restaurantName.contains(filteredPattern)) {

                        // No filters set
                        if(!isFiltersChanged) {
                            filteredRestaurants.add(restaurant);
                        }

                        else {
                            // Setting up variables for filter check
                            Inspection recentInspection = restaurant.getRecentInspection();
                            boolean isRestaurantFavourite = restaurant.getFavourite();
                            //restaurantName = restaurant.getRestaurantName();
                            boolean isRecentInspectionWithinYear = true;
                            String recentInspectionHazardLevel = "";
                            int recentInspectionNumCritIssues = -1;

                            if(recentInspection != null) {
                                recentInspectionHazardLevel = recentInspection.getHazardRating();
                                recentInspectionNumCritIssues = recentInspection.getNumCritical();
                                isRecentInspectionWithinYear = recentInspection.getInspectionWithinYear();

                                if (recentInspectionHazardLevel.equals("")) {
                                    recentInspectionHazardLevel = LOW_HAZARD;
                                }
                            }

                            else {
                                recentInspectionHazardLevel = LOW_HAZARD;
                                recentInspectionNumCritIssues = 0;
                            }

                            // filterHazardLevel, filterNumCritIssues <=, filterFavourite were set
                            if(recentInspectionHazardLevel.equals(filterHazardLevel)
                                    && filterComparison.equals("<=")
                                    && recentInspectionNumCritIssues <= filterNumCritIssues
                                    && isRecentInspectionWithinYear
                                    && isFilterFavouriteChecked && isRestaurantFavourite)
                            {
                                filteredRestaurants.add(restaurant);
                            }

                            // filterHazardLevel, filterNumCritIssues >=, filterFavourite were set
                            else if(recentInspectionHazardLevel.equals(filterHazardLevel)
                                    && filterComparison.equals(">=")
                                    && recentInspectionNumCritIssues >= filterNumCritIssues
                                    && isRecentInspectionWithinYear
                                    && isFilterFavouriteChecked && isRestaurantFavourite)
                            {
                                filteredRestaurants.add(restaurant);
                            }

                            // Two filters were set
                            // fc = filterNumCritIssues
                            // hl = filterHazardLevel
                            // ff = filterFavourites
                            //fc <=,hl
                            else if (recentInspectionHazardLevel.equals(filterHazardLevel)
                                    && filterComparison.equals("<=")
                                    && recentInspectionNumCritIssues <= filterNumCritIssues
                                    && !isFilterFavouriteChecked
                                    && isRecentInspectionWithinYear ) {
                                filteredRestaurants.add(restaurant);
                            }
                            //ff,fc <=
                            else if (filterHazardLevel.equals("")
                                    && filterComparison.equals("<=")
                                    && recentInspectionNumCritIssues <= filterNumCritIssues
                                    && isRestaurantFavourite
                                    && isRecentInspectionWithinYear) {
                                filteredRestaurants.add(restaurant);
                            }
                            //fc >=,hl
                            else if (recentInspectionHazardLevel.equals(filterHazardLevel)
                                    && filterComparison.equals(">=")
                                    && recentInspectionNumCritIssues >= filterNumCritIssues
                                    && !isFilterFavouriteChecked
                                    && isRecentInspectionWithinYear ) {
                                filteredRestaurants.add(restaurant);
                            }
                            //ff,fc >=
                            else if (filterHazardLevel.equals("")
                                    && filterComparison.equals(">=")
                                    && recentInspectionNumCritIssues >= filterNumCritIssues
                                    && isRestaurantFavourite
                                    && isRecentInspectionWithinYear) {
                                filteredRestaurants.add(restaurant);
                            }
                            //ff,hl
                            else if (recentInspectionHazardLevel.equals(filterHazardLevel) &&
                                    filterNumCritIssues == -1
                                    && isRestaurantFavourite) {
                                filteredRestaurants.add(restaurant);
                            }

                            // Only one of the filters were set
                            else {
                                // Only HazardLevel filter set
                                if (recentInspectionHazardLevel.equals(filterHazardLevel) && filterNumCritIssues == -1 && !isFilterFavouriteChecked) {
                                    filteredRestaurants.add(restaurant);
                                }
                                // Only numCritIssues <= filter set
                                else if (recentInspectionNumCritIssues <= filterNumCritIssues
                                        && filterComparison.equals("<=")
                                        && filterHazardLevel.equals("")
                                        && !isFilterFavouriteChecked
                                        && isRecentInspectionWithinYear) {
                                    filteredRestaurants.add(restaurant);
                                }
                                // Only numCritIssues >= filter set
                                else if (recentInspectionNumCritIssues >= filterNumCritIssues
                                        && filterComparison.equals(">=")
                                        && filterHazardLevel.equals("")
                                        && !isFilterFavouriteChecked
                                        && isRecentInspectionWithinYear) {
                                    filteredRestaurants.add(restaurant);
                                }
                                // Only favourites set
                                else if (isRestaurantFavourite && isFilterFavouriteChecked && filterNumCritIssues == -1 && filterHazardLevel.equals("")) {
                                    filteredRestaurants.add(restaurant);
                                }
                            }
                        }
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredRestaurants;
            return results;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return filterData(constraint);
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            restaurantList.clear();
            restaurantList.addAll((List) results.values);
            addRestaurantPegsOnSearch();
            setupClusterRenderer();
        }
    };

}








