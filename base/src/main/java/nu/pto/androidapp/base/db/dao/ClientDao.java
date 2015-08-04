package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import nu.pto.androidapp.base.db.model.Client;
import nu.pto.androidapp.base.util.SyncStatus;


public class ClientDao extends BaseDaoImpl<Client, Integer> {

    public ClientDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Client.class);

    }


    /**
     * get client by server client id
     *
     * @param serverClientId
     * @return
     */
    public Client getClientByServerClientId(int serverClientId) throws SQLException {
        PreparedQuery<Client> preparedQuery = queryBuilder().where().eq("server_client_id", serverClientId).prepare();
        return this.queryForFirst(preparedQuery);
    }

    /**
     * return client by client id
     *
     * @param clientId
     * @return
     * @throws java.sql.SQLException
     */
    public Client getClientByClientId(int clientId) throws SQLException {
        PreparedQuery<Client> preparedQuery = queryBuilder().where().eq("client_id", clientId).prepare();
        return this.queryForFirst(preparedQuery);
    }

    /**
     * update client password
     *
     * @param serverClientId, newPassword
     * @return
     */
    public boolean updateClientPasswordByServerClientId(int serverClientId, String newPassword) throws SQLException {
        boolean isUpdated = false;
        PreparedQuery<Client> preparedQuery = null;
        preparedQuery = queryBuilder().where().eq("server_client_id", serverClientId).prepare();
        Client client = this.queryForFirst(preparedQuery);
        client.password = newPassword;
        update(client);
        isUpdated = true;
        return isUpdated;
    }

    public List<Client> getUpdatedClients() throws SQLException {
        PreparedQuery<Client> preparedQuery = queryBuilder().where().ne("sync_status", SyncStatus.SYNCED).prepare();
        return query(preparedQuery);
    }

    public int deleteAll() throws SQLException {
        DeleteBuilder<Client, Integer> deleteBuilder = deleteBuilder();
        return deleteBuilder.delete();
    }
}
