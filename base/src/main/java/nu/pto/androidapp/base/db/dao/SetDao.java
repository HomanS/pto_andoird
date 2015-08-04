package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;

import nu.pto.androidapp.base.db.model.Set;


public class SetDao extends BaseDaoImpl<Set, Integer> {

    public SetDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Set.class);
    }


    /**
     * @param serverSetId exercise server id
     * @return exercise
     */
    public Set getSetByServerSetId(int serverSetId) throws SQLException {
        PreparedQuery<Set> preparedQuery = queryBuilder().where().eq("server_set_id", serverSetId).prepare();
        return this.queryForFirst(preparedQuery);
    }

    /**
     * get all sets assigned to exercise by server_exercise_id : selectedExerciseServerId
     *
     * @param selectedExerciseServerId
     * @return
     * @throws java.sql.SQLException
     */
    public ArrayList<Set> getSetsByExerciseServerId(int selectedExerciseServerId) throws SQLException {
        PreparedQuery<Set> preparedQuery = queryBuilder().where().eq("server_exercise_id", selectedExerciseServerId).prepare();
        return (ArrayList) this.query(preparedQuery);
    }

    public ArrayList<Set> getSetsByExerciseId(int selectedExerciseId) throws SQLException {
        PreparedQuery<Set> preparedQuery = queryBuilder().where().eq("exercise_id", selectedExerciseId).prepare();
        ArrayList<Set> sets = (ArrayList) this.query(preparedQuery);
        return sets;
    }

    public Set getSetBySetId(int setId) throws SQLException {
        PreparedQuery<Set> preparedQuery = queryBuilder().where().eq("set_id", setId).prepare();
        return this.queryForFirst(preparedQuery);
    }
}
