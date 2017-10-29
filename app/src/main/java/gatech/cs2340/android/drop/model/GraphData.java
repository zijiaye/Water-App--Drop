package gatech.cs2340.android.drop.model;

public class GraphData {
    private int count;
    private double total;

    public GraphData() {
        count = 0;
        total = 0;
    }

    public void addGraphData(String PPM) {
        total += Double.parseDouble(PPM);
        count++;
    }

    public double getAvgPPM() {
        if (count == 0) {
            return 0;
        } else {
            //NumberFormat formatter = new DecimalFormat("#0.00");
            //return Double.parseDouble(formatter.format(total/count));
            return total/count;
        }
    }
}
