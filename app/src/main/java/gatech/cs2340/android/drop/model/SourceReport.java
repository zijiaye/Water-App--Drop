package gatech.cs2340.android.drop.model;

public class SourceReport {
    //public variable required by FireBase
    public String _date;
    public String _reportNum;
    public String _reporter;
    public String _latitude;
    public String _longitude;
    public String _waterType;
    public String _waterCondition;

    public SourceReport() {

    }

    public SourceReport(String date, String reportNum, String reporter, String latitude,
                        String longitude, String waterType, String waterCondition) {
        _date = date;
        _reportNum = reportNum;
        _reporter = reporter;
        _latitude = latitude;
        _longitude = longitude;
        _waterType = waterType;
        _waterCondition = waterCondition;
    }
}
