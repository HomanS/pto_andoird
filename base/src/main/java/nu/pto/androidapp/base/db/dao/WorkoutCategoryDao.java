package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import nu.pto.androidapp.base.db.model.WorkoutCategory;


public class WorkoutCategoryDao extends BaseDaoImpl<WorkoutCategory, Integer> {

    public WorkoutCategoryDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, WorkoutCategory.class);
    }


    /**
     * @param workoutCategoryId
     * @return
     */
    public WorkoutCategory getWorkoutCategoryByServerWorkoutCategoryId(int workoutCategoryId) throws SQLException {
        PreparedQuery<WorkoutCategory> preparedQuery = null;
        preparedQuery = queryBuilder().where().eq("server_workout_category_id", workoutCategoryId).prepare();
        WorkoutCategory workoutCategory = this.queryForFirst(preparedQuery);
        return workoutCategory;
    }
}
