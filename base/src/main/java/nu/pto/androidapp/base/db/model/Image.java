package nu.pto.androidapp.base.db.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.ImageDao;

@DatabaseTable(tableName = "image", daoClass = ImageDao.class)
public class Image {

    @Expose
    @DatabaseField(id = true)
    public String _id;

    @Expose
    @SerializedName(value = "photo_base64")
    @DatabaseField(columnName = "photo_base64")
    public String photoBase64;

    @Expose
    @SerializedName(value = "url")
//    @DatabaseField(columnName = "url")
    public String url;


    public Image() {

    }

    public Image(String photo_base64, String _id) {
        this.photoBase64 = photo_base64;
        this._id = _id;
    }

    public String getPhotoBase64() {
        return photoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }

    public String get_id() {

        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
