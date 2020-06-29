package ca.cmpt276.model;

import android.content.Context;

import java.util.Hashtable;

import ca.cmpt276.restaurantapplication.R;

/**
 * Stores information about an issue
 */
public class Issue {

    private int issueCode;
    private String whetherCritical;
    private String whetherRepeat;
    private String description;
    private String violationType;
    private String briefDescription;
    private static Context context;

    public static void setStaticContext(Context context) {
        Issue.context = context;
    }

    private String translateCritOrNonCrit(String severity) {
        String translatedSeverity = "";

        if (severity.equals("Critical")) {
            translatedSeverity = context.getResources().getString(R.string.issue_severity_critical);
        }
        else {
            translatedSeverity = context.getResources().getString(R.string.issue_severity_notcritical);
        }
        return translatedSeverity;
    }

    public void setIssueCode(int issueCode) {
        this.issueCode = issueCode;
    }
    public void setWhetherCritical(String whetherCritical) {
        this.whetherCritical = whetherCritical;
    }
    public void setWhetherRepeat(String whetherRepeat) {
        this.whetherRepeat = whetherRepeat;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }
    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public int getIssueCode() {
        return issueCode;
    }
    public String getWhetherCritical() {
        return translateCritOrNonCrit(whetherCritical);
    }
    public String getWhetherRepeat() {
        return whetherRepeat;
    }
    public String getDescription() {
        return description+", "+whetherRepeat;
    }
    public String getViolationType() {
        return violationType;
    }
    public String getBriefDescription() {
        return briefDescription;
    }


    void getDescriptionAndTypeById(Hashtable<Integer,String> violTypesList, Context context){
        if(violTypesList.containsKey(issueCode)){
            String[] splitter = violTypesList.get(issueCode).split("\\|");
            this.violationType = splitter[0];
            this.briefDescription = splitter[1];
        }
        else{
            this.violationType = context.getString(R.string.unmatchedIssueCode);
            this.briefDescription = context.getString(R.string.unmatchedIssueCode);
        }
    }


}
