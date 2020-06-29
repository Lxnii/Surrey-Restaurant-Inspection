package ca.cmpt276.restaurantapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import ca.cmpt276.model.Restaurant;
import ca.cmpt276.model.RestaurantManager;
import ca.cmpt276.restaurantapplication.adapters.InspectionAdapter;

/**
 * This activity displays the details of a single restaurant
 */
public class InspectionActivity extends AppCompatActivity {
    private static final String EXTRA_INSPECTION = "com.cmpt276.inspection";
    private static final String EXTRA_INSPECTION_RESTAURANT = "com.cmpt276.inspectionRestaurant";
    private static final String EXTRA_INSPECTION_MAPS_RESTAURANT_NAME = "com.cmpt276.inspectionMapsRestaurantName";
    private static final String EXTRA_INSPECTION_MAPS_RESTAURANT_ADDRESS = "com.cmpt276.inspectionMapsRestaurantAddress";
    private static final String EXTRA_INSPECTION_MAPS_RESTAURANT_HAZARD = "com.cmpt276.inspectionMapsRestaurantHazard";
    private static final String EXTRA_INSPECTION_MAPS_COORDS = "com.cmpt276.inspectionMapsCoordinates";
    private static final String EXTRA_INSPECTION_MAPS_COORDS_LATITUDE= "com.cmpt276.inspectionMapsCoordinatesLatitude";
    private static final String EXTRA_INSPECTION_MAPS_COORDS_LONGITUDE = "com.cmpt276.inspectionMapsCoordinatesLongitude";
    private static final String EXTRA_INSPECTION_MAPS_RESTAURANT_POSITION = "com.cmpt276.inspectionMapsRestaurantPosition";

    private Restaurant restaurant;
    private int restaurantIndex;
    RestaurantManager manager_restaurant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);

        Intent intent = getIntent();
        manager_restaurant = RestaurantManager.getInstance();

        // The Restaurant clicked from main recyclerview.
        restaurantIndex = MainActivity.getRestaurantClicked(intent);
        restaurant = manager_restaurant.getRestaurant(restaurantIndex);

        populateRecyclerView();
        setRestaurantTextViews();
    }


    // Instantiates InspectionAdapter
    private void populateRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.activity_inspection_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new InspectionAdapter(this, restaurant.getInspections(), restaurantIndex));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Sets the name, coords, address of the restaurant clicked.
    // https://stackoverflow.com/questions/34195828/how-to-come-back-to-first-activity-without-its-oncreate-called/43466522
    private void setRestaurantTextViews() {
        TextView display_restaurantName = findViewById(R.id.inspectionRestaurantNameValue);
        TextView display_address = findViewById(R.id.inspectionAddressValue);
        TextView display_gps_latitude = findViewById(R.id.inspectionLatitude);


        String restaurantName =  restaurant.getRestaurantName();
        String restaurantAddress = restaurant.getPhysicalAddress();
        String longtitude = Double.toString(restaurant.getLongitude());
        String latitude = Double.toString(restaurant.getLatitude());
        String concat_latitude_longituide= "("+latitude+" , "+longtitude+")";

        display_restaurantName.setText(restaurantName);
        display_address.setText(restaurantAddress);
        display_gps_latitude.setText(concat_latitude_longituide);

        display_gps_latitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MapsActivity.makeIntent(InspectionActivity.this);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                final String LOW_HAZARD = getResources().getString(R.string.hazard_level_low);

                String hazardRating = LOW_HAZARD; // Low hazard rating by default
                if (restaurant.getRecentInspection() != null) {
                    hazardRating = restaurant.getRecentInspection().getHazardRating();
                }
                intent.putExtra(EXTRA_INSPECTION_MAPS_COORDS, true);
                intent.putExtra(EXTRA_INSPECTION_MAPS_RESTAURANT_NAME, restaurant.getRestaurantName());
                intent.putExtra(EXTRA_INSPECTION_MAPS_RESTAURANT_ADDRESS, restaurant.getPhysicalAddress());
                intent.putExtra(EXTRA_INSPECTION_MAPS_RESTAURANT_HAZARD, hazardRating);
                intent.putExtra(EXTRA_INSPECTION_MAPS_COORDS_LATITUDE, restaurant.getLatitude());
                intent.putExtra(EXTRA_INSPECTION_MAPS_COORDS_LONGITUDE, restaurant.getLongitude());
                intent.putExtra(EXTRA_INSPECTION_MAPS_RESTAURANT_POSITION, restaurant.getRestaurantPosition());
                startActivity(intent);
                //finish();
            }
        });
    }


    // Gets a hazard icon
    //https://www.flaticon.com/free-icon/complain_1041891?term=exclamation%20mark&page=1&position=20 yellow_alert picture
    //https://www.hiclipart.com/free-transparent-background-png-clipart-iyhth green_alert picture
    //https://commons.wikimedia.org/wiki/File:GHS-pictogram-exclam.svg red_alert picture
    private int getHazardIcon(String hazardLevel) {
        int hazardIcon;

        final String MODERATE_HAZARD = getResources().getString(R.string.hazard_level_moderate);
        final String HIGH_HAZARD = getResources().getString(R.string.hazard_level_high);

        if(hazardLevel.equals(HIGH_HAZARD)) {
            hazardIcon = R.drawable.red_alert;
        }
        else if (hazardLevel.equals(MODERATE_HAZARD)) {
            hazardIcon = R.drawable.yellow_alert;
        }
        else {
            hazardIcon = R.drawable.green_alert;
        }

        return hazardIcon;
    }


    // Gets a hazard colour bar
    private int getColourBar(String hazardLevel) {
        int colourBar;

        final String MODERATE_HAZARD = getResources().getString(R.string.hazard_level_moderate);
        final String HIGH_HAZARD = getResources().getString(R.string.hazard_level_high);

        if(hazardLevel.equals(HIGH_HAZARD)) {
            colourBar = R.color.redBar;
        }
        else if (hazardLevel.equals(MODERATE_HAZARD)) {
            colourBar = R.color.orangeBar;
        }
        else {
            colourBar =  R.color.greenBar;
        }

        return colourBar;
    }


    /**
     * Creating app buttons for action bar in the inspection activity
     * Code adapted from 'John's Android Studio Tutorials':
     * https://www.youtube.com/watch?v=5MSKuVO2hV4
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inspection_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // Acquires the item selected in Action Bar.
        int id = item.getItemId();

        if(id == R.id.actionbar_back_inspectionActivity) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    // getExtra functions for Issue below:
    // Returns the restaurantIndex (used by the IssueActivity class)
    public static int getExtraInspectionIssueRestaurantIndex(Intent intent) {
        return intent.getIntExtra(EXTRA_INSPECTION_RESTAURANT, 0);
    }

    // Returns the inspection that is clicked in the recyclerview_inspections (used in IssueActivity)
    public static int getExtraInspectionIssueClicked(Intent intent) {
        return intent.getIntExtra(EXTRA_INSPECTION, 0);
    }


    // getExtra functions for MapsActivity below:
    // Returns the the boolean if coordinates were clicked and launches mapActivity
    public static boolean getExtraInspectionMapsCoordinates(Intent intent) {
        return intent.getBooleanExtra(EXTRA_INSPECTION_MAPS_COORDS, false);
    }

    public static String getExtraInspectionMapsRestaurantName(Intent intent) {
        return intent.getStringExtra(EXTRA_INSPECTION_MAPS_RESTAURANT_NAME);
    }

    public static String getExtraInspectionMapsRestaurantAddress(Intent intent) {
        return intent.getStringExtra(EXTRA_INSPECTION_MAPS_RESTAURANT_ADDRESS);
    }

    public static String getExtraInspectionMapsRestaurantHazard(Intent intent) {
        return intent.getStringExtra(EXTRA_INSPECTION_MAPS_RESTAURANT_HAZARD);
    }

    public static double getExtraInspectionMapsCoordinatesLatitude(Intent intent) {
        return intent.getDoubleExtra(EXTRA_INSPECTION_MAPS_COORDS_LATITUDE, 0.00);
    }

    public static double getExtraInspectionMapsCoordinatesLongitude(Intent intent) {
        return intent.getDoubleExtra(EXTRA_INSPECTION_MAPS_COORDS_LONGITUDE, 0.00);
    }

    public static int getExtraInspectionMapsRestaurantPosition(Intent intent) {
        return intent.getIntExtra(EXTRA_INSPECTION_MAPS_RESTAURANT_POSITION, 0);
    }
    public static Intent makeIntent(Context context) {
        return new Intent(context, InspectionActivity.class);
    }
}
