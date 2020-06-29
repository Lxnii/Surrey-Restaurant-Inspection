package ca.cmpt276.getdata;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

import ca.cmpt276.model.RestaurantManager;
import ca.cmpt276.model.SavedData;
import ca.cmpt276.restaurantapplication.MapsActivity;
import ca.cmpt276.restaurantapplication.R;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Gets meta data from url
 */

public class GetMetaData extends AsyncTask<Void, Void, Void>{

    private static final String DATA_URL = "https://data.surrey.ca/";
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(DATA_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private APIEndpoint APIEndpoint = retrofit.create(APIEndpoint.class);

    private AlertDialog.Builder alertBuilder;
    private AlertDialog downloadAlert;

    private boolean newInspections;
    private boolean newRestaurants;
    private boolean hasNetworkConnection;

    private URLData restaurantData;
    private URLData inspectionData;

    private Context context;
    private MapsActivity mapsActivity;


    public GetMetaData(Context context, MapsActivity mapsActivity, boolean hasNetworkConnection) {
        this.context = context;
        alertBuilder = new AlertDialog.Builder(context);
        this.mapsActivity = mapsActivity;
        this.hasNetworkConnection = hasNetworkConnection;
    }


    // https://guides.codepath.com/android/consuming-apis-with-retrofit
    private void getMetadataResponses() {
        Log.d("getMetaData", "getResults-start");
        if(hasNetworkConnection){
            Call<URLData> restaurantCall = APIEndpoint.getRestaurants();
            Response<URLData> restaurantDataResponse = getResponse(restaurantCall);

            Call<URLData> inspectionCall = APIEndpoint.getInspections();
            Response<URLData> inspectionDataResponse = getResponse(inspectionCall);

            assert restaurantDataResponse != null;
            assert inspectionDataResponse != null;
            restaurantData = restaurantDataResponse.body();
            inspectionData = inspectionDataResponse.body();
            if(updateAvailable()){
                setUpdateAvailableAlert();
            }
        }
        else{
            Log.d("getMetaData", "No network connection");
        }
    }

    private Response<URLData> getResponse(Call<URLData> call){
        Log.d("getAPI", "getResponse");
        Response<URLData> response = null;
        try {
            response = call.execute();
            Log.d("getAPI", "getResponse-success " );
        } catch (IOException e) {
            Log.d("getAPI", "getResponse-failed " + e);
        }
        assert response != null;
        if (!response.isSuccessful()) {
            Log.d("getAPI","getResponse-Fail: "+ response.code());
            return null;
        }
        return response;
    }

    private boolean updateAvailable(){
        Log.d("GetAPI", "checkUpdateAvailable--checkUpdate--");
        SavedData savedData = RestaurantManager.getInstance().getSavedData();
        if(previousDownloadDataExists(savedData)){
            if(savedData.getHoursPassedSinceLastFullDownload() > 20
                    || savedData.isLastUpdateCancelled()){
                Log.d("GetAPI", "checkUpdateAvailable--greater than 20hrs||" +
                        " updateCancelled "+ savedData.getHoursPassedSinceLastFullDownload()+
                        "  " + savedData.isLastUpdateCancelled() );
                checkFileUpdateDateMatchesSavedData(savedData);
            }
        }
        else{
            Log.d("GetAPI", "checkUpdateAvailable--checkUpdate-- Load All");
            newRestaurants = true;
            newInspections = true;
        }
        if(noUpdates()){
            Log.d("GetAPI", "checkUpdateAvailable--no updates--");
            setClickableTrue();
            return false;
        }
        else{
            Log.d("GetAPI", "checkUpdateAvailable--Updateavailablealert--");
            return true;
        }
    }

    private boolean previousDownloadDataExists(SavedData savedData){
        return !savedData.getLastDownloadedData().isEmpty();
    }

    private void checkFileUpdateDateMatchesSavedData(SavedData savedData) {
        if(isRestaurantFileUpdated(savedData)){
            newRestaurants = true;
        }
        if(isInspectionFileUpdated(savedData)){
            newInspections = true;
        }
    }

    private boolean isRestaurantFileUpdated(SavedData savedData){
        return !restaurantData.getResult().getMetadataModified()
                .equals(savedData.getLastModifiedRestaurants());
    }

    private boolean isInspectionFileUpdated(SavedData savedData){
        return !inspectionData.getResult().getMetadataModified()
                .equals(savedData.getLastModifiedInspections());
    }

    private boolean noUpdates() {
        return !newRestaurants && !newInspections;
    }

    private void setClickableTrue(){
        Log.d("Click Getmeta","---------click true");
        ImageView mInfo = mapsActivity.findViewById(R.id.mapsReturnRestaurantList);
        ImageView mGpsBtn = mapsActivity.findViewById(R.id.mapsGetCurrentLocation);
        ImageView clickBlock = mapsActivity.findViewById(R.id.clickBlock);
        mapsActivity.runOnUiThread(() -> {
            clickBlock.setVisibility(View.GONE);
            mInfo.setEnabled(true);
            mGpsBtn.setEnabled(true);
        });
    }

    private void setUpdateAvailableAlert(){
        setAlertLook();
        setAlertButtons();
        mapsActivity.runOnUiThread(() -> {
            downloadAlert = alertBuilder.create();
            downloadAlert.show();
            downloadAlert.setCanceledOnTouchOutside(false);
        });
    }

    private void setAlertLook(){
        alertBuilder.setTitle(R.string.updateAvailable);
        alertBuilder.setMessage(R.string.updateAvailableMsg);
        alertBuilder.setIcon(R.drawable.restaurant_icon);
    }

    private void setAlertButtons(){
        DialogInterface.OnClickListener refuseUpdateListener = (dialog, which) -> {
            downloadAlert.dismiss();
            setClickableTrue();
            if(isSavedDataLoaded()){
                updateMap();
            }
        };
        DialogInterface.OnClickListener acceptUpdateListener = (dialog, which) -> {
            downloadAlert.dismiss();
            startDownload();
        };
        alertBuilder.setNegativeButton(R.string.yes, acceptUpdateListener);
        alertBuilder.setPositiveButton(R.string.no, refuseUpdateListener);
    }

    private boolean isSavedDataLoaded() {
        return RestaurantManager.getInstance().getSavedData().isSavedDataLoaded();
    }

    private void startDownload(){
        startLoadingFromURL(this.restaurantData, this.inspectionData);
    }

    private void startLoadingFromURL(URLData restaurantData, URLData inspectionData){
        DownloadDataFromURL newDownload = new DownloadDataFromURL(context,
                newInspections, newRestaurants, inspectionData, restaurantData);
        newDownload.execute();
    }

    //https://stackoverflow.com/questions/32426588/delete-old-activity-instance-when-starting-a-new-activity
    private void updateMap(){
        Intent newMap = new Intent(mapsActivity,MapsActivity.class);
        newMap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mapsActivity.startActivity(newMap);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d("GetMetaData", "Execute bkg");
        getMetadataResponses();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d("GetMetaData", "Post Execute");
        RestaurantManager.getInstance().setDataUpdated(true);
        if(!hasNetworkConnection){
            setClickableTrue();
        }
        if(noUpdates() && isSavedDataLoaded()){
            Log.d("GetMetaData", "Alert will not appear/no new updates");
            updateMap();
        }
    }
}
