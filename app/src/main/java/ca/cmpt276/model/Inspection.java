package ca.cmpt276.model;


import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ca.cmpt276.restaurantapplication.R;

/**
 * Stores information about an inspection of a restaurant
 */
public class Inspection {
    private HashMap<Integer, String> monthsMap = new HashMap<>();
    private String trackingNumber;
    private String inspectionDate;
    private String inspectionType;
    private int numCritical;
    private int numNonCritical;
    private String hazardRating;
    private String violLump;
    private boolean isInspectionWithinYear;
    private List<String> codeViolList;
    private static ArrayList<Issue> staticIssueList;
    private static Context context;

    public Inspection() {
        monthsMap.put(1, context.getString(R.string.inspection_month_january));
        monthsMap.put(2, context.getString(R.string.inspection_month_february));
        monthsMap.put(3, context.getString(R.string.inspection_month_march));
        monthsMap.put(4, context.getString(R.string.inspection_month_april));
        monthsMap.put(5, context.getString(R.string.inspection_month_may));
        monthsMap.put(6, context.getString(R.string.inspection_month_june));
        monthsMap.put(7, context.getString(R.string.inspection_month_july));
        monthsMap.put(8, context.getString(R.string.inspection_month_august));
        monthsMap.put(9, context.getString(R.string.inspection_month_september));
        monthsMap.put(10, context.getString(R.string.inspection_month_october));
        monthsMap.put(11, context.getString(R.string.inspection_month_november));
        monthsMap.put(12, context.getString(R.string.inspection_month_december));
    }

    private String translateHazardRating(String hazardRating) {
        String translatedHazardRating = "";
        if (hazardRating.equals("High")) {
            translatedHazardRating = context.getResources().getString(R.string.hazard_level_high);
        }
        else if (hazardRating.equals("Moderate")) {
            translatedHazardRating = context.getResources().getString(R.string.hazard_level_moderate);
        }
        else  {
            translatedHazardRating = context.getResources().getString(R.string.hazard_level_low);
        }
        return translatedHazardRating;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
    public void setInspectionDate(String inspectionDate) {
        this.inspectionDate = inspectionDate;
        displayInspectionDateFormatted(inspectionDate);
    }
    public void setInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;
    }
    public void setNumCritical(int numCritical) {
        this.numCritical = numCritical;
    }
    public void setNumNonCritical(int numNonCritical) {
        this.numNonCritical = numNonCritical;
    }
    public void setHazardRating(String hazardRating) {
        this.hazardRating = hazardRating;
    }
    public void setViolLump(String violLump) {
        this.violLump = violLump;
    }
    public void setCodeViolList(List<String> codeViolList) {
        this.codeViolList = codeViolList;
    }
    public static void setStaticIssueList(ArrayList<Issue> staticIssueList) {
        Inspection.staticIssueList = staticIssueList;
    }
    public static void setStaticContext(Context context) {
        Inspection.context = context;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }
    public String getInspectionDate() {
        return inspectionDate;
    }
    public String getInspectionType() {
        return inspectionType;
    }
    public int getNumCritical() {
        return numCritical;
    }
    public int getNumNonCritical() {
        return numNonCritical;
    }
    public String getHazardRating() {
        return translateHazardRating(hazardRating);
    }
    public String getViolLump() {
        return violLump;
    }
    public int getNumIssues(){return codeViolList.size();}
    public List getCodeViolList() {
        return codeViolList;
    }
    public boolean getInspectionWithinYear() {
        return isInspectionWithinYear;
    }

    public static ArrayList<Issue> getStaticIssueList() {
        return staticIssueList;
    }
    public static Context getContext() {
        return context;
    }

    public List<Issue> getIssueList() {
        List<Issue> issueList = new ArrayList<>();
        Issue addIssue;
            for (String code:codeViolList) {
                addIssue = matchCodes(Integer.parseInt(code));
                issueList.add(addIssue);
            }
        return issueList;
    }

    private Issue matchCodes(int id){
        for (Issue issue:staticIssueList) {
            if(issue.getIssueCode() == id)
                return issue;
        }
        return null;
    }

    /**
     *  Getting datetimes functions below:
     */

    // Converts inspectionDate to Date datatype
    // https://www.javatpoint.com/java-simpledateformat
    private Date getDateRestaurantFormat(String inspectionDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();

        try {
            date = formatter.parse(inspectionDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private Date getCurrentDate() {
        return new Date();
    }

    //https://stackoverflow.com/questions/20165564/calculating-days-between-two-dates-with-java
    private long getDifferenceDays(Date d1) {
        Date d2 = getCurrentDate();
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public String displayFullInspectionDate(String inspectionDate) {
        Date inspectDate = getDateRestaurantFormat(inspectionDate);

        // Formatting output
        SimpleDateFormat year_formatter = new SimpleDateFormat("yyyy");
        SimpleDateFormat months_formatter = new SimpleDateFormat("MM");
        SimpleDateFormat days_formatter = new SimpleDateFormat("dd");

        int year = Integer.parseInt(year_formatter.format(inspectDate));
        String month = monthsMap.get(Integer.parseInt(months_formatter.format(inspectDate)));
        int days = Integer.parseInt(days_formatter.format(inspectDate));

        inspectionDate = month + " " + days + ", " + year;
        return inspectionDate;
    }
    /**
     * https://stackoverflow.com/questions/7182996/java-get-month-integer-from-date
     * If inpsection date was within 30 days, tell me the number of days ago it was (such as "24 days")
     * Otherwise, if it was less than a year ago, tell me the month and day (such as "May 12")
     * Otherwise, tell me just the month and year (such as "May 2018")
     */
    public String displayInspectionDateFormatted(String inspectionDate) {

         Date inspectDate = getDateRestaurantFormat(inspectionDate);

        // Difference in days between inspection date and current date
        long numDays = getDifferenceDays(inspectDate);

        SimpleDateFormat year_formatter = new SimpleDateFormat("yyyy");
        SimpleDateFormat months_formatter = new SimpleDateFormat("MM");
        SimpleDateFormat days_formatter = new SimpleDateFormat("dd");

        int year = Integer.parseInt(year_formatter.format(inspectDate));
        String month = monthsMap.get(Integer.parseInt(months_formatter.format(inspectDate)));
        int days = Integer.parseInt(days_formatter.format(inspectDate));

        if (numDays <= 30) {
            if (numDays == 1){
                inspectionDate = numDays + context.getString(R.string.inspection_day);
            }
            else{
                inspectionDate = numDays + context.getString(R.string.inspection_days);
            }
            this.isInspectionWithinYear = true;
        }

        else if (numDays <= 365) {
            inspectionDate =  month + " " + days;
            this.isInspectionWithinYear = true;
        }

        else {
            inspectionDate = month + " " + year;
            this.isInspectionWithinYear = false;
        }
        return inspectionDate;
    }

    public Inspection clone(){
        Inspection cloned = new Inspection();
        cloned.setTrackingNumber(trackingNumber);
        cloned.setInspectionDate(inspectionDate);
        cloned.setInspectionType(inspectionType);
        cloned.setNumCritical(numCritical);
        cloned.setNumNonCritical(numNonCritical);
        cloned.setHazardRating(hazardRating);
        cloned.setViolLump(violLump);
        cloned.setCodeViolList(codeViolList);
        return cloned;
    }

}
