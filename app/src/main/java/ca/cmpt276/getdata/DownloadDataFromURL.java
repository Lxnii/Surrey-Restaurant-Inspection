package ca.cmpt276.getdata;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import ca.cmpt276.model.RestaurantManager;

import ca.cmpt276.model.SavedData;
import ca.cmpt276.restaurantapplication.MapsActivity;
import ca.cmpt276.restaurantapplication.R;

/**
 * Downloads and reads data from URL
 */

public class DownloadDataFromURL extends AsyncTask<Void, Void, Void>{

    private ReadNewData dataReader = new ReadNewData();

    private AlertDialog.Builder alertBuilder;
    private AlertDialog downloadAlert;
    private AnimatedVectorDrawableCompat downloadArrowAVDCompat;
    private AnimatedVectorDrawable downloadArrowAVD;
    private ImageView downloadArrow;

    private boolean newInspectionsAvailable;
    private boolean newRestaurantsAvailable;
    private URLData inspectionData;
    private URLData restaurantData;

    private static MapsActivity mapsActivity;


    public DownloadDataFromURL(Context context,
                               boolean newInspectionsAvailable, boolean newRestaurantsAvailable,
                               URLData inspectionData, URLData restaurantData) {
        alertBuilder = new AlertDialog.Builder(context);
        this.newInspectionsAvailable = newInspectionsAvailable;
        this.newRestaurantsAvailable = newRestaurantsAvailable;
        this.inspectionData = inspectionData;
        this.restaurantData = restaurantData;
        downloadArrow = DownloadDataFromURL.mapsActivity.findViewById(R.id.dl_arrow);
    }

    public static void setMapsActivity(MapsActivity mapsActivity) {
        DownloadDataFromURL.mapsActivity = mapsActivity;
    }

    private void readDataToTempLists(){
        if(newInspectionsAvailable){
            URL inspectionsCsvUrl = getURL(inspectionData);
            BufferedReader reader_restaurant = makeBufferedReader(inspectionsCsvUrl);
            dataReader.readInspectionDataFromURL(reader_restaurant);
        }

        if(newRestaurantsAvailable && !this.isCancelled()){
            URL restaurantCsvUrl = getURL(restaurantData);
            BufferedReader reader_restaurant = makeBufferedReader(restaurantCsvUrl);
            dataReader.readRestaurantDataFromURL(reader_restaurant);
        }
        if(!this.isCancelled()){
            dataReader.prepTempListsForManager();
        }
    }

    private URL getURL(URLData data){
        String url = data.getResult().getResources().get(0).getUrl();
        URL newURL = null;
        try {
            newURL = new URL(url);
        } catch (MalformedURLException e) {
            Log.d("DownloadData Error","stringToURL Error: " + e);
        }
        return newURL;
    }

    private BufferedReader makeBufferedReader(URL url){
        InputStream Restaurant_is = null;
        try {
            Restaurant_is = makeConnection(url).getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert Restaurant_is != null;
        return new BufferedReader(new InputStreamReader(Restaurant_is, StandardCharsets.UTF_8));
    }

    // https://stackoverflow.com/questions/8142335/download-csv-in-android
    private URLConnection makeConnection(URL url){
        Log.d("DownloadData","startConnection-" + url);
        URLConnection fromURL= null;
        try {
            fromURL = url.openConnection();
            fromURL.connect();
            Log.d("DownloadData","startConnection-" + fromURL);
            return fromURL;
        } catch (IOException e) {
            Log.d("DownloadData","startConnection-error:" + e);
        }
        return fromURL;
    }

    private void downloadAlert(){
        setAlertLook();
        setAlertButton();
        mapsActivity.runOnUiThread(() -> {
            downloadAlert = alertBuilder.create();
            downloadAlert.show();
            downloadAlert.setCanceledOnTouchOutside(false);
        });
    }

    private void setAlertLook(){
        alertBuilder.setTitle(R.string.downloading);
        alertBuilder.setMessage(R.string.downloadingwait);
        alertBuilder.setIcon(R.drawable.restaurant_icon);
    }

    private void setAlertButton(){
        DialogInterface.OnClickListener cancelListener = (dialog, which) -> {
            Log.d("DownloadData", "Cancel button clicked");
            this.cancel(true);
            hideArrow();
        };
        alertBuilder.setPositiveButton(R.string.downloadingcancel, cancelListener);
    }

    // https://www.youtube.com/watch?v=EuZIU_5O63Y
    private void downloadAnimation(){
        mapsActivity.runOnUiThread(() -> {
            Log.d("DownloadData","Show ARROW-------->");
            downloadArrow.setVisibility(View.VISIBLE);
            Drawable drawableArrow = downloadArrow.getDrawable();
            if(drawableArrow instanceof  AnimatedVectorDrawableCompat){
                downloadArrowAVDCompat = (AnimatedVectorDrawableCompat) drawableArrow;
                downloadArrowAVDCompat.start();
            }
            else if( drawableArrow instanceof AnimatedVectorDrawable){
                downloadArrowAVD = (AnimatedVectorDrawable) drawableArrow;
                downloadArrowAVD.start();
            }
        });
    }

    private void hideArrow(){
        downloadArrow.setVisibility(View.GONE);
    }

    private void saveDataOnSuccess(){
        Log.d("DownloadData", "saveDataOnSuccess--SAVING DATA--");
        SavedData savedData = RestaurantManager.getInstance().getSavedData();

        savedData.setLastModifiedRestaurants(restaurantData.getResult().getMetadataModified());
        savedData.setLastModifiedInspections(inspectionData.getResult().getMetadataModified());
        savedData.setLastDownloadData();
        savedData.setLastUpdateCancelled(false);

        savedData.saveLastDownloadInfo();
        savedData.saveLists();
    }

    private void saveUpdateCancelledInfo(){
        SavedData saver = RestaurantManager.getInstance().getSavedData();
        saver.setLastUpdateCancelled(true);
        saver.saveLastDownloadInfo();
    }

    //https://stackoverflow.com/questions/32426588/delete-old-activity-instance-when-starting-a-new-activity
    private void updateMap(){
        Intent newMap = new Intent(mapsActivity,MapsActivity.class);
        newMap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mapsActivity.startActivity(newMap);
    }

    private void updateMapIfSavedDataLoaded(){
        if(RestaurantManager.getInstance().getSavedData().isSavedDataLoaded()){
            updateMap();
        }
    }

    private void setClickableTrue(){
        Log.d("Download Data","---------Can click true");
        ImageView mInfo = mapsActivity.findViewById(R.id.mapsReturnRestaurantList);
        ImageView mGpsBtn = mapsActivity.findViewById(R.id.mapsGetCurrentLocation);
        ImageView clickBlock = mapsActivity.findViewById(R.id.clickBlock);
        mapsActivity.runOnUiThread(() -> {
            clickBlock.setVisibility(View.GONE);
            mInfo.setEnabled(true);
            mGpsBtn.setEnabled(true);
        });
    }

    @Override
    protected void onPreExecute() {
        Log.d("DownloadData", "onPreExecute----please wait dialogue tester---");
        super.onPreExecute();
        downloadAlert();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d("DownloadData","doInBackground-DOWNLOADDATA READING");
        downloadAnimation();
        readDataToTempLists();
        Log.d("DownloadData","doInBackground-DOWNLOADDATA Finish");
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.d("DownloadData","onPostExecute");
        hideArrow();
        dataReader.loadDataToManager();
        saveDataOnSuccess();
        downloadAlert.dismiss();
        setClickableTrue();
        updateMap();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d("DownloadData","onCancelled-DOWNLOADDATA Cancelled");
        saveUpdateCancelledInfo();
        downloadAlert.dismiss();
        setClickableTrue();
        updateMapIfSavedDataLoaded();
    }
}
