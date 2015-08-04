package nu.pto.androidapp.base.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import nu.pto.androidapp.base.BaseActivity;
import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.Updateable;
import nu.pto.androidapp.base.db.dao.WorkoutSessionDao;
import nu.pto.androidapp.base.ui.statistics.StatisticsChartView;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.TreeMap;


public class StatisticsChartFragment extends BaseFragment implements Updateable {
    public StatisticsChartFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.statistics_chart_layout, container, false);

        getBaseActivity().setTitle(getString(R.string.fragment_title_statistics));
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        updateView();
    }

    @Override
    public void updateView() {

        LinkedHashMap<String, Integer> statisticsHashMap = new LinkedHashMap<String, Integer>();
        TreeMap<Long, Integer> yearWeeksCounts = new TreeMap<Long, Integer>();

        try {
            WorkoutSessionDao workoutSessionDao = getBaseActivity().getDatabaseHelper().getWorkoutSessionDao();

            String queryString = "SELECT " +
                    "strftime('%Y%W', created_date,'unixepoch') as yearweek, COUNT(*) as count, created_date " +
                    "FROM workout_session  " +
                    "GROUP BY yearweek " +
                    "ORDER BY yearweek ASC";

            Cursor cursor = getBaseActivity().getDatabaseHelper().getReadableDatabase().rawQuery(queryString, null);

            int rowsCount = cursor.getCount();
            if (rowsCount >= 2) {

                Calendar calendar = new GregorianCalendar();

                for (int i = 0; i < rowsCount; i++) {
                    cursor.moveToPosition(i);
                    long timestamp = Long.parseLong(cursor.getString(2));
                    int count = cursor.getInt(1);
                    yearWeeksCounts.put(timestamp * 1000, count);
                }

                cursor.moveToFirst();
                long timestamp = Long.parseLong(cursor.getString(2));
                cursor.moveToLast();
                long lastDate = Long.parseLong(cursor.getString(2));


                calendar.setTimeInMillis(timestamp * 1000);

                int weekOfYear;
                int year;


                Calendar lastCalendar = new GregorianCalendar();
                lastCalendar.setTimeInMillis(lastDate * 1000);
                int lastWeekOfYear = lastCalendar.get(Calendar.WEEK_OF_YEAR);
                int lastYear = lastCalendar.get(Calendar.YEAR);

                boolean breakWhile = false;

                if (rowsCount != 1) {
                    do {
                        if (yearWeeksCounts.get(timestamp * 1000) == null) {
                            yearWeeksCounts.put(timestamp * 1000, 0);
                        }
                        timestamp = timestamp + (7 * 24 * 60 * 60);

                        calendar.setTimeInMillis(timestamp * 1000);

                        weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
                        year = calendar.get(Calendar.YEAR);

                        if (lastWeekOfYear == weekOfYear && year == lastYear) {
                            breakWhile = true;
                        }

                    } while (!breakWhile);
                }

                for (Long millis : yearWeeksCounts.keySet()) {
                    calendar.setTimeInMillis(millis);

                    Integer _year = calendar.get(Calendar.YEAR);
                    Integer _weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);

                    int count = yearWeeksCounts.get(millis);

                    String key = _year.toString() + _weekOfYear.toString();
                    Integer value = statisticsHashMap.get(key);

                    if (value == null) {
                        statisticsHashMap.put(key, count);
                    } else {
                        if (value == 0) {
                            statisticsHashMap.remove(key);
                            statisticsHashMap.put(key, count);
                        } else {
                            continue;
                        }
                    }

                    Log.d("___" + BaseActivity.LOG_TAG, key + "-" + count);

                }

            } else {
                getActivity().findViewById(R.id.viewStatisticsChartView).setVisibility(View.GONE);
                getActivity().findViewById(R.id.tv_not_enough_data).setVisibility(View.VISIBLE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        StatisticsChartView statisticsChartView = (StatisticsChartView) getActivity().findViewById(R.id.viewStatisticsChartView);
        statisticsChartView.setStatisticsHashMap(statisticsHashMap);
    }
}
