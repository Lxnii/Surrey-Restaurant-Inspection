package ca.cmpt276.restaurantapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import ca.cmpt276.restaurantapplication.markercluster.MarkerClusterItem;
import ca.cmpt276.restaurantapplication.R;

// How to create Clusters and Cluster items:
// https://developers.google.com/maps/documentation/android-sdk/utility/marker-clustering#info-window
// http://mobiledevhub.com/2018/11/16/android-how-to-group-and-cluster-markers-in-google-maps/
// https://www.youtube.com/watch?v=DhYofrJPzlI&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=11

/**
 * Adapter for ClusterItems inside MapsActivity
 */
public class ClusterItemAdapter implements GoogleMap.InfoWindowAdapter {
    private  final View mWindow;
    private Context mContext;
    private MarkerClusterItem mClickedClusterItem;
    private boolean isInspectionCoordinatesClick = false;

    public ClusterItemAdapter(Context context, MarkerClusterItem clickedClusterItem, boolean isInspectionCoordinatesClick) {
        this.mContext = context;
        this.mClickedClusterItem = clickedClusterItem;
        this.isInspectionCoordinatesClick = isInspectionCoordinatesClick;
        mWindow = LayoutInflater.from(mContext).inflate(R.layout.cluster_item_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView textViewTitle =  mWindow
                .findViewById(R.id.clusterItemRestaurantName);

        TextView textViewSnippet =  mWindow
                .findViewById(R.id.clusterItemRestaurantAddressAndHazard);

            textViewTitle.setText(marker.getTitle());
            textViewSnippet.setText(marker.getSnippet());

        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return  null;
    }
}
