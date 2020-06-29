package ca.cmpt276.restaurantapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.cmpt276.model.Issue;
import ca.cmpt276.restaurantapplication.R;

import static ca.cmpt276.model.Inspection.getContext;


/**
 * Adapter for issue list activity (called IssueActivity.java)
 * Used the video below to turn listview to recyclerView:
 * https://www.youtube.com/watch?v=W29vSFYF1ts
 */
public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.ViewHolder> {
    private int restaurantIndex;
    private int inspectionIndex;
    private List<Issue> issues;
    private Context context;

    public IssueAdapter(Context context, List<Issue> issues, int restuarantIndex, int inspectionIndex) {
        this.context = context;
        this.issues = issues;
        this.restaurantIndex = restuarantIndex;
        this.inspectionIndex = inspectionIndex;
    }

    // Checks the violationType and returns the resourceID of the correct picture to be set.
    //Building type icon: https://www.flaticon.com/free-icon/store_1802257?term=restaurant%20building&page=1&position=40
    //Equipment type icon: https://www.flaticon.com/free-icon/food_608857?term=cutlery&page=1&position=14
    //Food type icon: https://www.flaticon.com/free-icon/fruits_1546582?term=food%20box&page=3&position=18&fbclid=IwAR2IkrkvswyD6z22tX84TiurG2VTvRfwyTSdMJmom8gUVwUHdeFTvHQt9uA
    //Pests type icon: https://www.flaticon.com/free-icon/cockroach_597515?term=cockroach&page=1&position=3
    //Staff type icon:https://www.flaticon.com/free-icon/maid_471778?term=staff&page=1&position=23
    //Permit type icon: https://www.flaticon.com/free-icon/contract_684930?term=paper&page=1&position=74
    private int getIssueViolationTypePicture(String violationType) {
        int resourceID;

        if(violationType.equals("Building")) {
            resourceID = R.drawable.building_type_icon;
        }
        else if(violationType.equals("Equipment")) {
            resourceID = R.drawable.equipment_type_icon;
        }
        else if (violationType.equals("Food")) {
            resourceID = R.drawable.food_type_icon;
        }
        else if(violationType.equals("Staff")) {
            resourceID = R.drawable.staff_type_icon;
        }
        else if(violationType.equals("Permit")) {
            resourceID = R.drawable.permit_type_icon;
        }
        else {
            resourceID = R.drawable.pests_type_icon;
        }
        return resourceID;
    }

    // Checks whether issue is critical or non-critical then returns resourceID of a picture depending on the severity.
    // Critical picture found from: https://www.flaticon.com/free-icon/report_1632752?term=exclamation&page=1&position=55&fbclid=IwAR2cL1m2CNr8UWPrSxhjfnfdus9KiEoABzU2f2guY6bjqeeWOSp-tPi8YUU
    // Non-Critical picture found from: https://www.flaticon.com/free-icon/exclamation_1636062?term=warning&page=1&position=63&fbclid=IwAR0TNs_m6UwRbhrdaighUG_40SZUGa-rutOYFDlUGDTs32_E-c9eQD5Tdz4
    private int getIssueSeverityPicture(String issueSeverity) {
        int resourceID;

        final String ISSUE_SEVERITY_CRITICAL = context.getResources()
                .getString(R.string.issue_severity_critical);

        if(issueSeverity.equals(ISSUE_SEVERITY_CRITICAL)) {
            resourceID = R.drawable.critical_alert;
        }
        else {
            resourceID = R.drawable.noncritical_alert;
        }
        return resourceID;
    }

    // Gets the critical or non-crticial colour bar depending on the whetherCritical param.
    private int getIssueSeverityColourBar(String whetherCritical) {
        int colourBar;

        final String ISSUE_SEVERITY_CRITICAL = context.getResources()
                .getString(R.string.issue_severity_critical);

        if(whetherCritical.equals(ISSUE_SEVERITY_CRITICAL)) {
            colourBar = R.color.redBar;
        }
        else {
            colourBar =  R.color.orangeBar;
        }

        return colourBar;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View parentView;
        TextView display_issue_severity;
        ImageView display_issue_severity_icon;
        ImageView display_issue_severity_colourbar;
        TextView display_issue_violation_type;
        ImageView display_issue_violation_type_icon;
        TextView display_issue_brief_description;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.display_issue_severity = (TextView) view
                    .findViewById(R.id.issueSeverity);
            this.display_issue_severity_icon = view
                    .findViewById(R.id.issueSeverityIcon);
            this.display_issue_severity_colourbar = view
                    .findViewById(R.id.issueSeverityColourBar);
            this.display_issue_violation_type = view.findViewById(R.id.issueViolationTypeValue);
            this.display_issue_violation_type_icon = view.findViewById(R.id.issueViolationTypeIcon);
            this.display_issue_brief_description = view.findViewById(R.id.issueBriefDescription);
            parentView = view;
        }
    }


    @Override
    public IssueAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new IssueAdapter.ViewHolder(
                LayoutInflater
                        .from(context)
                        .inflate(R.layout.recyclerview_issue, parent, false)
        );
    }


    @Override
    public void onBindViewHolder(IssueAdapter.ViewHolder holder, final int position) {
        final Issue issue = issues.get(position);

        String issueSeverity = issue.getWhetherCritical();
        String briefDescription = issue.getBriefDescription();
        String violationType = issue.getViolationType();


        // Setting issue severity items
        holder.display_issue_severity.setText(issueSeverity);
        holder.display_issue_severity_icon.setImageResource(getIssueSeverityPicture(issueSeverity));
        // Checks issueSeverity then sets issueSeverity colour bar accordingly
        int colourBar = getIssueSeverityColourBar(issueSeverity);
        holder.display_issue_severity_colourbar.setColorFilter(ContextCompat.getColor(getContext(), colourBar));
        holder.display_issue_severity_colourbar.setBackgroundColor(ContextCompat.getColor(getContext(), colourBar));

        // Set violation items (nature of violation)
        holder.display_issue_violation_type.setText(getTranslatedType(violationType));
        holder.display_issue_violation_type_icon.setImageResource(getIssueViolationTypePicture(violationType));

        holder.display_issue_brief_description.setText(briefDescription);

        // Activates when a restaurant is clicked.
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = issue.getDescription();
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getTranslatedType(String inspectionType){
        if(inspectionType.equals("Building")) {
            return context.getString(R.string.type_building);
        }
        else if(inspectionType.equals("Equipment")) {
            return context.getString(R.string.type_equipment);
        }
        else if (inspectionType.equals("Food")) {
            return context.getString(R.string.type_food);
        }
        else if(inspectionType.equals("Staff")) {
            return context.getString(R.string.type_staff);
        }
        else if(inspectionType.equals("Permit")) {
            return context.getString(R.string.type_permit);
        }
        else {
            return context.getString(R.string.type_pests);
        }
    }

    @Override
    public int getItemCount() {
        return this.issues.size();
    }
}