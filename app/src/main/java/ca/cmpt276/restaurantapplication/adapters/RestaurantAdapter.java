package ca.cmpt276.restaurantapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ca.cmpt276.model.Inspection;
import ca.cmpt276.model.Restaurant;
import ca.cmpt276.model.RestaurantManager;
import ca.cmpt276.restaurantapplication.InspectionActivity;
import ca.cmpt276.restaurantapplication.MainActivity;
import ca.cmpt276.restaurantapplication.R;

import static android.content.Context.MODE_PRIVATE;
import static ca.cmpt276.model.Inspection.getContext;
import static ca.cmpt276.restaurantapplication.R.drawable.wendysresize;

/**
 * Adapter for restaurant list activity (called MainActivity.java)
 * Used the video below to turn listview to recyclerView:
 * https://www.youtube.com/watch?v=W29vSFYF1ts
 *
 * Used the videos below to learn how to filter results in recyclerView
 * https://www.youtube.com/watch?v=sJ-Z9G0SDhc&t=693s
 * https://www.youtube.com/watch?v=CTvzoVtKoJ8&t=45s
 *
 * https://www.flaticon.com/free-icon/store_123403 restaurant icon1
 * https://www.flaticon.com/free-icon/store_138310 restaurant icon2
 * https://www.flaticon.com/free-icon/store_138313 restaurant icon3
 * https://www.flaticon.com/free-icon/online-store_265754 restaurant icon4
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> implements Filterable {
    private int restaurantIcons[] = {R.drawable.restaurant_icon, R.drawable.restaurant_icon2, R.drawable.restaurant_icon3,
            R.drawable.restaurant_icon4};
    private List<Restaurant> restaurants;
    private List<String> favouriteRestaurantTrackingNum;
    private List<Restaurant> restaurantsFull;
    private Context context;
    private static final String EXTRA_RESTAURANT = "EXTRA_RESTAURANT";
    private String regex = "\t";
    private RestaurantManager restaurant_manager=RestaurantManager.getInstance();

    public RestaurantAdapter(Context context, List<Restaurant> restaurants) {
        this.context = context;
        this.restaurants = restaurants;

        this.favouriteRestaurantTrackingNum = restaurant_manager.getFavouriteRestaurantsTrackingNum();
        // Creates a new restaurantFull list that will not point to the same list as restaurants
        restaurantsFull = new ArrayList<>(restaurants);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView restaurantNameTextView;
        private TextView restaurantNumIssuesTextView;
        private TextView restaurantInspectionDate;
        private View parentView;
        private ImageView display_restaurant_icon;
        private ImageView display_hazard_colour_bar;
        private ImageView display_hazard_icon;
        private CheckBox checkBoxRestaurantFavourite;
        private CardView restaurantCardView;
        public ViewHolder(@NonNull View view) {
            super(view);
            this.restaurantNameTextView = view
                    .findViewById(R.id.restaurantName);
            this.restaurantNumIssuesTextView = view
                    .findViewById(R.id.restaurantNumIssuesValue);
            this.restaurantInspectionDate = view
                    .findViewById(R.id.restaurantInspectionDateValue);
            this.display_restaurant_icon = view.findViewById(R.id.restaurantIcon);
            this.display_hazard_colour_bar = view.findViewById(R.id.restaurantHazardLevelColour);
            this.display_hazard_icon = view.findViewById(R.id.restaurantHazardLevelIcon);
            this.checkBoxRestaurantFavourite = view.findViewById(R.id.restaurantFavouriteCheckBox);
            this.restaurantCardView = view.findViewById(R.id.restaurantCardView);
            parentView = view;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater
                        .from(context)
                        .inflate(R.layout.recyclerview_restaurants, parent, false)
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
        if (recentInspection != null) {
            issues = Integer.toString(recentInspection.getNumIssues());
            hazardLevel = recentInspection.getHazardRating();
            recentInspectionDate = recentInspection.getInspectionDate();
            recentInspectionDate = recentInspection.displayInspectionDateFormatted(recentInspectionDate);
        }
        holder.restaurantNameTextView.setText(restaurant.getRestaurantName());
        holder.restaurantNumIssuesTextView.setText(issues);
        holder.restaurantInspectionDate.setText(recentInspectionDate);

        String restaurantName = restaurant.getRestaurantName();
        if (restaurantName.contains("7-Eleven")) {
            holder.display_restaurant_icon.setImageResource(R.drawable.sevenelevenresize);
        } else if (restaurantName.contains("Panago")) {
            holder.display_restaurant_icon.setImageResource(R.drawable.panagoresize);
        } else if (restaurantName.contains("Pizza Hut")) {
            holder.display_restaurant_icon.setImageResource(R.drawable.pizzahutresize);
        } else if (restaurantName.contains("A&W") || restaurantName.contains("A & W")) {
            holder.display_restaurant_icon.setImageResource(R.drawable.awresized);
        } else if (restaurantName.contains("Starbucks Coffee")) {
            holder.display_restaurant_icon.setImageResource(R.drawable.starbucksresize);
        } else if (restaurantName.contains("Subway")) {
            holder.display_restaurant_icon.setImageResource(R.drawable.subwayresize);
        } else if (restaurantName.contains("Tim Hortons") || restaurantName.contains("Tim Horton's")) {
            holder.display_restaurant_icon.setImageResource(R.drawable.timhortonsresize);
        } else if (restaurantName.contains("Wendy's")) {
            holder.display_restaurant_icon.setImageResource(wendysresize);
        } else if (restaurantName.contains("Dairy Queen")) {
            holder.display_restaurant_icon.setImageResource(R.drawable.dqresized);
        } else if (restaurantName.contains("Safeway")) {
            holder.display_restaurant_icon.setImageResource(R.drawable.safewayresized);
        } else if (restaurantName.contains("Burger King")) {
            holder.display_restaurant_icon.setImageResource(R.drawable.burgerkingresize);
        } else {
            holder.display_restaurant_icon.setImageResource(chooseRestaurantPicture());
        }
        // Setting up hazard images
        int colourBar = getHazardColourBar(hazardLevel);
        holder.display_hazard_colour_bar.setColorFilter(ContextCompat.getColor(getContext(), colourBar));
        holder.display_hazard_colour_bar.setBackgroundColor(ContextCompat.getColor(getContext(), colourBar));
        holder.display_hazard_colour_bar.setImageResource(getHazardColourBar(hazardLevel));
        holder.display_hazard_icon.setImageResource(getHazardIcon(hazardLevel));

        holder.checkBoxRestaurantFavourite.setChecked(false);
        holder.restaurantCardView.setCardBackgroundColor(context.getResources().getColor(
                R.color.white));

        holder.checkBoxRestaurantFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBoxRestaurantFavourite.isChecked()) {
                    favouriteRestaurantTrackingNum.add(restaurant.getTrackingNumber());
                    holder.restaurantCardView.setCardBackgroundColor(context.getResources().getColor(
                            R.color.quantum_yellow));
                    restaurant.setFavourite(true);
                    saveFavouriteRestaurantList();
                } else {
                    favouriteRestaurantTrackingNum.remove(restaurant.getTrackingNumber());
                    holder.restaurantCardView.setCardBackgroundColor(context.getResources().getColor(
                            R.color.white));
                    restaurant.setFavourite(false);
                    saveFavouriteRestaurantList();
                }
            }
        });

        if (favouriteRestaurantTrackingNum.contains(restaurant.getTrackingNumber())) {
            //Log.d("TESTING", restaurant.getRestaurantName());
            holder.checkBoxRestaurantFavourite.setChecked(true);
            holder.restaurantCardView.setCardBackgroundColor(context.getResources().getColor(
                    R.color.quantum_yellow));
            // Set the status of this restaurant to true
            restaurant.setFavourite(true);
        }


        // Activates when a restaurant is clicked.
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = InspectionActivity.makeIntent(context);
                intent.putExtra(EXTRA_RESTAURANT, position);
                context.startActivity(intent);
            }
        });

    }
    // Save Restaurants Functions Below
    private void saveFavouriteRestaurantList(){
        try {
            FileOutputStream saveFavouriteRestaurantListFStream =
                    context.openFileOutput("save_favourite_restaurants", MODE_PRIVATE);
            writeFavouriteRestaurantsToFile(favouriteRestaurantTrackingNum, saveFavouriteRestaurantListFStream);
            saveFavouriteRestaurantListFStream.close();
        } catch (FileNotFoundException e) {
            Log.d("SavedData_Date", "saveRestaurantList-Error-"+e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeFavouriteRestaurantsToFile(List<String> favouriteRestaurants,
                                                 FileOutputStream saveFavouriteRestaurantListFStream) throws IOException {
        for (String restaurantTrackingNum: favouriteRestaurants) {
            saveFavouriteRestaurantListFStream.write
                    (appendSingleFavouriteRestaurantData(restaurantTrackingNum).getBytes());
            Log.d("SavedData_Date", "saveRestaurantList-Saving: "+
                    appendSingleFavouriteRestaurantData(restaurantTrackingNum));
        }
    }

    private String appendSingleFavouriteRestaurantData(String restaurant){
        return  restaurant +'\n';
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

    @Override
    public int getItemCount() {
        return this.restaurants.size();
    }

    // Used the videos below to learn how to filter results (SearchView) in recyclerView
    // https://www.youtube.com/watch?v=sJ-Z9G0SDhc&t=693s
    // https://www.youtube.com/watch?v=CTvzoVtKoJ8&t=45s
    @Override
    public Filter getFilter() {
        return restaurantFilter;
    }

    private Filter restaurantFilter = new Filter() {
        final String defaultFilterHazardLevel = "";
        final int defaultFilterCritIssues = -1;
        final boolean defaultFilterCheckBoxFavouriteChecked = false;
        // Checks if any filters are applied to the search
        private boolean isFiltersChanged(String filterHazardLevel, int filterNumCritIssues, boolean
                                         isFilterFavouriteChecked)
        {
            // No filters set
            if(filterHazardLevel.equals(defaultFilterHazardLevel)
                    && filterNumCritIssues == defaultFilterCritIssues
                    && isFilterFavouriteChecked == defaultFilterCheckBoxFavouriteChecked) {
                return false;
            }

            else {
                return true;
            }
        }

        private FilterResults filterData(CharSequence constraint) {
            String filterHazardLevel = MainActivity.getFilterHazardLevel();
            int filterNumCritIssues = MainActivity.getFilterCritIssues();
            boolean isFilterFavouriteChecked = MainActivity.getFilterFavouriteChecked();
            boolean isFiltersChanged = isFiltersChanged(filterHazardLevel, filterNumCritIssues,
                    isFilterFavouriteChecked);
            String filterComparison = MainActivity.getFilterComparison();

            final String LOW_HAZARD = context.getResources().getString(R.string.hazard_level_low);
            List<Restaurant> filteredRestaurants = new ArrayList<>();

            // Nothing typed in the textBox
            if(constraint == null || constraint.length() == 0 && !isFiltersChanged) {
                filteredRestaurants.addAll(restaurantsFull);
            }
            // Nothing typed in the textBox, but filters were applied.
            else if (constraint == null || constraint.length() == 0 && isFiltersChanged) {
                for (Restaurant restaurant:restaurantsFull) {
                    // Settings up variables for filter check
                    Inspection recentInspection = restaurant.getRecentInspection();

                    boolean isRestaurantFavourite = restaurant.getFavourite();
                    boolean isRecentInspectionWithinYear = true;
                    String recentInspectionHazardLevel = "";
                    int recentInspectionNumCritIssues = -1;

                    if (recentInspection != null) {
                        recentInspectionHazardLevel = recentInspection.getHazardRating();
                        recentInspectionNumCritIssues = recentInspection.getNumCritical();

                        if (recentInspectionHazardLevel.equals("")) {
                            recentInspectionHazardLevel = LOW_HAZARD;
                        }
                        isRecentInspectionWithinYear = recentInspection.getInspectionWithinYear();
                    }
                    else {
                        recentInspectionHazardLevel = LOW_HAZARD;
                        recentInspectionNumCritIssues = 0;
                    }

                    // filterHazardLevel, filterNumCritIssues <=, filterFavourite were set
                    if (recentInspectionHazardLevel.equals(filterHazardLevel)
                            && filterComparison.equals("<=")
                            && recentInspectionNumCritIssues <= filterNumCritIssues
                            && isFilterFavouriteChecked && isRestaurantFavourite
                            && isRecentInspectionWithinYear)
                    {
                            filteredRestaurants.add(restaurant);
                    }

                    // filterHazardLevel, filterNumCritIssues >=, filterFavourite were set
                    else if (recentInspectionHazardLevel.equals(filterHazardLevel)
                            && filterComparison.equals(">=")
                            && recentInspectionNumCritIssues >= filterNumCritIssues
                            && isFilterFavouriteChecked && isRestaurantFavourite
                            && isRecentInspectionWithinYear)
                    {
                        filteredRestaurants.add(restaurant);
                    }

                    // Two filters were set
                    // fc = filterNumCritIssues
                    // hl = filterHazardLevel
                    // ff = filterFavourites

                    //fc <=,hl
                    else if (recentInspectionHazardLevel.equals(filterHazardLevel)
                            && filterComparison.equals("<=")
                            && recentInspectionNumCritIssues <= filterNumCritIssues
                            && !isFilterFavouriteChecked
                            && isRecentInspectionWithinYear) {
                        filteredRestaurants.add(restaurant);
                    }
                    //ff,fc <=
                    else if (filterHazardLevel.equals("")
                            && filterComparison.equals("<=")
                            && recentInspectionNumCritIssues <= filterNumCritIssues
                            && isRestaurantFavourite
                            && isRecentInspectionWithinYear) {
                        filteredRestaurants.add(restaurant);
                    }
                    //fc >=,hl
                    else if (recentInspectionHazardLevel.equals(filterHazardLevel)
                            && filterComparison.equals(">=")
                            && recentInspectionNumCritIssues >= filterNumCritIssues
                            && !isFilterFavouriteChecked
                            && isRecentInspectionWithinYear) {
                        filteredRestaurants.add(restaurant);
                    }
                    //ff,fc >=
                    else if (filterHazardLevel.equals("")
                            && filterComparison.equals(">=")
                            && recentInspectionNumCritIssues >= filterNumCritIssues
                            && isRestaurantFavourite
                            && isRecentInspectionWithinYear) {
                        filteredRestaurants.add(restaurant);
                    }

                    //ff,hl
                    else if (recentInspectionHazardLevel.equals(filterHazardLevel) &&
                            filterNumCritIssues == -1
                            && isRestaurantFavourite) {
                        filteredRestaurants.add(restaurant);
                    }

                    // Only one of the filters were set
                    else {
                            // Only HazardLevel filter set
                            if (recentInspectionHazardLevel.equals(filterHazardLevel) && filterNumCritIssues == -1 && !isFilterFavouriteChecked) {
                                    filteredRestaurants.add(restaurant);
                            }

                            // Only numCritIssues filter set <=
                            else if (recentInspectionNumCritIssues <= filterNumCritIssues
                                    && filterComparison.equals("<=")
                                    && filterHazardLevel.equals("") && !isFilterFavouriteChecked
                                    && isRecentInspectionWithinYear) {
                                filteredRestaurants.add(restaurant);
                            }

                            // Only numCritIssues filter set >=
                            else if (recentInspectionNumCritIssues >= filterNumCritIssues
                                    && filterComparison.equals(">=")
                                    && filterHazardLevel.equals("") && !isFilterFavouriteChecked
                                    && isRecentInspectionWithinYear) {
                                filteredRestaurants.add(restaurant);
                            }
                            // Only favourites set
                            else if (isRestaurantFavourite == isFilterFavouriteChecked && filterNumCritIssues == -1 && filterHazardLevel.equals("")) {
                                filteredRestaurants.add(restaurant);
                            }
                    }
                }
            }

            else {
                // Removes case sensitive, and removes empty spaces
                String filteredPattern = constraint.toString().toLowerCase().trim();

                for (Restaurant restaurant:restaurantsFull) {
                    String restaurantName = restaurant.getRestaurantName().toLowerCase();

                    if (restaurantName.contains(filteredPattern)) {

                        // No filters set
                        if(!isFiltersChanged) {
                            filteredRestaurants.add(restaurant);
                        }

                        else {
                            // Setting up variables for filter check
                            Inspection recentInspection = restaurant.getRecentInspection();
                            boolean isRestaurantFavourite = restaurant.getFavourite();
                            //restaurantName = restaurant.getRestaurantName();
                            boolean isRecentInspectionWithinYear = true;
                            String recentInspectionHazardLevel = "";
                            int recentInspectionNumCritIssues = -1;

                            if(recentInspection != null) {
                                recentInspectionHazardLevel = recentInspection.getHazardRating();
                                recentInspectionNumCritIssues = recentInspection.getNumCritical();
                                isRecentInspectionWithinYear = recentInspection.getInspectionWithinYear();
                                if (recentInspectionHazardLevel.equals("")) {
                                    recentInspectionHazardLevel = LOW_HAZARD;
                                }
                            }

                            else {
                                recentInspectionHazardLevel = LOW_HAZARD;
                                recentInspectionNumCritIssues = 0;
                            }

                            // filterHazardLevel, filterNumCritIssues <=, filterFavourite were set
                            if(recentInspectionHazardLevel.equals(filterHazardLevel)
                                    && filterComparison.equals("<=")
                                    && recentInspectionNumCritIssues <= filterNumCritIssues
                                    && isRecentInspectionWithinYear
                                    && isFilterFavouriteChecked && isRestaurantFavourite)
                            {
                                filteredRestaurants.add(restaurant);
                            }
                            // filterHazardLevel, filterNumCritIssues >=, filterFavourite were set
                            else if(recentInspectionHazardLevel.equals(filterHazardLevel)
                                    && filterComparison.equals(">=")
                                    && recentInspectionNumCritIssues >= filterNumCritIssues
                                    && isRecentInspectionWithinYear
                                    && isFilterFavouriteChecked && isRestaurantFavourite)
                            {
                                filteredRestaurants.add(restaurant);
                            }


                            // Two filters were set
                            // fc = filterNumCritIssues
                            // hl = filterHazardLevel
                            // ff = filterFavourites

                            //fc <=,hl
                            else if (recentInspectionHazardLevel.equals(filterHazardLevel)
                                    && filterComparison.equals("<=")
                                    && recentInspectionNumCritIssues <= filterNumCritIssues
                                    && isRecentInspectionWithinYear
                                    && !isFilterFavouriteChecked) {
                                filteredRestaurants.add(restaurant);
                            }
                            //ff,fc <=
                            else if (filterHazardLevel.equals("")
                                    && filterComparison.equals("<=")
                                    && recentInspectionNumCritIssues <= filterNumCritIssues
                                    && isRecentInspectionWithinYear
                                    && isRestaurantFavourite) {
                                filteredRestaurants.add(restaurant);
                            }

                            //fc >=,hl
                            else if (recentInspectionHazardLevel.equals(filterHazardLevel)
                                    && filterComparison.equals(">=")
                                    && recentInspectionNumCritIssues >= filterNumCritIssues
                                    && isRecentInspectionWithinYear
                                    && !isFilterFavouriteChecked) {
                                filteredRestaurants.add(restaurant);
                            }
                            //ff,fc >=
                            else if (filterHazardLevel.equals("")
                                    && filterComparison.equals(">=")
                                    && recentInspectionNumCritIssues >= filterNumCritIssues
                                    && isRecentInspectionWithinYear
                                    && isRestaurantFavourite) {
                                filteredRestaurants.add(restaurant);
                            }

                            //ff,hl
                            else if (recentInspectionHazardLevel.equals(filterHazardLevel) &&
                                    filterNumCritIssues == -1
                                    && isRestaurantFavourite) {
                                filteredRestaurants.add(restaurant);
                            }

                            // Only one of the filters were set
                            else {
                                // Only HazardLevel filter set
                                if (recentInspectionHazardLevel.equals(filterHazardLevel) && filterNumCritIssues == -1 && !isFilterFavouriteChecked) {
                                    filteredRestaurants.add(restaurant);
                                }
                                // Only numCritIssues filter set <=
                                else if (recentInspectionNumCritIssues <= filterNumCritIssues
                                        && filterComparison.equals("<=")
                                        && filterHazardLevel.equals("")
                                        && !isFilterFavouriteChecked
                                        && isRecentInspectionWithinYear) {
                                    filteredRestaurants.add(restaurant);
                                }
                                // Only numCritIssues filter set >=
                                else if (recentInspectionNumCritIssues >= filterNumCritIssues
                                        && filterComparison.equals(">=")
                                        && filterHazardLevel.equals("")
                                        && !isFilterFavouriteChecked
                                        && isRecentInspectionWithinYear) {
                                    filteredRestaurants.add(restaurant);
                                }
                                // Only favourites set
                                else if (isRestaurantFavourite && isFilterFavouriteChecked && filterNumCritIssues == -1 && filterHazardLevel.equals("")) {
                                    filteredRestaurants.add(restaurant);
                                }
                            }
                        }
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredRestaurants;

            return results;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return filterData(constraint);
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            restaurants.clear();
            restaurants.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}