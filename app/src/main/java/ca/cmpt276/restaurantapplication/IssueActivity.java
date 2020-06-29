package ca.cmpt276.restaurantapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import ca.cmpt276.model.Inspection;
import ca.cmpt276.model.RestaurantManager;
import ca.cmpt276.restaurantapplication.adapters.IssueAdapter;

/**
 * This activity displays information about violations in an issue.
 */
public class IssueActivity extends AppCompatActivity {
    private Inspection inspection;
    private int inspectionIndex;
    private int restaurantIndex;
    RestaurantManager manager_restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        manager_restaurant = RestaurantManager.getInstance();
        Intent intent = getIntent();

        restaurantIndex = InspectionActivity.getExtraInspectionIssueRestaurantIndex(intent);
        inspectionIndex = InspectionActivity.getExtraInspectionIssueClicked(intent);
        inspection = manager_restaurant.getRestaurant(restaurantIndex).getInspections().get(inspectionIndex);

        displayInspectionDetails();
        populateRecyclerView();
    }

    // Instantiates IssueAdapter
    private void populateRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.activity_issue_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new IssueAdapter(this, inspection.getIssueList(), restaurantIndex, inspectionIndex));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Displays the inspection information that was clicked on - gets
    private void displayInspectionDetails() {
        TextView display_issue_inspection_date;
        TextView display_issue_inspection_type;
        TextView display_issue_num_critical_issues;
        TextView display_issue_num_noncritical_issues;
        TextView display_issue_hazard;
        ImageView display_issue_hazard_colour_bar;
        ImageView display_issue_hazard_icon;

        // Format inspection date to the full date of inspection
        String tempDate = inspection.getInspectionDate();
        String inspectionDate;
        inspectionDate = inspection.displayFullInspectionDate(tempDate);

        String inspectionType = inspection.getInspectionType();
        String inspectionNumCritIssues = Integer.toString(inspection.getNumCritical());
        String inspectionNumNonCritIssues = Integer.toString(inspection.getNumNonCritical());
        String inspectionHazardLevel = inspection.getHazardRating();

        // Display information items
        display_issue_inspection_date = findViewById(R.id.issueInspectionDateValue);
        display_issue_inspection_type = findViewById(R.id.issueInspectionTypeValue);
        display_issue_num_critical_issues = findViewById(R.id.issueNumCriticalIssuesValue);
        display_issue_num_noncritical_issues = findViewById(R.id.issueNumNonCriticalIssuesValue);
        display_issue_hazard = findViewById(R.id.issueHazardLevelValue);
        display_issue_hazard_colour_bar = findViewById(R.id.issueHazardLevelColourBar);
        display_issue_hazard_icon = findViewById(R.id.issueHazardLevelIcon);

        display_issue_inspection_date.setText(inspectionDate);
        display_issue_inspection_type.setText(getTranslatedType(inspectionType));
        display_issue_num_critical_issues.setText(inspectionNumCritIssues);
        display_issue_num_noncritical_issues.setText(inspectionNumNonCritIssues);
        display_issue_hazard.setText(inspectionHazardLevel);

        // Checks hazardLevel then sets hazard colour bar accordingly
        int colourBar = getHazardColourBar(inspectionHazardLevel);
        display_issue_hazard_colour_bar.setColorFilter(ContextCompat.getColor(IssueActivity.this, colourBar));
        display_issue_hazard_colour_bar.setBackgroundColor(ContextCompat.getColor(IssueActivity.this, colourBar));

        // Checks hazardLevel then sets hazard icon accordingly
        display_issue_hazard_icon.setImageResource(getHazardIcon(inspectionHazardLevel));
    }

    private String getTranslatedType(String inspectionType){
        if(inspectionType.equals("Building")) {
            return getString(R.string.type_building);
        }
        else if(inspectionType.equals("Equipment")) {
            return getString(R.string.type_equipment);
        }
        else if (inspectionType.equals("Food")) {
            return getString(R.string.type_food);
        }
        else if(inspectionType.equals("Staff")) {
            return getString(R.string.type_staff);
        }
        else if(inspectionType.equals("Permit")) {
            return getString(R.string.type_permit);
        }
        else {
            return getString(R.string.type_pests);
        }
    }

    // Gets a hazard icon
    //https://www.flaticon.com/free-icon/complain_1041891?term=exclamation%20mark&page=1&position=20 yellow_alert picture
    //https://www.hiclipart.com/free-transparent-background-png-clipart-iyhth green_alert picture
    //https://commons.wikimedia.org/wiki/File:GHS-pictogram-exclam.svg red_alert picture
    private int getHazardIcon(String hazardLevel) {
        int hazardIcon;

        final String MODERATE_HAZARD = getResources().getString(R.string.hazard_level_moderate);
        final String HIGH_HAZARD = getResources().getString(R.string.hazard_level_high);

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

    // Gets a hazard colour bar
    private int getHazardColourBar(String hazardLevel) {
        int colourBar;

        final String MODERATE_HAZARD = getResources().getString(R.string.hazard_level_moderate);
        final String HIGH_HAZARD = getResources().getString(R.string.hazard_level_high);

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

    /**
     * Creating app buttons for action bar in the issue activity
     * Code adapted from 'John's Android Studio Tutorials':
     * https://www.youtube.com/watch?v=5MSKuVO2hV4
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_issue_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // Acquires the item selected in Action Bar.
        int id = item.getItemId();

        if(id == R.id.actionbar_back_issueActivity) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, IssueActivity.class);
    }
}
