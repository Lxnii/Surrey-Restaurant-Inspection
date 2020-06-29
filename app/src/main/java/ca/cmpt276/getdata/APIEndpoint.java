package ca.cmpt276.getdata;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Stores endpoints for access to data
 */

public interface APIEndpoint {
    @GET("api/3/action/package_show?id=restaurants")
    Call<URLData> getRestaurants();


    @GET("api/3/action/package_show?id=fraser-health-restaurant-inspection-reports")
    Call<URLData> getInspections();
}
