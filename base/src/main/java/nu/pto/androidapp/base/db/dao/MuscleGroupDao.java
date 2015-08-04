package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import nu.pto.androidapp.base.db.model.MuscleGroup;


public class MuscleGroupDao extends BaseDaoImpl<MuscleGroup, Integer> {

    public MuscleGroupDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, MuscleGroup.class);
    }


}
