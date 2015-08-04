package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import nu.pto.androidapp.base.db.model.DietCategory;


public class DietCategoryDao extends BaseDaoImpl<DietCategory, Integer> {

    public DietCategoryDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, DietCategory.class);
    }


    /**
     * @param dietCategoryId
     * @return
     */
    public DietCategory getDietCategoryByServerDietCategoryId(int dietCategoryId) throws SQLException {
        PreparedQuery<DietCategory> preparedQuery = null;
        preparedQuery = queryBuilder().where().eq("server_diet_category_id", dietCategoryId).prepare();
        DietCategory dietCategory = this.queryForFirst(preparedQuery);
        return dietCategory;
    }
}
