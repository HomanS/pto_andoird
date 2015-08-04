package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import nu.pto.androidapp.base.db.model.PackagePrice;


public class PackagePriceDao extends BaseDaoImpl<PackagePrice, Integer> {

    public PackagePriceDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, PackagePrice.class);
    }


}
