package ca.cmpt276.restaurantapplication.markercluster;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

// Used the link below to learn how to create a ClusterItem
// http://mobiledevhub.com/2018/11/16/android-how-to-group-and-cluster-markers-in-google-maps/
/**
 * This class represents a Cluster Item in a Cluster Manager
 */
public class MarkerClusterItem implements ClusterItem {

    private LatLng latLng;
    private String title;
    private String snippet;
    // Used for when starting the inspectionsActivity to pass the position of restaurant.
    private int positionInRecyclerView;
    private BitmapDescriptor icon;
    public MarkerClusterItem(LatLng latLng, String title, BitmapDescriptor icon, String snippet, int positionInRecyclerView){
        this.latLng = latLng;
        this.title = title;
        this.icon = icon;
        this.snippet = snippet;
        this.positionInRecyclerView = positionInRecyclerView;
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public int getPositionInRecyclerView() {
        return positionInRecyclerView;
    }
    public BitmapDescriptor getIcon() {
        return icon;
    }



}