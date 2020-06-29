package ca.cmpt276.restaurantapplication.markercluster;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

// Used the links below to learn how to create a MarkerClusterRenderer for
//https://stackoverflow.com/questions/27745299/how-to-add-title-snippet-and-icon-to-clusteritem/27745681#27745681
//http://mobiledevhub.com/2018/11/16/android-how-to-group-and-cluster-markers-in-google-maps/
//https://stackoverflow.com/questions/32909260/disable-clustering-at-max-zoom-level-with-googles-android-maps-utils
//https://stackoverflow.com/questions/27470137/android-google-map-utils-clustering-distance
//https://stackoverflow.com/questions/29785890/how-can-i-change-the-color-of-the-clusters-on-my-android-google-map/44251314

/**
 *  This class renders how markers in a Cluster Manager look.
 */
public class MarkerClusterRenderer<T extends ClusterItem> extends DefaultClusterRenderer<MarkerClusterItem> {
    private MarkerClusterItem clusterItem;
    private boolean isInspectionCoordinatesClick;

    public MarkerClusterRenderer(Context context, GoogleMap googleMap,
                                 ClusterManager<MarkerClusterItem> clusterManager,
                                 boolean isInspectionCoordinatesClick) {
        super(context, googleMap, clusterManager);
        this.isInspectionCoordinatesClick = isInspectionCoordinatesClick;
        setMinClusterSize(2);
    }

    @Override
    protected void onBeforeClusterItemRendered(MarkerClusterItem item,
                                               MarkerOptions markerOptions) {
        clusterItem = item;
        markerOptions.icon(item.getIcon());
        markerOptions.snippet(item.getSnippet());
        markerOptions.title(item.getTitle());
        super.onBeforeClusterItemRendered(item,markerOptions);
    }

    @Override
    protected void onClusterItemRendered(MarkerClusterItem clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        if (isInspectionCoordinatesClick) {
            getMarker(clusterItem).showInfoWindow();
        }
    }

    // Sets the colour of the cluster depending on its size
    @Override
    protected int getColor(int clusterSize) {
        int returnColour;

        if (clusterSize < 20) {
            returnColour = Color.RED;
        }
        else if(clusterSize < 50) {
            returnColour = Color.MAGENTA;
        }
        else if(clusterSize < 100) {
            returnColour = Color.parseColor("#FF7F50");
        }
        else {
            returnColour = Color.BLUE;
        }

        return returnColour;
    }

    @Override
    public void setOnClusterItemClickListener(ClusterManager.OnClusterItemClickListener<MarkerClusterItem> listener) {
        super.setOnClusterItemClickListener(listener);
    }

    //    @Override
//    protected boolean shouldRenderAsCluster(Cluster<MarkerClusterItem> cluster) {
//        return  cluster.getSize() > 1;
//    }
}