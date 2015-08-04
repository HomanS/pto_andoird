package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;
import nu.pto.androidapp.base.db.model.Image;

import java.sql.SQLException;
import java.util.List;


public class ImageDao extends BaseDaoImpl<Image, String> {

    public ImageDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Image.class);
    }

    /**
     * get Image by imageId
     *
     * @param imageId
     * @return
     */
    public Image getImageById(String imageId) {
        PreparedQuery<Image> preparedQuery = null;
        try {
            preparedQuery = queryBuilder().where().eq("_id", imageId).prepare();
            return queryForFirst(preparedQuery);
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public List<Image> getImagesWithEmptyContent(){
        PreparedQuery<Image> preparedQuery = null;
        try {
            preparedQuery = queryBuilder().where().isNull("photo_base64").prepare();
            return query(preparedQuery);
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

}
