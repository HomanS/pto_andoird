package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nu.pto.androidapp.base.db.model.Diet;


public class DietDao extends BaseDaoImpl<Diet, Integer> {

    public DietDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Diet.class);
    }


    /**
     * get diet by server client id
     *
     * @param serverDietId diet's server id
     * @return diets' list
     */
    public Diet getDietByServerDietId(int serverDietId) throws SQLException {
        PreparedQuery<Diet> preparedQuery = queryBuilder().where().eq("server_diet_id", serverDietId).prepare();
        return this.queryForFirst(preparedQuery);
    }

    /**
     * get client's Diet
     * diet must not be deleted
     *
     * @param dietId diet's id
     * @return diet
     * @throws java.sql.SQLException
     */
    public Diet getDietByDietId(int dietId) throws SQLException {
        PreparedQuery<Diet> preparedQuery = queryBuilder().where().eq("diet_id", dietId).and().eq("deleted", 0).prepare();
        return this.queryForFirst(preparedQuery);
    }


    /**
     * @param n        count of diets must be fetched
     * @param clientId client's id
     * @return client model
     * @throws java.sql.SQLException
     */
    public ArrayList<Diet> getClientLastNDiets(int n, int clientId) throws SQLException {
        PreparedQuery<Diet> preparedQuery = queryBuilder()
                .orderBy("created_date", false)
                .limit((long)n)
                .where().eq("client_id", clientId)
                .and().eq("deleted", 0).prepare();

        return (ArrayList) this.query(preparedQuery);
    }

    /**
     * get clients list
     *
     * @param clientId
     * @return
     * @throws java.sql.SQLException
     */
    public List<Diet> getClientDiets(Integer clientId) throws SQLException {
        PreparedQuery<Diet> dietPreparedQuery = queryBuilder().orderBy("title", true).where().eq("client_id", clientId).and().eq("deleted", 0).prepare();
        return query(dietPreparedQuery);
    }

}
