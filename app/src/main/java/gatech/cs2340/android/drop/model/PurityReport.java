package gatech.cs2340.android.drop.model;

public class PurityReport {
    //public variable required by FireBase
    public String _date;
    public String _reportNum;
    public String _worker;
    public String _latitude;
    public String _longitude;
    public String _overallCond;
    public String _virusPPM;
    public String _contaminant;

    public PurityReport() {

    }

    public PurityReport(String data, String reportNum, String worker, String latitude, String longitude,
                        String overallCond, String virusPPM, String contaminant){
        _date = data;
        _reportNum = reportNum;
        _worker = worker;
        _latitude = latitude;
        _longitude = longitude;
        _overallCond = overallCond;
        _virusPPM = virusPPM;
        _contaminant = contaminant;

    }
}
