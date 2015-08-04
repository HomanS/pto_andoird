package nu.pto.androidapp.base.ui.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class StatisticsChartAdapter {

    private String yAxis = "ggr";
    private String xAxis = "v.";
    private int maxY = 0;
    private int minY = 100000;
    private int margin;

    public int getMaxY() {
        return maxY;
    }

    public int getMinY() {
        return minY;
    }

    public String getyAxis() {
        return yAxis;
    }

    public String getxAxis() {
        return xAxis;
    }

    public float getCanvasScaleX() {
        return canvasScaleX;
    }

    public float getCanvasScaleY() {
        return canvasScaleY;
    }

    private float canvasScaleX;
    private float canvasScaleY;
    private float canvasWidth;

    public float getCanvasWidth() {
        return canvasWidth;
    }

    public float getCanvasHeight() {
        return canvasHeight;
    }

    private float canvasHeight;
    private ArrayList<StatisticsChartPoint> chartPoints;

    public StatisticsChartAdapter(Map<String, Integer> statisticsHashMap, int w, int h, int margin) {
        this.margin = margin;
        chartPoints = new ArrayList<StatisticsChartPoint>();
        this.canvasHeight = h - this.margin - this.margin;
        this.canvasWidth = w - this.margin - this.margin;

        for (HashMap.Entry<String, Integer> entry : statisticsHashMap.entrySet()) {
            if (entry.getValue() > maxY) {
                maxY = entry.getValue();
            }
            if (entry.getValue() < minY) {
                minY = entry.getValue();
            }

        }
        this.canvasScaleX = this.canvasWidth / (statisticsHashMap.size() - 1);
        this.canvasScaleY = this.canvasHeight / maxY;
        int counter = 0;

        for (HashMap.Entry<String, Integer> entry : statisticsHashMap.entrySet()) {
            StatisticsChartPoint chartPoint = new StatisticsChartPoint(counter * canvasScaleX + margin, canvasHeight - entry.getValue() * canvasScaleY + margin, entry.getValue(), entry.getKey());

            chartPoints.add(chartPoint);
            counter++;
        }
    }

    public ArrayList<StatisticsChartPoint> getChartPoints() {
        return this.chartPoints;
    }

    public float getMargin() {
        return margin;
    }

}
