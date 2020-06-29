package ca.cmpt276.restaurantapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

import ca.cmpt276.model.Inspection;
import ca.cmpt276.model.Restaurant;
import ca.cmpt276.model.RestaurantManager;
import ca.cmpt276.restaurantapplication.InspectionActivity;
import ca.cmpt276.restaurantapplication.R;

import static ca.cmpt276.model.Inspection.getContext;
import static ca.cmpt276.restaurantapplication.R.drawable.wendysresize;

/**
 * Adapter for RecentInspectionsFavouriteRestaurants activity
 * Used the video below to turn listview to recyclerView:
 * https://www.youtube.com/watch?v=W29vSFYF1ts
 *
 * https://www.flaticon.com/free-icon/store_123403 restaurant icon1
 * https://www.flaticon.com/free-icon/store_138310 restaurant icon2
 * https://www.flaticon.com/free-icon/store_138313 restaurant icon3
 * https://www.flaticon.com/free-icon/online-store_265754 restaurant icon4
 */
public class RecentInspectionsFavouriteRestaurantsAdapter extends RecyclerView.Adapter<RecentInspectionsFavouriteRestaurantsAdapter.ViewHolder> {
    private int restaurantIcons[] = {R.drawable.restaurant_icon, R.drawable.restaurant_icon2, R.drawable.restaurant_icon3,
            R.drawable.restaurant_icon4};
    private List<Restaurant> restaurants;
    private List<String> favouriteRestaurants;
    private Context context;
    private static final String EXTRA_RESTAURANT = "EXTRA_RESTAURANT";
    private String regex = "\t";
    public RecentInspectionsFavouriteRestaurantsAdapter(Context context, List<Restaurant> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
        this.favouriteRestaurants = RestaurantManager.getInstance().getFavouriteRestaurantsTrackingNum();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView favouriteRestaurantNameTextView;
        private TextView favouriteRestaurantNumIssuesTextView;
        private TextView favouriteRestaurantInspectionDate;
        private View parentView;
        private ImageView display_restaurant_icon;
        private ImageView display_hazard_colour_bar;
        private ImageView display_hazard_icon;
        public ViewHolder(@NonNull View view) {
            super(view);
            this.favouriteRestaurantNameTextView = view
                    .findViewById(R.id.favouriteRestaurantName);
            this.favouriteRestaurantNumIssuesTextView = view
                    .findViewById(R.id.favouriteRestaurantNumIssuesValue);
            this.favouriteRestaurantInspectionDate = view
                    .findViewById(R.id.favouriteRestaurantInspectionDateValue);
            this.display_restaurant_icon = view.findViewById(R.id.favouriteRestaurantIcon);
            this.display_hazard_colour_bar = view.findViewById(R.id.favouriteRestaurantHazardLevelColour);
            this.display_hazard_icon = view.findViewById(R.id.favouriteRestaurantHazardLevelIcon);

            parentView = view;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater
                        .from(context)
                        .inflate(R.layout.recyclerview_favourite_restaurants, parent, false)
        );
    }

    /*
        Safeway logo citation: https://logodix.com/logos/76626
        7-eleven: https://www.reddit.com/r/mildlyinfuriating/comments/a6f9id/the_n_in_the_7eleven_logo_is_in_lowercase/
        Panago: https://www.locapon.com/city/london/listing/panago-pizza/
        Pizza Hut: https://logotyp.us/logo/pizza-hut/
        A&W: https://twitter.com/awcanada
        Starbucks: http://www.artitudesdesign.com/starbucks-logo-story/
        Subway: https://clios.com/awards/winner/brand-design/subway/subway-logo-44588
        Tims : https://www.simcoereformer.ca/news/local-news/water-street-tim-hortons-closing
        DQ: https://www.pointfranchise.co.uk/articles/dairy-queen-franchise-uk-5963/
        Wendys: https://www.businessinsider.com/there-is-a-hidden-message-in-the-new-wendys-logo-2013-7
       BK: https://www.freepnglogos.com/pics/burger-king-logo
    */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Restaurant restaurant = restaurants.get(position);
        restaurant.setRestaurantPosition(position);
        Inspection recentInspection = restaurant.getRecentInspection();

            String issues = "0";
            String hazardLevel = "";
            String recentInspectionDate = context.getString(R.string.no_inspections);

            // An inspection exist
            if(recentInspection != null) {
                issues = Integer.toString(recentInspection.getNumIssues());
                hazardLevel = recentInspection.getHazardRating();
                recentInspectionDate = recentInspection.getInspectionDate();
                recentInspectionDate = recentInspection.displayInspectionDateFormatted(recentInspectionDate);
            }
        holder.favouriteRestaurantNameTextView.setText(restaurant.getRestaurantName());
        holder.favouriteRestaurantNumIssuesTextView.setText(issues);
        holder.favouriteRestaurantInspectionDate.setText(recentInspectionDate);

        String restaurantName= restaurant.getRestaurantName();
        if(restaurantName.contains("7-Eleven")) {
            holder.display_restaurant_icon.setImageResource(R.drawable.sevenelevenresize);
        }
        else  if(restaurantName.contains("Panago")) {
            holder.display_restaurant_icon.setImageResource(R.drawable.panagoresize);
        }
        else if(restaurantName.contains("Pizza Hut")) {
            holder.display_restaurant_icon.setImageResource(R.drawable.pizzahutresize);
        }
        else if (restaurantName.contains("A&W")||restaurantName.contains("A & W")) {
            holder.display_restaurant_icon.setImageResource(R.drawable.awresized);
        }
        else if(restaurantName.contains("Starbucks Coffee")) {
            holder.display_restaurant_icon.setImageResource(R.drawable.starbucksresize);
        }
        else if(restaurantName.contains("Subway")){
            holder.display_restaurant_icon.setImageResource(R.drawable.subwayresize);
        }
        else if(restaurantName.contains("Tim Hortons") || restaurantName.contains("Tim Horton's")){
            holder.display_restaurant_icon.setImageResource(R.drawable.timhortonsresize);
        }
        else if(restaurantName.contains("Wendy's")){
            holder.display_restaurant_icon.setImageResource(wendysresize);
        }
        else if(restaurantName.contains("Dairy Queen")){
            holder.display_restaurant_icon.setImageResource(R.drawable.dqresized);
        }
        else if(restaurantName.contains("Safeway")){
            holder.display_restaurant_icon.setImageResource(R.drawable.safewayresized);
        }
        else if(restaurantName.contains("Burger King")){
            holder.display_restaurant_icon.setImageResource(R.drawable.burgerkingresize);
        }
        else{
            holder.display_restaurant_icon.setImageResource(chooseRestaurantPicture());
        }
        // Setting up hazard images
        int colourBar = getHazardColourBar(hazardLevel);
        holder.display_hazard_colour_bar.setColorFilter(ContextCompat.getColor(getContext(), colourBar));
        holder.display_hazard_colour_bar.setBackgroundColor(ContextCompat.getColor(getContext(), colourBar));
        holder.display_hazard_colour_bar.setImageResource(getHazardColourBar(hazardLevel));

        holder.display_hazard_icon.setImageResource(getHazardIcon(hazardLevel));

    }

    @Override
    public int getItemCount() {
        return this.restaurants.size();
    }

    // Gets hazard icon
    //https://www.flaticon.com/free-icon/complain_1041891?term=exclamation%20mark&page=1&position=20 yellow_alert picture
    //https://www.hiclipart.com/free-transparent-background-png-clipart-iyhth green_alert picture
    //https://commons.wikimedia.org/wiki/File:GHS-pictogram-exclam.svg red_alert picture
    private int getHazardIcon(String hazardLevel) {
        int hazardIcon;

        final String MODERATE_HAZARD = context.getResources().getString(R.string.hazard_level_moderate);
        final String HIGH_HAZARD = context.getResources().getString(R.string.hazard_level_high);
        if(hazardLevel.equals(HIGH_HAZARD)) {
            hazardIcon = R.drawable.red_alert;
        }
        else if (hazardLevel.equals(MODERATE_HAZARD)) {
            hazardIcon = R.drawable.yellow_alert;
        }
        else {
            hazardIcon = R.drawable.green_alert;
        }

        return hazardIcon;
    }

    // Gets hazard colour bar
    private int getHazardColourBar(String hazardLevel) {
        int colourBar;

        final String MODERATE_HAZARD = context.getResources().getString(R.string.hazard_level_moderate);
        final String HIGH_HAZARD = context.getResources().getString(R.string.hazard_level_high);
        if(hazardLevel.equals(HIGH_HAZARD)) {
            colourBar = R.color.redBar;
        }
        else if (hazardLevel.equals(MODERATE_HAZARD)) {
            colourBar = R.color.orangeBar;
        }
        else {
            colourBar =  R.color.greenBar;
        }

        return colourBar;
    }

    // Selects a random restaurant icon
    private int chooseRestaurantPicture() {
        Random random = new Random();
        // Generate from 0 to 3
        int randomIndex = random.nextInt(4);

        return restaurantIcons[randomIndex];

    }

    public RecentInspectionsFavouriteRestaurantsAdapter updateRestaurantList(List<Restaurant> newRestaurantList){
        return new RecentInspectionsFavouriteRestaurantsAdapter(context,newRestaurantList);
    }

}