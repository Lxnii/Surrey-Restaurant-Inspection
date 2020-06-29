package ca.cmpt276.restaurantapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.model.RestaurantManager;
import ca.cmpt276.restaurantapplication.adapters.RestaurantAdapter;

/**
 * This activity displays the list of all restaurants.
 */
public class MainActivity extends AppCompatActivity {
    private static final String EXTRA_RESTAURANT = "EXTRA_RESTAURANT";
    private static final String FILTER_HAZARD_LEVEL_KEY = "ca.cmpt276.hazardLevel";
    private static final String FILTER_IS_FAVOURITE_CHECKED_KEY = "ca.cmpt276.isFavouriteChecked";
    private static final String FILTER_NUM_CRIT_ISSUES_KEY = "ca.cmpt276.numCritIssues";
    private static final String FILTER_COMPARISON_NUM_CRIT_KEY = "ca.cmpt276.comparisonNumCrit";
    private static final String APP_PREFS = "AppPrefs";

    private RestaurantAdapter adapter;
    RestaurantManager manager_restaurant;

    private static String filterHazardLevel = "";
    private static boolean isFilterCheckBoxFavouriteChecked = false;
    private static int filterCritIssues = -1;
    private static String filterComparison = "<=";
    private boolean isTextInSearchQuery = false;
    private boolean isFiltersApplied = false;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String defaultFilterHazardLevel = "";
        final String defaultFilterComparison = "<=";
        final String defaultFilterNumCritIssues = "";
        final boolean defaultIsFilterCheckBoxFavouriteChecked = false;

        // Clear the filter settings when activity is just created
        saveFilterSettings(defaultFilterHazardLevel, defaultFilterComparison,
                defaultFilterNumCritIssues,defaultIsFilterCheckBoxFavouriteChecked);

        manager_restaurant = RestaurantManager.getInstance();
        Log.d("MapActivitys","MainLoaded");
        populateRecyclerView();
    }

    // Instantiates RestaurantAdapter
    private void populateRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.activity_main_recyclerview);
        adapter = new RestaurantAdapter(this, manager_restaurant.getRestaurantList());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Returns the restaurant that is clicked in the recyclerview_restaurants
    public static int getRestaurantClicked(Intent intent) {
        return intent.getIntExtra(EXTRA_RESTAURANT, 0);
    }

    /**
     * Creating app buttons for action bar in the issue activity
     * Code adapted from 'John's Android Studio Tutorials':
     * https://www.youtube.com/watch?v=5MSKuVO2hV4
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        this.menu = menu;
        // Creates the search bar
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        MenuItem filterItem = menu.findItem(R.id.action_settings);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                setItemsVisibility(menu, searchItem, filterItem, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                setItemsVisibility(menu, searchItem, filterItem, true);
                return true;
            }
        });

        //https://stackoverflow.com/questions/7409288/how-to-dismiss-keyboard-in-android-searchview
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!query.equals("")) {
                    isTextInSearchQuery = true;
                }
                else {
                    isTextInSearchQuery = false;
                }
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.equals("")) {
                    isTextInSearchQuery = true;
                }
                else {
                    isTextInSearchQuery = false;
                }
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    //https://stackoverflow.com/questions/42295164/how-to-hide-menu-item-from-toolbar-when-click-on-search-icon-in-android
    private void setItemsVisibility(Menu menu, MenuItem exception, MenuItem exception2, boolean visible) {
        for (int i=0; i<menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item != exception && item != exception2) {
                    item.setVisible(visible);
            }
        }
    }
    /**
     * Creating app buttons for action bar - Code adapted from 'John's Android Studio Tutorials':
     * https://www.youtube.com/watch?v=5MSKuVO2hV4
     *
     * Image icon8_calculator_24 obtained from https://icons8.com/icon/3352/calculator
     *
     * Used the videos below to learn how to filter results in recyclerView
     * https://www.youtube.com/watch?v=sJ-Z9G0SDhc&t=693s
     * https://www.youtube.com/watch?v=CTvzoVtKoJ8&t=45s
     *
     * Used the code in the link below to hide other action bar items
     * https://stackoverflow.com/questions/30577252/expand-search-view-to-use-entire-action-bar-hide-other-things/30577820
     *
     * Used the video below to learn how to make a custom dialog
     * https://www.youtube.com/watch?v=nlqtyfshUkc
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // Acquires the item selected in Action Bar.
        int id = item.getItemId();

        if(id == R.id.action_maps) {
            if(isFiltersApplied || isTextInSearchQuery) {
                String text = getResources().getString(R.string.mainactivity_toast_text);
                Toast.makeText(this,text,Toast.LENGTH_LONG).show();
            }
            else {
                displayMapActivity();
            }
        }

        else if(id == R.id.action_settings) {
            displayFilterSettingsDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    // Goes to MapsActivity
    private void displayMapActivity() {
        Intent intent = MapsActivity.makeIntent(MainActivity.this);
        startActivity(intent);
        finish();
    }

    // Used the two videos below to learn how to use check boxes and spinners
    //https://www.youtube.com/watch?v=QFCRqyWA6zg
    //https://www.youtube.com/watch?v=nlqtyfshUkc
    private void displayFilterSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.filter_search_dialog, null);

        String alertDialogTitle = getResources().getString(R.string.alert_filter_dialog_title);
        builder.setTitle(alertDialogTitle);
        CharSequence filterNumCritIssuesText = getSharedFilterNumCritIssues(this);

        EditText editTextNumCritIssues = view.findViewById(R.id.filter_editText_num_critical_violations);
        editTextNumCritIssues.setText(filterNumCritIssuesText);

        CheckBox checkBoxFavourites = view.findViewById(R.id.filter_checkBox_tag_favourite);
        checkBoxFavourites.setChecked(getSharedIsFilterFavouriteChecked(this));

        Spinner spinnerHazardLevels = view.findViewById(R.id.filter_spinner_hazard_levels);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.hazard_levels));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHazardLevels.setAdapter(arrayAdapter);

        int spinnerPosition = getSpinnerHazardPosition();
        spinnerHazardLevels.setSelection(spinnerPosition);

        RadioGroup radioGroup = view.findViewById(R.id.radio_group_comparisons);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton btn = group.findViewById(checkedId);
                    filterComparison = btn.getText().toString();
                }
            });
            //https://stackoverflow.com/questions/9842516/set-selected-index-of-an-android-radiogroup
            int radioGroupPosition = getRadioGroupNumIssuesPosition();
            ((RadioButton)radioGroup.getChildAt(radioGroupPosition)).setChecked(true);

        String alertDialogPositiveBtnTxt = getResources().getString(R.string.alert_filter_dialog_positive);
        builder.setPositiveButton(alertDialogPositiveBtnTxt, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String itemFromSpinerSelected = spinnerHazardLevels.getSelectedItem().toString();
                String numCritIssuesText = editTextNumCritIssues.getText().toString();
                boolean isCheckBoxFavouritesChecked = checkBoxFavourites.isChecked();
                setFilterData(itemFromSpinerSelected, numCritIssuesText, isCheckBoxFavouritesChecked);
            }
        });

        String alertDialogNegativeBtnText = getResources().getString(R.string.alert_filter_dialog_negative);
        builder.setNegativeButton(alertDialogNegativeBtnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String defaultFilterHazardLevel = "";
                String defaultFilterCritIssues = "-1";
                boolean defaultCheckedFavourites = false;
                setFilterData(defaultFilterHazardLevel, defaultFilterCritIssues, defaultCheckedFavourites);
                dialog.dismiss();
            }
        });
        builder.setView(view);

        AlertDialog dialog = builder.create();


        dialog.show();
    }

    // Sets the filter data from the filter settings when search is prompted
    private void setFilterData(String itemFromSpinerSelected,  String numCritIssuesText,
                               boolean isFavouriteCheckBoxSelected) {

        boolean isDefaultHazardLevelSelected = itemFromSpinerSelected.toLowerCase()
                .equalsIgnoreCase(getResources()
                        .getString(R.string.filter_stringarray_hazard_default_level));

        // Default values
        String defaultFilterHazardLevel = "";

        // Set an arbitary value of -1 to use as a check when displaying filtered data
        int defaultFilterCritIssues = -1;

        boolean  defaultFavouriteSelected = false;

        if (isDefaultHazardLevelSelected) {
            filterHazardLevel = defaultFilterHazardLevel;
        }
        else {
            filterHazardLevel = itemFromSpinerSelected;
        }

        if(!numCritIssuesText.equals("")) {
            filterCritIssues = Integer.parseInt(numCritIssuesText);
        }
        else {
            filterCritIssues = defaultFilterCritIssues;
        }


        if(isFavouriteCheckBoxSelected) {
            isFilterCheckBoxFavouriteChecked = isFavouriteCheckBoxSelected;
        }
        else {
             isFilterCheckBoxFavouriteChecked = defaultFavouriteSelected;
        }

        // Since -1 is the default value, set it blank in the saveFilter settings
        if(numCritIssuesText.equals("-1")) {
            saveFilterSettings(filterHazardLevel, filterComparison,"", isFilterCheckBoxFavouriteChecked);
        }
        else {
            saveFilterSettings(filterHazardLevel, filterComparison,numCritIssuesText, isFilterCheckBoxFavouriteChecked);
        }

        isFiltersApplied = isFiltersChanged(filterHazardLevel, filterCritIssues, isFilterCheckBoxFavouriteChecked);
        // Refreshes the adapter to display the search with filters.
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        String query = searchView.getQuery().toString();
        adapter.getFilter().filter(query);
    }

    public static String getFilterHazardLevel() {
        return filterHazardLevel;
    }

    public static int getFilterCritIssues() {
        return filterCritIssues;
    }

    public static boolean getFilterFavouriteChecked (){
        return isFilterCheckBoxFavouriteChecked;
    }
    public static String getFilterComparison() {
        return filterComparison;
    }
    // When back button on android clicked exit application.
    //https://stackoverflow.com/questions/4778754/how-do-i-kill-an-activity-when-the-back-button-is-pressed
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keycode, event);
    }
    public static Intent makeIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    private boolean isFiltersChanged(String filterHazardLevel, int filterNumCritIssues, boolean
            isFilterFavouriteChecked)
    {
        final String defaultFilterHazardLevel = "";
        final int defaultFilterCritIssues = -1;
        // No filters set
        if(filterHazardLevel.equals(defaultFilterHazardLevel)
                && filterNumCritIssues == defaultFilterCritIssues
                && !isFilterFavouriteChecked) {
            return false;
        }

        else {
            return true;
        }
    }

    /**
     * Save filter settings
     * https://www.youtube.com/watch?v=fJEFZ6EOM9o&t=611s
     */
    private void saveFilterSettings(String filterHazardLevel, String filterComparison,
                                    String filterNumCritIssues, boolean isFilterCheckBoxFavouriteChecked) {
        String defaultFilterHazardLevel = "";
        String defaultFilterComparison = "<=";
        String defaultFilterCritIssues = "-1";
        boolean defaultCheckedFavourites = false;
        boolean isDefaultHazardLevelSelected = filterHazardLevel.toLowerCase()
                .equalsIgnoreCase(getResources()
                        .getString(R.string.filter_stringarray_hazard_default_level));

        if(isDefaultHazardLevelSelected) {
            filterHazardLevel = "";
        }
        SharedPreferences prefs = this.getSharedPreferences(APP_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(FILTER_HAZARD_LEVEL_KEY, filterHazardLevel);
        editor.putString(FILTER_COMPARISON_NUM_CRIT_KEY, filterComparison);
        editor.putString(FILTER_NUM_CRIT_ISSUES_KEY, filterNumCritIssues);
        editor.putBoolean(FILTER_IS_FAVOURITE_CHECKED_KEY, isFilterCheckBoxFavouriteChecked);
        editor.apply();
    }
    private int getRadioGroupNumIssuesPosition() {
        String comparison = getSharedFilterComparison(this);
        int position = 0;
        if(!comparison.equals("<=")) {
            position = 1;
        }
        return position;
    }
    private String getSharedHazardLevel(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFS, MODE_PRIVATE);
        String defaultFilterHazardLevel = "";
        return prefs.getString(FILTER_HAZARD_LEVEL_KEY, defaultFilterHazardLevel);
    }
    private int getSpinnerHazardPosition() {
        String hazardLevel = getSharedHazardLevel(this);
        int position = 0;
        final String LOW_HAZARD = getResources().getString(R.string.hazard_level_low);
        final String MODERATE_HAZARD = getResources().getString(R.string.hazard_level_moderate);
        final String HIGH_HAZARD = getResources().getString(R.string.hazard_level_high);

        if(hazardLevel.equals(LOW_HAZARD)) {
            position = 1;
        }
        else if (hazardLevel.equals(MODERATE_HAZARD)){
            position = 2;
        }
        else if (hazardLevel.equals(HIGH_HAZARD)) {
            position = 3;
        }
        return position;
    }

    private String getSharedFilterComparison(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFS, MODE_PRIVATE);
        String defaultFilterComparison = "<=";
        return prefs.getString(FILTER_COMPARISON_NUM_CRIT_KEY, defaultFilterComparison);
    }

    private String getSharedFilterNumCritIssues(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFS, MODE_PRIVATE);
        String defaultFilterCritIssues = "";
        return prefs.getString(FILTER_NUM_CRIT_ISSUES_KEY, defaultFilterCritIssues);
    }

    private boolean getSharedIsFilterFavouriteChecked(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFS, MODE_PRIVATE);
        boolean defaultCheckedFavourites = false;
        return prefs.getBoolean(FILTER_IS_FAVOURITE_CHECKED_KEY, defaultCheckedFavourites);
    }


}




