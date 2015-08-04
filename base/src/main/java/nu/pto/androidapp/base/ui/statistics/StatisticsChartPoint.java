package nu.pto.androidapp.base.ui.statistics;


public class StatisticsChartPoint {
    private float xCoordinate;
    private float yCoordinate;
    private String label;
    private int value;

    public float getXCoordinate() {
        return xCoordinate;
    }

    public float getYCoordinate() {
        return yCoordinate;
    }

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public StatisticsChartPoint(float x, float y, int value, String label) {
        this.xCoordinate = x;
        this.yCoordinate = y;
        if (label == null || label.isEmpty() || label.length() < 4) {
            this.label = "";
        } else {
            this.label = label.substring(4);
        }

        this.value = value;
    }
}
