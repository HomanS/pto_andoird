package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import nu.pto.androidapp.base.db.model.Package;


public class PackageDao extends BaseDaoImpl<Package, Integer> {

    public PackageDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Package.class);
    }


    /**
     * @param serverPackageId
     * @return
     * @throws java.sql.SQLException
     */
    public Package getPackageByServerPackageId(int serverPackageId) throws SQLException {
        PreparedQuery<Package> packagePreparedQuery = queryBuilder().where().eq("server_package_id", serverPackageId).prepare();
        Package aPackage = queryForFirst(packagePreparedQuery);
        return aPackage;
    }


}
