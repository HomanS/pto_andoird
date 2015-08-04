package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import nu.pto.androidapp.base.db.model.ClientPackage;
import nu.pto.androidapp.base.util.SyncStatus;


public class ClientPackageDao extends BaseDaoImpl<ClientPackage, Integer> {

    public ClientPackageDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, ClientPackage.class);
    }


    /**
     * @param serverClientPackageId server client package id
     * @return
     * @throws java.sql.SQLException
     */
    public ClientPackage getClientPackageByServerClientPackageId(int serverClientPackageId) throws SQLException {
        PreparedQuery<ClientPackage> packagePreparedQuery = queryBuilder().where().eq("server_client_package_id", serverClientPackageId).prepare();
        return queryForFirst(packagePreparedQuery);
    }


    public List<ClientPackage> getUpdatedClientPackages() throws SQLException {
        PreparedQuery<ClientPackage> preparedQuery = queryBuilder().where().ne("sync_status", SyncStatus.SYNCED).prepare();
        return this.query(preparedQuery);
    }

    /**
     * get client active cliept package
     *
     * @param clientId  client's id
     * @param packageId
     * @return
     * @throws java.sql.SQLException
     */
    public ClientPackage getClientPackageByClientIdAndPackageId(Integer clientId, Integer packageId) throws SQLException {
        PreparedQuery<ClientPackage> clientPackagePreparedQuery = queryBuilder().where().eq("client_id", clientId).and().eq("package_id", packageId).prepare();

        return queryForFirst(clientPackagePreparedQuery);
    }

}
