package ca.cmpt276.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Stores information about a restaurant
 */
public class Restaurant {
    private String trackingNumber;
    private String name;
    private String physicalAddress;
    private String physicalCity;
    private String facilityType;
    private Double latitude;
    private Double longitude;
    private boolean isFavourite;
    // Position in the recyclerView
    private int restaurantPosition;
    private ArrayList<Inspection> inspections;


    public Restaurant() {
        inspections = new ArrayList<>();
    }


    public void setInspections(ArrayList<Inspection> inspections) {
        this.inspections = inspections;
    }
    public void setTrackingNumber(String trackingNumber) {
        if (RestaurantManager.getInstance().getFavouriteRestaurantsTrackingNum().contains(trackingNumber)) {
            // Set the status of this restaurant to true
            this.setFavourite(true);
        }
        this.trackingNumber = trackingNumber;
    }
    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPhysicalCity(String physicalCity) {
        this.physicalCity = physicalCity;
    }
    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public void setRestaurantPosition(int position) {
        this.restaurantPosition = position;
    }
    public void setFavourite (boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    public boolean getFavourite() {
        return isFavourite;
    }
    public String getTrackingNumber() {
        return trackingNumber;
    }
    public String getRestaurantName() {
        return name;
    }
    public String getPhysicalAddress() {
        return physicalAddress;
    }
    public String getPhysicalCity() {
        return physicalCity;
    }
    public String getFacilityType() {
        return facilityType;
    }
    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }

    // Needed to get the inspection of that restaurant when the info window
    // is clicked MapActivity
    public int getRestaurantPosition() {
        return restaurantPosition;
    }
    public ArrayList<Inspection> getInspections() {
        return inspections;
    }


    public Inspection getRecentInspection(){
        int size = inspections.size();

        if (size == 0) {
            return null;
        }
        else {
            return inspections.get(0);
        }

    }
    public Inspection getInspection(int pos)
    {
        return inspections.get(pos);
    }

    public void MatchRestaurantAndInspection(ArrayList<Inspection> storedInspection, boolean newUpdate) {
        ArrayList<Inspection> newInspections = new ArrayList<>();

        int oldRecentInspectionDate = 0;
        int newRecentInspectionDate = 0;
        for (Inspection inspection:storedInspection) {
            if (inspection.getTrackingNumber().equals(trackingNumber)){
                newInspections.add(inspection);
            }
        }

        if(newUpdate) {
            // Check if the restaurant was marked as favourite
            for(String trackingNum: RestaurantManager.getInstance().getFavouriteRestaurantsTrackingNum()) {
                if (this.getTrackingNumber().equals(trackingNum) && this.getRecentInspection() != null) {
                    try {
                        oldRecentInspectionDate = Integer.parseInt(this.getRecentInspection().getInspectionDate());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        inspections = newInspections;
        sortInspectionsDates();

        try {
            newRecentInspectionDate = Integer.parseInt(this.getRecentInspection().getInspectionDate());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Check if the oldInspection is later than the newRecent inspection
        if(newRecentInspectionDate > oldRecentInspectionDate && oldRecentInspectionDate != 0) {
            RestaurantManager.getInstance().getFavouriteRestaurants().add(this);
        }

    }

    // Sorted in descending order dates.
    // https://dev.to/codebyamir/sort-a-list-of-objects-by-field-in-java-3coj
    public void sortInspectionsDates() {
        Collections.sort(inspections, new Comparator<Inspection>() {
            @Override
            public int compare(Inspection o1, Inspection o2) {
                return o2.getInspectionDate().compareTo(o1.getInspectionDate());
            }
        });
    }

    public Restaurant clone(){
        Restaurant cloned = new Restaurant();
        cloned.setTrackingNumber(trackingNumber);
        cloned.setName(name);
        cloned.setPhysicalAddress(physicalAddress);
        cloned.setPhysicalCity(physicalCity);
        cloned.setFacilityType(facilityType);
        cloned.setLatitude(latitude);
        cloned.setLongitude(longitude);
        cloned.setRestaurantPosition(restaurantPosition);
        cloned.setInspections(inspections);
        return cloned;
    }

}
