package ca.cmpt276.model;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import ca.cmpt276.restaurantapplication.R;

/**
 *  Singleton model that stores a collection of restaurants
 */
public class RestaurantManager implements Iterable<Restaurant> {
    private List<Restaurant> restaurantList;
    private List<String> favouriteRestaurantsTrackingNum;
    private List<Restaurant> favouriteRestaurants;
    private ArrayList<Inspection> inspectionList ;
    private ArrayList<Issue> issueList;
    private static RestaurantManager instance;
    private Hashtable<Integer,String> violTypesList;
    private static Context context;
    private boolean isDataLoaded;
    private boolean isDataUpdated;
    private SavedData savedData;

    private RestaurantManager() {
        favouriteRestaurantsTrackingNum = new ArrayList<>();
        favouriteRestaurants = new ArrayList<>();
        loadFavouriteRestaurants();
        restaurantList = new ArrayList<>();
        inspectionList = new ArrayList<>();
        issueList = new ArrayList<>();
        violTypesList = new Hashtable<>();
        inputDataToViolTypeList();
        savedData = new SavedData(context);
    }

    public static RestaurantManager getInstance() {
        if (instance == null) {
            instance = new RestaurantManager();
        }
        return instance;
    }

    public static void setInstance(RestaurantManager instance) {
        RestaurantManager.instance = instance;
    }
    public void setRestaurantList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }
    public void setInspectionList(ArrayList<Inspection> inspectionList) {
        this.inspectionList = inspectionList;
    }
    public void setIssueList(ArrayList<Issue> issueList) {
        this.issueList = issueList;
    }
    public void setIsDataLoaded(boolean isDataLoaded) {
        this.isDataLoaded = isDataLoaded;
    }
    public void setDataUpdated(boolean dataUpdated) {
        isDataUpdated = dataUpdated;
    }
    public void setSavedData(SavedData savedData) {
        this.savedData = savedData;
    }

    public ArrayList<Inspection> getInspectionList() {
        return inspectionList;
    }
    public List<Restaurant> getFavouriteRestaurants() {
        return favouriteRestaurants;
    }
    public List<String> getFavouriteRestaurantsTrackingNum() {
        return favouriteRestaurantsTrackingNum;
    }
    public List<Restaurant> getRestaurantList() {
        return this.restaurantList;
    }
    public Restaurant getRestaurant(int index) {
        return  restaurantList.get(index);
    }
    public ArrayList<Issue> getIssueList() {
        return issueList;
    }
    public boolean checkDataLoaded(){
        return isDataLoaded;
    }
    public boolean isDataUpdated() {
        return isDataUpdated;
    }
    public SavedData getSavedData() {
        return savedData;
    }

    // https://dev.to/codebyamir/sort-a-list-of-objects-by-field-in-java-3coj
    public void sortRestaurantsAlphabetically(List<Restaurant>restaurants) {
        Collections.sort(restaurants, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant o1, Restaurant o2) {
                return o1.getRestaurantName().compareTo(o2.getRestaurantName());
            }
        });
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return restaurantList.iterator();
    }

    private void removeApostrophes(String[] tokens){
        for(int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].replaceAll("^\"|\"$", "");
        }
    }

    // Below are read data methods
    // https://stackoverflow.com/questions/43055661/reading-csv-file-in-android-app
    public void readRestaurantData(BufferedReader reader) {
        String line = "";
        try {
            //step over header
            reader.readLine();
            while (((line = reader.readLine()) != null)){
                String regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
                String[] tokens = line.split(regex);
                removeApostrophes(tokens);

                Restaurant newRestaurant = new Restaurant();
                addRestaurantData(newRestaurant,tokens);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addRestaurantData(Restaurant newRestaurant, String[] tokens){
        newRestaurant.setTrackingNumber(tokens[0]);
        newRestaurant.setName(tokens[1]);
        newRestaurant.setPhysicalAddress(tokens[2]);
        newRestaurant.setPhysicalCity(tokens[3]);
        newRestaurant.setFacilityType(tokens[4]);
        newRestaurant.setLatitude(Double.parseDouble(tokens[5]));
        newRestaurant.setLongitude(Double.parseDouble(tokens[6]));
        newRestaurant.MatchRestaurantAndInspection(inspectionList, false);
        restaurantList.add(newRestaurant);
    }

    public void readInspectionData(BufferedReader reader) {
        String line;
        try {
            // Step over header
            reader.readLine();
            while (((line = reader.readLine()) != null)){
                String regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
                String[] tokens = line.split(regex);
                removeApostrophes(tokens);

                Inspection newInspection = new Inspection();
                setInspectionFields(newInspection,tokens);

                if (tokens.length >= 7 && tokens[6].length() > 0) {
                    newInspection.setViolLump(tokens[6]);
                    extractAndSetViolCode(newInspection);
                    inspectionList.add(newInspection);
                }
                else {
                    setEmptyViolLump(newInspection);
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
        newInspection.setHazardRating(tokens[5]);
    }

    private void extractAndSetViolCode(Inspection newInspection){
        List<String> violation_list  = new ArrayList<>();
        String[] viol = newInspection.getViolLump().split("\\|");

        for (String s : viol) {
            if (s.length() > 0) {
                violation_list.add(s.substring(0, 3));
            }
        }
        newInspection.setCodeViolList(violation_list);
    }

    private void setEmptyViolLump(Inspection newInspection){
        List<String> violation_list  = new ArrayList<>();
        newInspection.setCodeViolList(violation_list);
        newInspection.setViolLump("");
        inspectionList.add(newInspection);
    }

    // Reads all possible violations from all_violations.txt in /raw folder
    public void readViolationData(BufferedReader reader) {
        String line = "";
        try {
            //step over header
            reader.readLine();
            while (((line = reader.readLine()) != null)){
                String[] tokens = line.split(",");
                Issue issue_sample = new Issue();
                issue_sample.setIssueCode(Integer.parseInt(tokens[0]));
                issue_sample.setWhetherCritical(tokens[1]);
                issue_sample.setDescription(tokens[2]);
                issue_sample.setWhetherRepeat(tokens[3]);
                issueList.add(issue_sample);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setStaticContext(Context context) {
        RestaurantManager.context = context;
        Inspection.setStaticContext(context);
        Issue.setStaticContext(context);
    }

    public void  setInspectionStaticIssueList(){
        matchIssueCodesForViolType();
        Inspection.setStaticIssueList(issueList);
    }

    private void matchIssueCodesForViolType(){
        for (Issue issue:issueList) {
            issue.getDescriptionAndTypeById(violTypesList,context);
        }
    }

    // Read the saved data file and insert the favourites to RestaurantManager favouriteList
    private BufferedReader inputStreamBReader(FileInputStream inputStream){
        BufferedInputStream reader_last_download = new BufferedInputStream(inputStream);
        return new BufferedReader(new InputStreamReader(reader_last_download, StandardCharsets.UTF_8));
    }

    private void loadFavouriteRestaurants(){
        try {
            Log.d("SavedData_Date", "loadFavouriteRestaurantList: Load restaurant start");
            FileInputStream saveFavouriteRestaurants =
                    context.openFileInput("save_favourite_restaurants");
            BufferedReader reader = inputStreamBReader(saveFavouriteRestaurants);
            readFavouriteRestaurant(reader);
            saveFavouriteRestaurants.close();
        } catch (FileNotFoundException e) {
            Log.d("SavedData_Date", "FileNotFoundException "+e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("SavedData_Date", "loadFavouriteRestaurantList Load restaurant End: ");
    }

    private void readFavouriteRestaurant(BufferedReader reader) {
        try {
            String line = "";
            while (((line = reader.readLine()) != null)){
                Log.d("SavedData_Dates","readFavouriteRestaurantData-line: " + line);
                favouriteRestaurantsTrackingNum.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void inputDataToViolTypeList(){
        violTypesList.put(101,context.getString(R.string.violation_type_building));
        violTypesList.put(102,context.getString(R.string.violation_type_permit));
        violTypesList.put(103,context.getString(R.string.violation_type_permit));
        violTypesList.put(104,context.getString(R.string.violation_type_permit));
        violTypesList.put(201,context.getString(R.string.violation_type_food));
        violTypesList.put(202,context.getString(R.string.violation_type_food));
        violTypesList.put(203,context.getString(R.string.violation_type_food));
        violTypesList.put(204,context.getString(R.string.violation_type_food));
        violTypesList.put(205,context.getString(R.string.violation_type_food));
        violTypesList.put(206,context.getString(R.string.violation_type_food));
        violTypesList.put(208,context.getString(R.string.violation_type_food));
        violTypesList.put(209,context.getString(R.string.violation_type_food));
        violTypesList.put(210,context.getString(R.string.violation_type_food));
        violTypesList.put(211,context.getString(R.string.violation_type_food));
        violTypesList.put(212,context.getString(R.string.violation_type_staff));
        violTypesList.put(301,context.getString(R.string.violation_type_equipment));
        violTypesList.put(302,context.getString(R.string.violation_type_equipment));
        violTypesList.put(303,context.getString(R.string.violation_type_equipment));
        violTypesList.put(304,context.getString(R.string.violation_type_pests));
        violTypesList.put(305,context.getString(R.string.violation_type_pests));
        violTypesList.put(306,context.getString(R.string.violation_type_building));
        violTypesList.put(307,context.getString(R.string.violation_type_equipment));
        violTypesList.put(308,context.getString(R.string.violation_type_equipment));
        violTypesList.put(309,context.getString(R.string.violation_type_equipment));
        violTypesList.put(310,context.getString(R.string.violation_type_equipment));
        violTypesList.put(311,context.getString(R.string.violation_type_building));
        violTypesList.put(312,context.getString(R.string.violation_type_equipment));
        violTypesList.put(313,context.getString(R.string.violation_type_pests));
        violTypesList.put(314,context.getString(R.string.violation_type_staff));
        violTypesList.put(315,context.getString(R.string.violation_type_equipment));
        violTypesList.put(401,context.getString(R.string.violation_type_equipment));
        violTypesList.put(402,context.getString(R.string.violation_type_staff));
        violTypesList.put(403,context.getString(R.string.violation_type_staff));
        violTypesList.put(404,context.getString(R.string.violation_type_staff));
        violTypesList.put(501,context.getString(R.string.violation_type_staff));
        violTypesList.put(502,context.getString(R.string.violation_type_staff));
    }
}
