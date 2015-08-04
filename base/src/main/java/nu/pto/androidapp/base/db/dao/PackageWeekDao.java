package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import nu.pto.androidapp.base.db.model.PackageWeek;


public class PackageWeekDao extends BaseDaoImpl<PackageWeek, Integer> {

    public PackageWeekDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, PackageWeek.class);
    }


}
