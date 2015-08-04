package nu.pto.androidapp.base.ui.statistics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import nu.pto.androidapp.base.R;


public class StatisticsChartView extends View {

    int Y_NUMBERS_MAX_COUNT;

    private LinkedHashMap<String, Integer> statisticsHashMap = new LinkedHashMap<String, Integer>();

    public void setStatisticsHashMap(LinkedHashMap<String, Integer> statisticsHashMap) {
        this.statisticsHashMap = statisticsHashMap;
        Y_NUMBERS_MAX_COUNT = getResources().getInteger(R.integer.statistics_y_numbers_max_count);
    }

    Paint paintValues = new Paint();
    Paint paintAxes = new Paint();

    public StatisticsChartView(Context context) {
        super(context);

    }

    public StatisticsChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatisticsChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public View getView() {
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        paintValues.setColor(Color.rgb(20, 20, 20));
        paintValues.setStyle(Paint.Style.FILL);
        paintValues.setStyle(Paint.Style.STROKE);
        paintValues.setStrokeWidth(3);
        paintValues.setStyle(Paint.Style.FILL);
        paintValues.setAntiAlias(true);
        paintValues.setTextSize(getResources().getDimensionPixelSize(R.dimen.tv_size_statistics));

        paintAxes.setColor(Color.rgb(20, 20, 20));
        paintAxes.setAntiAlias(true);
        paintAxes.setTextSize(getResources().getDimensionPixelSize(R.dimen.tv_axe_statistics));


        int parentWidth = this.getMeasuredWidth();
        int parentHeight = this.getMeasuredHeight();


        StatisticsChartAdapter chartAdapter = new StatisticsChartAdapter(statisticsHashMap, parentWidth, parentHeight, getResources().getDimensionPixelSize(R.dimen.margin_statistics));
        ArrayList<StatisticsChartPoint> chartPoints = chartAdapter.getChartPoints();

        StatisticsChartPoint startPoint;
        if (chartPoints.size() > 0) {
            startPoint = chartPoints.get(0);
        } else {
            startPoint = new StatisticsChartPoint(0, 0, 0, "0");
        }


        if (chartPoints.size() != 0) {
            int xAxeScale;
            if (chartPoints.size() > Y_NUMBERS_MAX_COUNT) {
                xAxeScale = chartPoints.size() / Y_NUMBERS_MAX_COUNT + 1;
            } else {
                xAxeScale = 1;
            }
            int counter = 1;
            for (StatisticsChartPoint chartPoint : chartPoints) {
                canvas.drawLine(startPoint.getXCoordinate(), startPoint.getYCoordinate(), chartPoint.getXCoordinate(), chartPoint.getYCoordinate(), paintValues);
                startPoint = chartPoint;

                if (chartPoint.getValue() != 0) {
                    canvas.drawCircle(startPoint.getXCoordinate(), startPoint.getYCoordinate(), getResources().getDimensionPixelSize(R.dimen.circle_radius_statistics), paintValues);

                }
                if (counter == xAxeScale) {
                    canvas.drawText(startPoint.getLabel(), startPoint.getXCoordinate() - 15f, parentHeight - chartAdapter.getMargin() + getResources().getDimensionPixelSize(R.dimen.padding_y_axe_statistics), paintValues);
                    counter = 1;
                } else {
                    counter++;
                }
            }
            // finally x draw axe text
            canvas.drawText(chartAdapter.getxAxis(), startPoint.getXCoordinate() + chartAdapter.getMargin() / 2, parentHeight - chartAdapter.getMargin() + getResources().getDimensionPixelSize(R.dimen.padding_y_axe_statistics), paintAxes);
            int yAxeScale;
            if (chartAdapter.getMaxY() > Y_NUMBERS_MAX_COUNT) {
                yAxeScale = chartAdapter.getMaxY() / Y_NUMBERS_MAX_COUNT + 1;
            } else {
                yAxeScale = 1;
            }
            int yTemp = chartAdapter.getMaxY() % yAxeScale;
            int startYValue = 1;

            for (int i = startYValue; i <= chartAdapter.getMaxY(); i++) {
                if (i % yAxeScale == yTemp) {
                    canvas.drawText(Integer.toString(i), chartAdapter.getMargin() / 5, parentHeight - chartAdapter.getMargin() - chartAdapter.getCanvasScaleY() * i + getResources().getDimensionPixelSize(R.dimen.circle_radius_statistics), paintValues);
                }
            }
            // finally y draw axe text
            canvas.drawText(chartAdapter.getyAxis(), chartAdapter.getMargin() / 5, parentHeight - chartAdapter.getMargin() - chartAdapter.getCanvasScaleY() * chartAdapter.getMaxY() - getResources().getDimensionPixelSize(R.dimen.padding_y_axe_statistics), paintAxes);
            if (chartPoints.size() == 1) {
                canvas.drawCircle(parentWidth / 2, chartAdapter.getMargin(), getResources().getDimensionPixelSize(R.dimen.circle_radius_statistics), paintValues);
                canvas.drawText(startPoint.getLabel(), parentWidth / 2, parentHeight - chartAdapter.getMargin(), paintValues);
                canvas.drawText(chartAdapter.getxAxis(), chartAdapter.getCanvasWidth(), parentHeight - chartAdapter.getMargin(), paintAxes);
            }
        } else {
            canvas.drawText(chartAdapter.getxAxis(), chartAdapter.getCanvasWidth(), chartAdapter.getCanvasHeight(), paintAxes);
            canvas.drawText(chartAdapter.getyAxis(), chartAdapter.getMargin(), chartAdapter.getMargin(), paintAxes);
        }


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
    }
}