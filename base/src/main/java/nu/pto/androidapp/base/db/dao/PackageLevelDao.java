package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import nu.pto.androidapp.base.db.model.PackageLevel;


public class PackageLevelDao extends BaseDaoImpl<PackageLevel, Integer> {

    public PackageLevelDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, PackageLevel.class);
    }


}
