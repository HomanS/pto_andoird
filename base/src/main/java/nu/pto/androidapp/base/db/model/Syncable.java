package nu.pto.androidapp.base.db.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public abstract class Syncable {

    @Expose
    @DatabaseField(unique = true, persisted = true, width = 24, canBeNull = false, index = true)
    public String _id;

    @Expose
    @DatabaseField(dataType = DataType.INTEGER, canBeNull = false, defaultValue = "0")
    public int deleted;

    @Expose
    @SerializedName(value = "updated_date")
    @DatabaseField(columnName = "updated_date", canBeNull = false, dataType = DataType.LONG)
    public long updatedDate;

    @Expose
    @SerializedName(value = "created_date")
    @DatabaseField(columnName = "created_date", canBeNull = false, dataType = DataType.LONG)
    public long createdDate;

    @Expose(serialize = true, deserialize = false)
    @DatabaseField(columnName = "sync_status", canBeNull = false, dataType = DataType.INTEGER, defaultValue = "0")
    public int syncStatus;
}
