package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import nu.pto.androidapp.base.db.model.Trainer;
import nu.pto.androidapp.base.util.SyncStatus;


public class TrainerDao extends BaseDaoImpl<Trainer, Integer> {

    public TrainerDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Trainer.class);
    }


    /**
     * get trainer by given server trainer id
     *
     * @param serverTrainerId server trainer id
     * @return trainer object
     * @throws java.sql.SQLException
     */
    public Trainer getTrainerByServerTrainerId(int serverTrainerId) throws SQLException {

        PreparedQuery<Trainer> preparedQuery = queryBuilder().where().eq("server_trainer_id", serverTrainerId).prepare();
        return this.queryForFirst(preparedQuery);

    }

    public Trainer getTrainerByTrainerId(int trainerId) throws SQLException {
        PreparedQuery<Trainer> preparedQuery = queryBuilder().where().eq("trainer_id", trainerId).prepare();
        return this.queryForFirst(preparedQuery);

    }


    /**
     * get updated trainers
     *
     * @return trainers list
     * @throws java.sql.SQLException
     */
    public List<Trainer> getUpdatedTrainers() throws SQLException {
        PreparedQuery<Trainer> preparedQuery = queryBuilder().where().ne("sync_status", SyncStatus.SYNCED).prepare();
        return this.query(preparedQuery);
    }

    public int deleteAll() throws SQLException {
        DeleteBuilder<Trainer, Integer> deleteBuilder = deleteBuilder();
        return deleteBuilder.delete();
    }
}
