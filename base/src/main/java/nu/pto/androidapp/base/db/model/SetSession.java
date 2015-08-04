package nu.pto.androidapp.base.db.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.SetSessionDao;


@DatabaseTable(tableName = "set_session", daoClass = SetSessionDao.class)
public class SetSession extends Syncable {

    @Expose
    @SerializedName(value = "set_session_id")
    @DatabaseField(generatedId = true, columnName = "set_session_id", dataType = DataType.INTEGER_OBJ)
    public Integer setSessionId;

    @Expose
    @SerializedName(value = "server_set_session_id")
    @DatabaseField(columnName = "server_set_session_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverSetSessionId;


    //***************************************************************************************


    @Expose
    @SerializedName(value = "set_id")
    @DatabaseField(columnName = "set_id", dataType = DataType.INTEGER_OBJ)
    public Integer setId;

    @Expose
    @SerializedName(value = "server_set_id")
    @DatabaseField(columnName = "server_set_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverSetId;


    //***************************************************************************************


    @Expose
    @SerializedName(value = "workout_session_id")
    @DatabaseField(columnName = "workout_session_id", dataType = DataType.INTEGER_OBJ)
    public Integer workoutSessionId;

    @Expose
    @SerializedName(value = "server_workout_session_id")
    @DatabaseField(columnName = "server_workout_session_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverWorkoutSessionId;


    //***************************************************************************************

    @Expose
    @SerializedName(value = "client_id")
    @DatabaseField(dataType = DataType.INTEGER_OBJ, columnName = "client_id")
    public Integer clientId;

    @Expose
    @SerializedName(value = "server_client_id")
    @DatabaseField(columnName = "server_client_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverClientId;


    //***************************************************************************************

    @Expose
    @SerializedName(value = "distance_unit")
    @DatabaseField(dataType = DataType.STRING, columnName = "distance_unit")
    public String distanceUnit;

    @Expose
    @SerializedName(value = "weight_unit")
    @DatabaseField(dataType = DataType.STRING, columnName = "weight_unit")
    public String weightUnit;


    @Expose
    @SerializedName(value = "time_unit")
    @DatabaseField(dataType = DataType.STRING, columnName = "time_unit")
    public String timeUnit;


    @Expose
    @SerializedName(value = "rest_unit")
    @DatabaseField(dataType = DataType.STRING, columnName = "rest_unit")
    public String restUnit;

    @Expose
    @DatabaseField(dataType = DataType.INTEGER_OBJ)
    public Integer rest;

    @Expose
    @DatabaseField(dataType = DataType.INTEGER_OBJ)
    public Integer time;

    @Expose
    @DatabaseField(dataType = DataType.DOUBLE_OBJ)
    public Double weight;

    @Expose
    @DatabaseField(dataType = DataType.DOUBLE_OBJ)
    public Double distance;

    @Expose
    @DatabaseField(dataType = DataType.INTEGER_OBJ)
    public Integer repetitions;

    @Override
    public String toString() {
        return "SetSession{" +
                "setSessionId=" + setSessionId +
                ", serverSetSessionId=" + serverSetSessionId +
                ", setId=" + setId +
                ", serverSetId=" + serverSetId +
                ", workoutSessionId=" + workoutSessionId +
                ", serverWorkoutSessionId=" + serverWorkoutSessionId +
                ", clientId=" + clientId +
                ", serverClientId=" + serverClientId +
                ", distanceUnit='" + distanceUnit + '\'' +
                ", weightUnit='" + weightUnit + '\'' +
                ", timeUnit='" + timeUnit + '\'' +
                ", restUnit='" + restUnit + '\'' +
                ", rest=" + rest +
                ", time=" + time +
                ", weight=" + weight +
                ", distance=" + distance +
                ", repetitions=" + repetitions +
                ", createdDate=" + super.createdDate +
                ", updatedDate=" + super.updatedDate +
                '}';
    }
}
