package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nu.pto.androidapp.base.db.model.Client;
import nu.pto.androidapp.base.db.model.Set;
import nu.pto.androidapp.base.db.model.SetSession;
import nu.pto.androidapp.base.util.SyncStatus;


public class SetSessionDao extends BaseDaoImpl<SetSession, Integer> {

    public SetSessionDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, SetSession.class);
    }


    /**
     * @param serverSetSessionId ses session's server id
     * @return set session
     */
    public SetSession getSetSessionByServerSetSessionId(int serverSetSessionId) throws SQLException {
        PreparedQuery<SetSession> preparedQuery = queryBuilder().where().eq("server_set_session_id", serverSetSessionId).prepare();
        return this.queryForFirst(preparedQuery);
    }


    public SetSession getSetSessionBySetSessionId(int setSessionId) throws SQLException {
        PreparedQuery<SetSession> preparedQuery = queryBuilder().where().eq("set_session_id", setSessionId).prepare();
        return this.queryForFirst(preparedQuery);
    }


    public SetSession getSetSessionByServerSetId(Set set, Client client, int workoutServerId) throws SQLException {
        PreparedQuery<SetSession> preparedQuery = queryBuilder().where().eq("server_set_id", set.serverSetId).prepare();
        SetSession newSetSession = this.queryForFirst(preparedQuery);
        if (newSetSession == null) {
            // clone and create
            newSetSession = new SetSession();
            newSetSession.serverClientId = client.serverClientId;
            newSetSession.serverSetId = set.serverSetId;
            newSetSession.serverWorkoutSessionId = workoutServerId;
            newSetSession.repetitions = set.repetitions;
            newSetSession.weight = set.weight;
            newSetSession.weightUnit = set.weightUnit;
            newSetSession.time = set.time;
            newSetSession.timeUnit = set.timeUnit;
            newSetSession.rest = set.rest;
            newSetSession.restUnit = set.restUnit;
            newSetSession.distance = set.distance;
            newSetSession.distanceUnit = set.distanceUnit;
            this.create(newSetSession);
        }
        return newSetSession;
    }

    public SetSession getSetSessionBySetId(int setId) throws SQLException {
        PreparedQuery<SetSession> preparedQuery = queryBuilder().where().eq("set_id", setId).and().eq("deleted", 0).prepare();
        return this.queryForFirst(preparedQuery);
    }

    public ArrayList<SetSession> getSetSessionsByServerWorkoutSessionId(int serverWorkoutSessionId) throws SQLException {
        PreparedQuery<SetSession> preparedQuery = queryBuilder().where().eq("server_workout_session_id", serverWorkoutSessionId).and().eq("deleted", 0).prepare();
        return (ArrayList) this.query(preparedQuery);
    }

    public ArrayList<SetSession> getSetSessionByServerWorkoutSessionIdAndServerClientId(int serverWorkoutSessionId, int serverClientId) throws SQLException {
        PreparedQuery<SetSession> preparedQuery = queryBuilder().orderBy("updated_date", false).where().eq("server_workout_session_id", serverWorkoutSessionId).and().eq("server_client_id", serverClientId).prepare();
        return (ArrayList) this.query(preparedQuery);
    }

    public ArrayList<SetSession> getSetSessionByWorkoutSessionIdAndClientId(int workoutSessionId, int clientId) throws SQLException {
        PreparedQuery<SetSession> preparedQuery = queryBuilder().orderBy("updated_date", false).where().eq("workout_session_id", workoutSessionId).and().eq("client_id", clientId).prepare();
        return (ArrayList) this.query(preparedQuery);
    }

    /**
     * get updated or new set sessions
     *
     * @return trainers list
     * @throws java.sql.SQLException
     */
    public List<SetSession> getUpdatedOrNewSetSessions() throws SQLException {
        PreparedQuery<SetSession> preparedQuery = queryBuilder().where().ne("sync_status", SyncStatus.SYNCED).prepare();
        return this.query(preparedQuery);
    }
}
