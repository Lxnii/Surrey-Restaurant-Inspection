package ca.cmpt276.getdata;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.model.Inspection;
import ca.cmpt276.model.Restaurant;
import ca.cmpt276.model.RestaurantManager;

/**
 * Reads the CSV data obtained from the url from metadata
 */

public class ReadNewData {

    private ArrayList<Inspection> inspectionList = new ArrayList<>();
    private List<Restaurant> restaurantList = new ArrayList<>();
    private boolean newData;

    public void prepTempListsForManager(){
        Log.d("DownloadData Readdata","prepDataManager start");
        if (updateAvailable()) {
            RestaurantManager manager = RestaurantManager.getInstance();
            cloneManagerListsToTempLists(manager);
            for (Restaurant restaurant : restaurantList) {
                restaurant.MatchRestaurantAndInspection(inspectionList, true);
            }
            manager.sortRestaurantsAlphabetically(restaurantList);
            newData = true;
        }
    }

    private boolean updateAvailable() {
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

    public void loadDataToManager() {
        if(newData){
            Log.d("DownloadData Readdata","loadDataToManager start");
            RestaurantManager manager = RestaurantManager.getInstance();
            manager.setInspectionList(inspectionList);
            manager.setRestaurantList(restaurantList);
        }
    }

    public void readRestaurantDataFromURL(BufferedReader reader) {
        String line;
        try {
            //step over header
            reader.readLine();
            while (((line = reader.readLine()) != null)){
                String regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
                String[] tokens = line.split(regex);
                removeApostrophes(tokens);

                Restaurant newRestaurant = new Restaurant();
                newRestaurant.setTrackingNumber(tokens[0]);
                if(!restaurantExists(newRestaurant)){
                    addRestaurantData(newRestaurant, tokens);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean restaurantExists(Restaurant newRestaurant){
        for (Restaurant res: RestaurantManager.getInstance().getRestaurantList()) {
            if(newRestaurant.getTrackingNumber().equals(res.getTrackingNumber())){
                return true;
            }
        }
        return false;
    }

    private void addRestaurantData(Restaurant newRestaurant, String[] tokens){
        newRestaurant.setTrackingNumber(tokens[0]);
        newRestaurant.setName(tokens[1]);
        newRestaurant.setPhysicalAddress(tokens[2]);
        newRestaurant.setPhysicalCity(tokens[3]);
        newRestaurant.setFacilityType(tokens[4]);
        newRestaurant.setLatitude(Double.parseDouble(tokens[5]));
        newRestaurant.setLongitude(Double.parseDouble(tokens[6]));

        restaurantList.add(newRestaurant);
        Log.d("ReadData","Adding Restaurant to temp list"+ newRestaurant.getTrackingNumber());
    }

    public void readInspectionDataFromURL(BufferedReader reader) {
        String line = "";
        try {
            // Step over header
            reader.readLine();
            while (((line = reader.readLine()) != null)){
                String regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
                String[] tokens = line.split(regex);
                removeApostrophes(tokens);
                if(tokens.length>0){
                    Inspection newInspection = new Inspection();
                    setInspectionFields(newInspection,tokens);
                    if(!inspectionExists(newInspection)){
                        Log.d("ReadData", "Adding Inspection to temp list");
                        addInspectionData(tokens,newInspection);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // hazard level + viol lump not included
    // data from url has them in a different order compared to pre-installed
    private void setInspectionFields(Inspection newInspection, String[] tokens){
        newInspection.setTrackingNumber(tokens[0]);
        newInspection.setInspectionDate(tokens[1]);
        newInspection.setInspectionType(tokens[2]);
        newInspection.setNumCritical(Integer.parseInt(tokens[3]));
        newInspection.setNumNonCritical(Integer.parseInt(tokens[4]));
    }

    private boolean inspectionExists(Inspection newInspection){
        for (Inspection inspection:RestaurantManager.getInstance().getInspectionList()) {
            if (inspection.getTrackingNumber().equals(newInspection.getTrackingNumber())
                    && inspection.getInspectionDate().equals(newInspection.getInspectionDate())) {
                return true;
            }
        }
        return false;
    }

    private void addInspectionData(String[] tokens, Inspection newInspection){
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
        Log.d("ReadNewData","Adding   "+newInspection.getTrackingNumber()+" "
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
        String[] viol = newInspection.getViolLump().split("\\|");
        for (String s : viol) {
            if (s.length() > 0) {
                violation_list.add(s.substring(0, 3));
            }
        }
        newInspection.setCodeViolList(violation_list);
    }

    private void removeApostrophes(String[] tokens){
        for(int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].replaceAll("^\"|\"$", "");
        }
    }
}
