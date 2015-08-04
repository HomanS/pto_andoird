package nu.pto.androidapp.base.db.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.WorkoutDao;


@DatabaseTable(tableName = "workout", daoClass = WorkoutDao.class)
public class Workout extends Syncable {

    //***************************************************************************************

    @Expose
    @SerializedName(value = "workout_id")
    @DatabaseField(generatedId = true, columnName = "workout_id", dataType = DataType.INTEGER_OBJ)
    public Integer workoutId;

    @Expose
    @SerializedName(value = "server_workout_id")
    @DatabaseField(columnName = "server_workout_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverWorkoutId;


    //***************************************************************************************

    @Expose
    @SerializedName(value = "workout_category_id")
    @DatabaseField(columnName = "workout_category_id", dataType = DataType.INTEGER_OBJ)
    public Integer workoutCategoryId;

    @Expose
    @SerializedName(value = "server_workout_category_id")
    @DatabaseField(columnName = "server_workout_category_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverWorkoutCategoryId;

    //***************************************************************************************

    @Expose
    @SerializedName(value = "workout_name")
    @DatabaseField(dataType = DataType.STRING, columnName = "workout_name")
    public String workoutName;

    @Expose
    @SerializedName(value = "workout_description")
    @DatabaseField(dataType = DataType.STRING, columnName = "workout_description")
    public String workoutDescription;

    @Expose
    @DatabaseField(dataType = DataType.INTEGER)
    public int order;

    //***************************************************************************************

    @Expose
    @DatabaseField(dataType = DataType.INTEGER_OBJ, columnName = "trainer_id")
    public Integer trainerId = 0;

    @Expose
    @SerializedName(value = "server_trainer_id")
    @DatabaseField(columnName = "server_trainer_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverTrainerId;

    //***************************************************************************************

    @Expose
    @DatabaseField(dataType = DataType.INTEGER_OBJ, columnName = "client_id")
    public Integer clientId;

    @Expose
    @SerializedName(value = "server_client_id")
    @DatabaseField(columnName = "server_client_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverClientId;

}
