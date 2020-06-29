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

import ca.cmpt276.model.Inspection;
import ca.cmpt276.restaurantapplication.IssueActivity;
import ca.cmpt276.restaurantapplication.R;

import static ca.cmpt276.model.Inspection.getContext;

/**
 *  Adapter for inspection list activity (called InspectionActivity.java)
 *  Used the video below to turn listview to recyclerView:
 *  https://www.youtube.com/watch?v=W29vSFYF1ts
 */
public class InspectionAdapter extends RecyclerView.Adapter<InspectionAdapter.ViewHolder> {
    private static final String EXTRA_INSPECTION = "com.cmpt276.inspection";
    private static final String EXTRA_INSPECTION_RESTAURANT = "com.cmpt276.inspectionRestaurant";
    private int restaurantIndex;
    private List<Inspection> inspections;
    private Context context;


    public InspectionAdapter(Context context, List<Inspection> inspections, int restuarantIndex) {
        this.context = context;
        this.inspections = inspections;
        this.restaurantIndex = restuarantIndex;
    }


    // Gets a hazard icon
    //https://www.flaticon.com/free-icon/complain_1041891?term=exclamation%20mark&page=1&position=20 yellow_alert picture
    //https://www.hiclipart.com/free-transparent-background-png-clipart-iyhth green_alert picture
    //https://commons.wikimedia.org/wiki/File:GHS-pictogram-exclam.svg red_alert picture
    private int getHazardIcon(String hazardLevel) {
        int hazardIcon;
        final String HIGH_HAZARD = context.getResources().getString(R.string.hazard_level_high);
        final String MODERATE_HAZARD = context.getResources().getString(R.string.hazard_level_moderate);

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
    private int getColourBar(String hazardLevel) {
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


    public class ViewHolder extends RecyclerView.ViewHolder {
        private View parentView;
        TextView display_num_critical_issues;
        TextView display_num_non_critical_issues;
        TextView display_inspection_date;
        ImageView display_hazard_colour_bar;
        ImageView display_hazard_icon;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.display_num_critical_issues = view
                    .findViewById(R.id.inspectionNumCritValue);
            this.display_num_non_critical_issues = view
                    .findViewById(R.id.inspectionNumNonCritValue);
            this.display_inspection_date = view
                    .findViewById(R.id.inspectionInspectionDateValue);
            this.display_hazard_colour_bar = view.findViewById(R.id.inspectionHazardLevelColourBar);
            this.display_hazard_icon = view.findViewById(R.id.inspectionHazardLevelIcon);
            parentView = view;
        }
    }


    @Override
    public InspectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InspectionAdapter.ViewHolder(
                LayoutInflater
                        .from(context)
                        .inflate(R.layout.recyclerview_inspections, parent, false)
        );
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Inspection inspection = inspections.get(position);

        int numCritical = inspection.getNumCritical();
        int numNonCritical = inspection.getNumNonCritical();

        String num_critical_issues = Integer.toString(numCritical);
        String num_non_critical_issues = Integer.toString(numNonCritical);
        String hazardLevel = inspection.getHazardRating();
        String tempInspectionDate = inspection.getInspectionDate();

        // Formatting the inspection date
        String inspectionDate = inspection.displayInspectionDateFormatted(tempInspectionDate);

        holder.display_num_critical_issues.setText(num_critical_issues);
        holder.display_num_non_critical_issues.setText(num_non_critical_issues);
        holder.display_inspection_date.setText(inspectionDate);

        // Checks hazardLevel then sets hazard colour bar accordingly
        int colourBar = getColourBar(hazardLevel);
        holder.display_hazard_colour_bar.setColorFilter(ContextCompat.getColor(getContext(), colourBar));
        holder.display_hazard_colour_bar.setBackgroundColor(ContextCompat.getColor(getContext(), colourBar));

        // Checks hazardLevel then sets hazard icon accordingly
        holder.display_hazard_icon.setImageResource(getHazardIcon(hazardLevel));

        // Activates when a restaurant is clicked.
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = IssueActivity.makeIntent(context); //makeIntent function should be in IssueActivity
                intent.putExtra(EXTRA_INSPECTION, position); //EXTRA_INSPECTION variable (already created) should be in InpsectionActivity
                intent.putExtra(EXTRA_INSPECTION_RESTAURANT, restaurantIndex);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return this.inspections.size();
    }
}
