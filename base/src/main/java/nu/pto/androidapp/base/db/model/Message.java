package nu.pto.androidapp.base.db.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.MessageDao;

@DatabaseTable(tableName = "message", daoClass = MessageDao.class)
public class Message extends Syncable {

    @Expose
    @SerializedName(value = "message_id")
    @DatabaseField(generatedId = true, columnName = "message_id", dataType = DataType.INTEGER_OBJ)
    public Integer messageId;

    @Expose
    @SerializedName(value = "server_message_id")
    @DatabaseField(columnName = "server_message_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverMessageId;


    @Expose
    @SerializedName(value = "server_from_id")
    @DatabaseField(columnName = "server_from_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverFromId;

    @Expose
    @SerializedName(value = "from_id")
    @DatabaseField(columnName = "from_id", dataType = DataType.INTEGER_OBJ)
    public Integer fromId = 0;


    @Expose
    @SerializedName(value = "server_to_id")
    @DatabaseField(columnName = "server_to_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverToId;

    @Expose
    @SerializedName(value = "to_id")
    @DatabaseField(columnName = "to_id", dataType = DataType.INTEGER_OBJ)
    public Integer toId = 0;


    @Expose
    @SerializedName(value = "from_trainer")
    @DatabaseField(columnName = "from_trainer", defaultValue = "0", dataType = DataType.INTEGER)
    public int fromTrainer;

    @Expose
    @DatabaseField
    public String image;

    @Expose
    @DatabaseField
    public String message;

    @Expose
    @DatabaseField(dataType = DataType.INTEGER)
    public int read;

    @Expose
    @DatabaseField(dataType = DataType.INTEGER)
    public int received;

}
