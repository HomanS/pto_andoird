package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import nu.pto.androidapp.base.db.model.ExerciseType;


public class ExerciseTypeDao extends BaseDaoImpl<ExerciseType, Integer> {

    public ExerciseTypeDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, ExerciseType.class);
    }


}
