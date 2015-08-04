package nu.pto.androidapp.base.db.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.WorkoutSessionDao;


@DatabaseTable(tableName = "workout_session", daoClass = WorkoutSessionDao.class)
public class WorkoutSession extends Syncable {

    @Expose
    @SerializedName(value = "workout_session_id")
    @DatabaseField(generatedId = true, columnName = "workout_session_id", dataType = DataType.INTEGER_OBJ)
    public Integer workoutSessionId;

    @Expose
    @SerializedName(value = "server_workout_session_id")
    @DatabaseField(columnName = "server_workout_session_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverWorkoutSessionId;


    //***************************************************************************************

    @Expose
    @SerializedName(value = "workout_id")
    @DatabaseField(columnName = "workout_id", dataType = DataType.INTEGER_OBJ, canBeNull = false)
    public Integer workoutId;

    @Expose
    @SerializedName(value = "server_workout_id")
    @DatabaseField(columnName = "server_workout_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverWorkoutId;


    //***************************************************************************************

    @Expose(serialize = true, deserialize = false)
    @SerializedName(value = "client_id")
    @DatabaseField(dataType = DataType.INTEGER_OBJ, columnName = "client_id", canBeNull = false)
    public Integer clientId;

    @Expose
    @SerializedName(value = "server_client_id")
    @DatabaseField(columnName = "server_client_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverClientId;

    //***************************************************************************************

    @Expose
    @SerializedName(value = "session_start_date")
    @DatabaseField(columnName = "session_start_date", canBeNull = false, dataType = DataType.LONG)
    public long sessionStartDate;

    @Expose
    @SerializedName(value = "session_end_date")
    @DatabaseField(columnName = "session_end_date", canBeNull = false, dataType = DataType.LONG)
    public long sessionEndDate;

    @Override
    public String toString() {
        return "WorkoutSession{" +
                "workoutSessionId=" + workoutSessionId +
                ", serverWorkoutSessionId=" + serverWorkoutSessionId +
                ", workoutId=" + workoutId +
                ", serverWorkoutId=" + serverWorkoutId +
                ", clientId=" + clientId +
                ", serverClientId=" + serverClientId +
                ", sessionStartDate=" + sessionStartDate +
                ", sessionEndDate=" + sessionEndDate +
                '}';
    }
}
