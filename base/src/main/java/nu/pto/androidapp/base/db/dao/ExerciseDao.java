package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nu.pto.androidapp.base.db.model.Exercise;


public class ExerciseDao extends BaseDaoImpl<Exercise, Integer> {

    public ExerciseDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Exercise.class);
    }


    /**
     * @param serverExerciseId exercise server id
     * @return exercise
     */
    public Exercise getExerciseByServerExerciseId(int serverExerciseId) throws SQLException {
        PreparedQuery<Exercise> preparedQuery = queryBuilder().where().eq("server_exercise_id", serverExerciseId).prepare();
        return this.queryForFirst(preparedQuery);
    }

    /**
     * get exercises assigned to workout by serverWorkoutId
     */
    public List<Exercise> getExercisesByServerWorkoutId(int serverWorkoutId) throws SQLException {
        PreparedQuery<Exercise> preparedQuery = queryBuilder().orderBy("order", true).where().eq("server_workout_id", serverWorkoutId).prepare();
        return this.query(preparedQuery);
    }

    /**
     * get exercises assigned to workout by workoutId
     */
    public ArrayList<Exercise> getExercisesByWorkoutId(int workoutId) throws SQLException {
        // TODO check for usage
        PreparedQuery<Exercise> preparedQuery = queryBuilder().orderBy("order", true).orderBy("server_exercise_id", false).where().eq("workout_id", workoutId).and().eq("deleted", 0).prepare();
        return (ArrayList) this.query(preparedQuery);
    }

//    public ArrayList<Exercise> getExercisesByWorkoutIdWithExistingSetSession(int workoutId) throws SQLException {
//        // TODO do not show exercises without set
//        SetDao setDao = new SetDao(getConnectionSource());
//
//        PreparedQuery<Exercise> preparedQuery = queryBuilder().orderBy("order", true).where().eq("workout_id", workoutId).and().eq("deleted", 0).prepare();
//        ArrayList<Exercise> exercises = (ArrayList) this.query(preparedQuery);
//        ArrayList<Exercise> returnExercises = new ArrayList<Exercise>();
//        for (Exercise exercise : exercises) {
//            ArrayList<Set> sets = setDao.getSetsByExerciseId(exercise.exerciseId);
//            if (sets != null && sets.size() > 1) {
//                returnExercises.add(exercise);
//            }
//        }
//
//        exercises.clear();
//        return returnExercises;
//    }

    public String getFirstExerciseImageIdByWorkoutId(int workoutId) throws SQLException {
        PreparedQuery<Exercise> preparedQuery = queryBuilder().limit(10).orderBy("order", true).where().eq("workout_id", workoutId).prepare();
        ArrayList<Exercise> firstNExercise = (ArrayList) this.query(preparedQuery);
        for (Exercise exercise : firstNExercise) {
            String[] imageIds = exercise.imagesIds.split("#|#");
            String imageId = "";
            if (imageIds.length > 0) {
                // check for valid mongo id
                imageId = imageIds[0];
                if (imageId.length() == 24) {
                    return imageId;
                }
            }
        }
        return " ";
    }

    public Exercise getExerciseByExerciseId(int exerciseId) throws SQLException {
        PreparedQuery<Exercise> preparedQuery = queryBuilder().where().eq("exercise_id", exerciseId).prepare();
        return this.queryForFirst(preparedQuery);
    }
}
