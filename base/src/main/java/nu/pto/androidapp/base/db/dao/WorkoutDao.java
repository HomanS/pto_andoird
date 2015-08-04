package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;

import nu.pto.androidapp.base.db.model.Workout;


public class WorkoutDao extends BaseDaoImpl<Workout, Integer> {

    public WorkoutDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Workout.class);
    }


    /**
     * @param serverWorkoutId
     * @return
     */
    public Workout getWorkoutByServerWorkoutId(int serverWorkoutId) throws SQLException {
        PreparedQuery<Workout> preparedQuery = queryBuilder().where().eq("server_workout_id", serverWorkoutId).prepare();
        return this.queryForFirst(preparedQuery);
    }

    /**
     * @param id
     * @return
     */
    public Workout getWorkoutById(int id) throws SQLException {
        PreparedQuery<Workout> preparedQuery = queryBuilder().where().eq("workout_id", id).prepare();
        return this.queryForFirst(preparedQuery);
    }

    /**
     * return n last workouts for start page last activities view
     *
     * @param n
     * @param clientId
     * @return
     * @throws java.sql.SQLException
     */
    public ArrayList<Workout> getClientLastNWorkouts(int n, int clientId) throws SQLException {
        PreparedQuery<Workout> preparedQuery = queryBuilder()
                .orderBy("created_date", false)
                .limit((long) n)
                .where().eq("client_id", clientId)
                .and().eq("deleted", 0)
                .prepare();

        return (ArrayList) this.query(preparedQuery);
    }

    /**
     * returns workouts assigned to specified client by clientId
     *
     * @param clientId
     * @return
     * @throws java.sql.SQLException
     */

    public ArrayList<Workout> getWorkoutsByClientId(int clientId) throws SQLException {
        PreparedQuery<Workout> preparedQuery = queryBuilder().orderBy("order", true).where().eq("client_id", clientId).and().eq("deleted", 0).prepare();
        ArrayList<Workout> workouts = (ArrayList) this.query(preparedQuery);
        return workouts;
    }

    public ArrayList<Workout> getWorkoutsByClientIdAndExistingSets(int clientId) throws SQLException {
        PreparedQuery<Workout> preparedQuery = queryBuilder().orderBy("order", true).orderBy("created_date", false).orderBy("workout_name", true).where().eq("client_id", clientId).and().eq("deleted", 0).prepare();
        ArrayList<Workout> workouts = (ArrayList) this.query(preparedQuery);
        ArrayList<Workout> workoutsWithExistingSets = new ArrayList<Workout>();
        if (workouts != null) {
            for (Workout workout : workouts) {
                SetDao setDao = new SetDao(connectionSource);
                int numRows = (int) setDao.queryBuilder().where().eq("workout_id", workout.workoutId).and().eq("deleted", 0).countOf();
                if (numRows > 0) {
                    workoutsWithExistingSets.add(workout);
                }
            }
        }


//        QueryBuilder<Workout,Integer> queryBuilderWorkout = queryBuilder();
//        queryBuilderWorkout.orderBy("order", true).where().eq("client_id", clientId).and().eq("deleted", 0);
//        SetDao setDao = new SetDao(connectionSource);
//        QueryBuilder<Set,Integer> queryBuilderSet = setDao.queryBuilder();
//        qu
//        ArrayList<Workout> workouts = (ArrayList)queryBuilderWorkout.join(queryBuilderSet).query();
        return workoutsWithExistingSets;
    }

    public ArrayList<Workout> getWorkoutsWithDeletedByClientId(int clientId) throws SQLException {
        PreparedQuery<Workout> preparedQuery = queryBuilder().orderBy("order", true).where().eq("client_id", clientId).prepare();
        ArrayList<Workout> workouts = (ArrayList) this.query(preparedQuery);
        return workouts;
    }
}
