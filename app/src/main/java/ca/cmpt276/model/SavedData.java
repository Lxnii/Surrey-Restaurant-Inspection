package ca.cmpt276.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ca.cmpt276.restaurantapplication.MapsActivity;
import ca.cmpt276.restaurantapplication.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Saves data and loads saved data
 */

public class SavedData extends AsyncTask<Void, Void, Void> {

    private AnimatedVectorDrawableCompat loadingArrowAVDCompat;
    private AnimatedVectorDrawable loadingArrowAVD;
    private AlertDialog.Builder builder;
    private AlertDialog loadingAlert;
    private ImageView loadingArrow;

    private String regex = "\t";
    private String lastModifiedRestaurants="";
    private String lastModifiedInspections="";
    private String lastDownloadedData ="";
    private boolean lastUpdateCancelled = false;
    private static SharedPreferences locationPermission;
    private static boolean locationPermissionGranted = false;

    private ArrayList<Inspection> inspectionList = new ArrayList<>();
    private List<Restaurant> restaurantList = new ArrayList<>();
    private boolean savedDataLoaded;

    private Context context;
    private static MapsActivity activityParent;


    SavedData(Context context) {
        this.context = context;
        builder = new AlertDialog.Builder(context);
    }

    public static void setActivityParent(MapsActivity parent) {
        SavedData.activityParent = parent;
    }
    public void setArrow() {
        this.loadingArrow = activityParent.findViewById(R.id.dl_arrow);
    }
    public void setLastModifiedInspections(String lastModifiedInspections) {
        this.lastModifiedInspections = lastModifiedInspections;
    }
    public void setLastModifiedRestaurants(String lastModifiedRestaurants) {
        this.lastModifiedRestaurants = lastModifiedRestaurants;
    }
    public void setLastDownloadData(String lastDownloadedData) {
        this.lastDownloadedData = lastDownloadedData;
    }
    public void setLastUpdateCancelled(boolean lastUpdateCancelled) {
        this.lastUpdateCancelled = lastUpdateCancelled;
    }

    public String getLastModifiedInspections() {
        return lastModifiedInspections;
    }
    public String getLastModifiedRestaurants() {
        return lastModifiedRestaurants;
    }
    public String getLastDownloadedData() {
        return lastDownloadedData;
    }
    public boolean isLastUpdateCancelled() {
        return lastUpdateCancelled;
    }
    public boolean isSavedDataLoaded() {
        return savedDataLoaded;
    }

    public void setLastDownloadData(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        lastDownloadedData = format.format(today);
    }

    public static void setAndSaveLocationPermission(Boolean permissionGranted){
        locationPermissionGranted = permissionGranted;
        locationPermission = activityParent.getSharedPreferences("permission",MODE_PRIVATE);
        SharedPreferences.Editor editor = locationPermission.edit();
        editor.putBoolean("locationPermissionGranted",permissionGranted);
        editor.apply();
        Log.d("updatedData", "Permission Granted Saved value: "+locationPermissionGranted);
    }

    public static boolean isLocationPermissionGranted(){
        locationPermission = activityParent.getSharedPreferences("permission",MODE_PRIVATE);
        locationPermissionGranted = locationPermission.getBoolean("locationPermissionGranted",false);
        Log.d("updatedData", "Permission Granted Bool: "+locationPermissionGranted);
        return locationPermissionGranted;
    }

    public void saveLastDownloadInfo(){
        try {
            FileOutputStream saveLastDownloadFStream = context.openFileOutput(
                    "save_last_download", MODE_PRIVATE);
            saveLastDownloadFStream.write(lastDownloadDatesString().getBytes());
            Log.d("SavedData_Date", "saveLastDownloadInfo-Saving: "+ lastDownloadDatesString());
            saveLastDownloadFStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String lastDownloadDatesString(){
        return lastModifiedInspections +'\n' + lastModifiedRestaurants
                +'\n' + lastDownloadedData +'\n' + lastUpdateCancelled ;
    }

    public void loadLastDownloadInfo(){
        try {
            FileInputStream lastDownloadInfoFile = context.openFileInput("save_last_download");
            BufferedReader reader = inputStreamBReader(lastDownloadInfoFile);
            try {
                String line;
                String[] data = new String[4];
                int index = 0;

                while (((line = reader.readLine()) != null)){
                    data[index] = line;
                    index++;
                }
                lastModifiedInspections = data[0];
                lastModifiedRestaurants = data[1];
                lastDownloadedData = data[2];
                lastUpdateCancelled = data[3].equals("true");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("SavedData_Date","getLastDownloadInfo-readline Error"+e);
            }
            lastDownloadInfoFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("SavedData_Date","getLastDownloadInfo-file not found Error"+e);
        } catch (IOException e) {
            Log.d("SavedData_Date","getLastDownloadInfo-exception Error"+e);
        }
    }

    public void saveLists(){
        saveInspectionList();
        saveRestaurantList();
    }

    private void loadListsFromSave(){
        loadInspectionList();
        loadRestaurantList();
        prepListsForManager();
    }

    private void saveRestaurantList(){
        List<Restaurant> restaurants = RestaurantManager.getInstance().getRestaurantList();
        try {
            FileOutputStream saveRestaurantListFStream =
                    context.openFileOutput("save_restaurants", MODE_PRIVATE);

            writeRestaurantsToFile(restaurants, saveRestaurantListFStream);
            saveRestaurantListFStream.close();
        } catch (FileNotFoundException e) {
            Log.d("SavedData_Date", "saveRestaurantList-Error-"+e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeRestaurantsToFile(List<Restaurant> restaurants,
                                        FileOutputStream saveRestaurantListFStream) throws IOException {
        for (Restaurant restaurant: restaurants) {
            saveRestaurantListFStream.write(appendSingleRestaurantData(restaurant).getBytes());
            Log.d("SavedData_Date", "saveRestaurantList-Saving: "+
                    appendSingleRestaurantData(restaurant));
        }
    }

    private String appendSingleRestaurantData(Restaurant restaurant){
        return  restaurant.getTrackingNumber()+regex+
                restaurant.getRestaurantName()+regex+
                restaurant.getPhysicalAddress()+regex+
                restaurant.getPhysicalCity()+regex+
                restaurant.getFacilityType()+regex+
                restaurant.getLatitude()+regex+
                restaurant.getLongitude()+'\n';
    }

    private void loadRestaurantList(){
        try {
            Log.d("SavedData_Date", "loadRestaurantList: Load restaurant start");
            FileInputStream savedRestaurantsFile = context.openFileInput("save_restaurants");
            BufferedReader reader = inputStreamBReader(savedRestaurantsFile);
            readRestaurantData(reader);
            savedRestaurantsFile.close();
        } catch (FileNotFoundException e) {
            Log.d("SavedData_Date", "FileNotFoundException "+e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("SavedData_Date", "loadRestaurantList Load restaurant End: ");
    }

    private void readRestaurantData(BufferedReader reader) {
        try {
            String line = "";
            while (((line = reader.readLine()) != null)){
                Log.d("SavedData_Date","readRestaurantData-line: " + line);
                String[] tokens = line.split(regex);
                Log.d("SavedData_Date","readRestaurantData-length: " + tokens.length);

                Restaurant newRestaurant = new Restaurant();
                newRestaurant.setTrackingNumber(tokens[0]);

                if(!restaurantExists(newRestaurant)){
                    addRestaurantDataToList(newRestaurant,tokens);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean restaurantExists(Restaurant newRestaurant){
        RestaurantManager manager = RestaurantManager.getInstance();
        for (Restaurant restaurant: manager.getRestaurantList()) {
            if( trackingNumbersMatch(newRestaurant,restaurant) ){
                return true;
            }
        }
        return false;
    }

    private boolean trackingNumbersMatch(Restaurant newRestaurant, Restaurant restaurant){
        return newRestaurant.getTrackingNumber().equals(restaurant.getTrackingNumber());
    }
    //Addhere
    private void addRestaurantDataToList(Restaurant newRestaurant, String[] tokens){
        newRestaurant.setTrackingNumber(tokens[0]);
        newRestaurant.setName(tokens[1]);
        newRestaurant.setPhysicalAddress(tokens[2]);
        newRestaurant.setPhysicalCity(tokens[3]);
        newRestaurant.setFacilityType(tokens[4]);
        newRestaurant.setLatitude(Double.parseDouble(tokens[5]));
        newRestaurant.setLongitude(Double.parseDouble(tokens[6]));
        restaurantList.add(newRestaurant);


        Log.d("SavedData_Date","addRestaurantData-Data:    "+
                newRestaurant.getTrackingNumber());
    }

    private void saveInspectionList(){
        List<Inspection> inspections = RestaurantManager.getInstance().getInspectionList();
        try {
            FileOutputStream saveInspectionFStream =
                    context.openFileOutput("save_inspections", MODE_PRIVATE);

            writeInspectionsToFile(inspections, saveInspectionFStream);
            saveInspectionFStream.close();
        } catch (FileNotFoundException e) {
            Log.d("SavedData_Date", "saveInspectionList  "+e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeInspectionsToFile(List<Inspection> inspections,
                                        FileOutputStream saveInspectionFStream) throws IOException {
        for (Inspection inspection: inspections) {
            saveInspectionFStream.write(appendInspectionData(inspection).getBytes());
        }
    }

    private String appendInspectionData(Inspection inspection){
        return inspection.getTrackingNumber()+regex+
                inspection.getInspectionDate()+regex+
                inspection.getInspectionType()+regex+
                inspection.getNumCritical()+regex+
                inspection.getNumNonCritical()+regex+
                inspection.getViolLump()+regex+
                inspection.getHazardRating()+'\n';
    }

    private void loadInspectionList(){
        try {
            Log.d("SavedData_Date", "loadInspectionList-Load inspections");
            FileInputStream savedInspectionsFile = context.openFileInput("save_inspections");

            BufferedReader reader = inputStreamBReader(savedInspectionsFile);
            readInspectionData(reader);
            savedInspectionsFile.close();

        } catch (FileNotFoundException e) {
            Log.d("SavedData_Date", "loadInspectionList "+e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("SavedData_Date", "loadInspectionList-End: ");
    }

    private void readInspectionData(BufferedReader reader) {
        try {
            String line = "";
            while (((line = reader.readLine()) != null)){
                String[] tokens = line.split(regex);

                if(tokens.length>0){
                    Inspection newInspection = new Inspection();
                    setInspectionFields(newInspection,tokens);
                    if(!inspectionExists(newInspection)){
                        addNewInspectionFromURL(tokens,newInspection);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setInspectionFields(Inspection newInspection, String[] tokens){
        newInspection.setTrackingNumber(tokens[0]);
        newInspection.setInspectionDate(tokens[1]);
        newInspection.setInspectionType(tokens[2]);
        newInspection.setNumCritical(Integer.parseInt(tokens[3]));
        newInspection.setNumNonCritical(Integer.parseInt(tokens[4]));
    }

    private boolean inspectionExists(Inspection newInspection){
        for (Inspection inspection:RestaurantManager.getInstance().getInspectionList()) {
            if (inspectionTrackNumAndDateMatches(newInspection,inspection)) {
                return true;
            }
        }
        return false;
    }

    private boolean inspectionTrackNumAndDateMatches(Inspection newInspection, Inspection inspection){
        return inspection.getTrackingNumber().equals(newInspection.getTrackingNumber()) &&
                inspection.getInspectionDate().equals(newInspection.getInspectionDate());
    }

    private void addNewInspectionFromURL(String[] tokens, Inspection newInspection){
        if(tokens.length < 6){
            // Missing lump + rating
            newInspection.setHazardRating("");
            setEmptyViolLump(newInspection);
        }
        else if(tokens.length < 7){
            // Missing rating
            newInspection.setHazardRating("");
            if (tokens[5].length() > 0) {
                newInspection.setViolLump(tokens[5]);
                extractAndSetViolCode(newInspection);
            }
        }
        else{
            newInspection.setHazardRating(tokens[6]);
            if (tokens[5].length() > 0) {
                newInspection.setViolLump(tokens[5]);
                extractAndSetViolCode(newInspection);
            }
            else {
                setEmptyViolLump(newInspection);
            }
        }
        Log.d("SavedData","Adding   "+newInspection.getTrackingNumber()+" "
                +newInspection.getInspectionDate() +" "+ newInspection.getViolLump());
        inspectionList.add(newInspection);
    }

    private void setEmptyViolLump(Inspection newInspection){
        List<String> violation_list  = new ArrayList<>();
        newInspection.setCodeViolList(violation_list);
        newInspection.setViolLump("");
    }

    private void extractAndSetViolCode(Inspection newInspection){
        List<String> violation_list  = new ArrayList<>();
        String[] violLump = newInspection.getViolLump().split("\\|");

        for (String s : violLump) {
            if (s.length() > 0) {
                violation_list.add(s.substring(0, 3));
            }
        }
        newInspection.setCodeViolList(violation_list);
    }

    private BufferedReader inputStreamBReader(FileInputStream inputStream){
        BufferedInputStream reader_last_download = new BufferedInputStream(inputStream);
        return new BufferedReader(new InputStreamReader (reader_last_download, StandardCharsets.UTF_8));
    }

    public long getHoursPassedSinceLastFullDownload(){
        Log.d("SavedData_Date","getHoursPassedSinceLastFullDownload-lastDownloadedData String to convert: " + lastDownloadedData);
        Date date = convertStringToDate(lastDownloadedData);
        Log.d("SavedData_Date","getHoursPassedSinceLastFullDownload-DateToString: " + date.toString());
        Log.d("SavedData_Date","getHoursPassedSinceLastFullDownload - diffhours: " + getDifferenceHours(date));
        return getDifferenceHours(date);
    }

    private Date convertStringToDate(String date) {
        Log.d("SavedData_Date", "convertStringtoDate-Date is: " + date);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date newdate = new Date();
        try {
            newdate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newdate;
    }

    private long getDifferenceHours(Date date) {
        Date today = new Date();
        Log.d("SavedData_Date","getDifferenceHours-today string: " + today.toString());
        long diff = today.getTime() - date.getTime();
        return TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
    }

    // https://www.youtube.com/watch?v=EuZIU_5O63Y
    private void startLoadingAnimation(){
        Log.d("SavedData","Start ARROW-------->");
        activityParent.runOnUiThread(() -> {
            loadingArrow.setVisibility(View.VISIBLE);
            Log.d("SavedData","SHOW ARROW-------->");
            Drawable drawableArrow = loadingArrow.getDrawable();
            if(drawableArrow instanceof  AnimatedVectorDrawableCompat){
                loadingArrowAVDCompat = (AnimatedVectorDrawableCompat) drawableArrow;
                loadingArrowAVDCompat.start();
            }
            else if( drawableArrow instanceof AnimatedVectorDrawable){
                loadingArrowAVD = (AnimatedVectorDrawable) drawableArrow;
                loadingArrowAVD.start();
            }
        });
    }

    private void hideArrow(){
        loadingArrow.setVisibility(View.GONE);
    }

    private void setLoadingAlert(){
        setAlertLook(builder);
        activityParent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingAlert = builder.create();
                loadingAlert.show();
                loadingAlert.setCanceledOnTouchOutside(false);
            }
        });
    }

    private void setAlertLook(AlertDialog.Builder builder){
        builder.setTitle(R.string.loading);
        builder.setMessage(R.string.dataLoading);
        builder.setIcon(R.drawable.restaurant_icon);
    }

    private void setClickableOnMapFalse(){
        Log.d("Click SavedData","---------click False");
        ImageView mInfo = activityParent.findViewById(R.id.mapsReturnRestaurantList);
        ImageView mGpsBtn = activityParent.findViewById(R.id.mapsGetCurrentLocation);
        ImageView clickBlock = activityParent.findViewById(R.id.clickBlock);
        clickBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Click ","---------click set to false");
            }
        });
        activityParent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clickBlock.setVisibility(View.VISIBLE);
                mInfo.setEnabled(false);
                mGpsBtn.setEnabled(false);
            }
        });
    }

    private void prepListsForManager(){
        Log.d("DownloadData SavedData","prepDataManager start");
        if (updateExists()) {
            RestaurantManager manager = RestaurantManager.getInstance();
            cloneManagerListsToTempLists(manager);
            for (Restaurant restaurant : restaurantList) {
                restaurant.MatchRestaurantAndInspection(inspectionList, false);
            }
            manager.sortRestaurantsAlphabetically(restaurantList);
            savedDataLoaded = true;
        }
    }

    private boolean updateExists() {
        return !inspectionList.isEmpty() || !restaurantList.isEmpty();
    }

    private void cloneManagerListsToTempLists(RestaurantManager manager) {
        for (Restaurant restaurant: manager.getRestaurantList()) {
            restaurantList.add(restaurant.clone());
        }
        for (Inspection inspection: manager.getInspectionList()) {
            inspectionList.add(inspection.clone());
        }
    }

    private void loadDataToManager() {
        Log.d("DownloadData SavedData","loadDataToManager start");
        if(savedDataLoaded){
            RestaurantManager manager = RestaurantManager.getInstance();
            manager.setRestaurantList(restaurantList);
            manager.setInspectionList(inspectionList);
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d("SavedData","background");
        startLoadingAnimation();
        loadListsFromSave();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d("SavedData","onPostExecute");
        super.onPostExecute(aVoid);
        loadDataToManager();
        loadingAlert.dismiss();
        hideArrow();
    }

    @Override
    protected void onPreExecute() {
        Log.d("SavedData","onPreExecute");
        super.onPreExecute();
        setClickableOnMapFalse();
        setLoadingAlert();
    }
}
