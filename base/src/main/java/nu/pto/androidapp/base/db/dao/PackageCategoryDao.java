package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import nu.pto.androidapp.base.db.model.PackageCategory;


public class PackageCategoryDao extends BaseDaoImpl<PackageCategory, Integer> {

    public PackageCategoryDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, PackageCategory.class);
    }


    public PackageCategory getPackageCategoryByServerPackageCategoryId(int serverPackageCategoryId) throws SQLException {
        PreparedQuery<PackageCategory> preparedQuery = queryBuilder().where().eq("server_package_category_id", serverPackageCategoryId).prepare();
        PackageCategory packageCategory = this.queryForFirst(preparedQuery);
        return packageCategory;
    }
}
