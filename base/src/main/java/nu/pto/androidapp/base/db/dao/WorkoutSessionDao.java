package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nu.pto.androidapp.base.db.model.WorkoutSession;
import nu.pto.androidapp.base.util.SyncStatus;


public class WorkoutSessionDao extends BaseDaoImpl<WorkoutSession, Integer> {

    public WorkoutSessionDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, WorkoutSession.class);
    }


    /**
     * @param serverWorkoutSessionId
     * @return
     */
    public WorkoutSession getWorkoutSessionByServerWorkoutSessionId(int serverWorkoutSessionId) throws SQLException {
        PreparedQuery<WorkoutSession> preparedQuery = queryBuilder().where().eq("server_workout_session_id", serverWorkoutSessionId).prepare();
        return this.queryForFirst(preparedQuery);
    }

    public WorkoutSession getWorkoutSessionByWorkoutSessionId(int workoutSessionId) throws SQLException {
        PreparedQuery<WorkoutSession> preparedQuery = queryBuilder().where().eq("workout_session_id", workoutSessionId).prepare();
        return this.queryForFirst(preparedQuery);
    }


    public ArrayList<WorkoutSession> getWorkoutSessionsByServerClientId(int serverClientId) throws SQLException {
        PreparedQuery<WorkoutSession> preparedQuery = queryBuilder().where().eq("server_client_id", serverClientId).and().eq("deleted", 0).prepare();
        return (ArrayList) this.query(preparedQuery);
    }

    public ArrayList<WorkoutSession> getWorkoutSessionsByClientId(int clientId) throws SQLException {
        PreparedQuery<WorkoutSession> preparedQuery = queryBuilder().orderBy("created_date", false).where().eq("client_id", clientId).and().eq("deleted", 0).prepare();
        return (ArrayList) this.query(preparedQuery);
    }


    public List<WorkoutSession> getUpdatedOrNewWorkoutSessions() throws SQLException {
        PreparedQuery<WorkoutSession> preparedQuery = queryBuilder().where().ne("sync_status", SyncStatus.SYNCED).prepare();
        return this.query(preparedQuery);
    }

}
