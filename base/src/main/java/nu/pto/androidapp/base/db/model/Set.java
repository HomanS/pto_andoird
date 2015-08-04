package nu.pto.androidapp.base.db.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.SetDao;


@DatabaseTable(tableName = "set", daoClass = SetDao.class)
public class Set extends Syncable {

    @Expose
    @SerializedName(value = "set_id")
    @DatabaseField(generatedId = true, columnName = "set_id", dataType = DataType.INTEGER_OBJ)
    public Integer setId;

    @Expose
    @SerializedName(value = "server_set_id")
    @DatabaseField(columnName = "server_set_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverSetId;


    //***************************************************************************************

    @Expose
    @SerializedName(value = "exercise_id")
    @DatabaseField(columnName = "exercise_id", dataType = DataType.INTEGER_OBJ)
    public Integer exerciseId;

    @Expose
    @SerializedName(value = "server_exercise_id")
    @DatabaseField(columnName = "server_exercise_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverExerciseId;

    //***************************************************************************************

    @Expose
    @SerializedName(value = "workout_id")
    @DatabaseField(columnName = "workout_id", dataType = DataType.INTEGER_OBJ)
    public Integer workoutId;

    @Expose
    @SerializedName(value = "server_workout_id")
    @DatabaseField(columnName = "server_workout_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverWorkoutId;


    //***************************************************************************************

    @Expose
    @SerializedName(value = "trainer_id")
    @DatabaseField(dataType = DataType.INTEGER_OBJ, columnName = "trainer_id")
    public Integer trainerId = 0;

    @Expose
    @SerializedName(value = "server_trainer_id")
    @DatabaseField(columnName = "server_trainer_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverTrainerId;


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
        return "Set{" +
                "setId=" + setId +
                ", serverSetId=" + serverSetId +
                ", exerciseId=" + exerciseId +
                ", serverExerciseId=" + serverExerciseId +
                ", workoutId=" + workoutId +
                ", serverWorkoutId=" + serverWorkoutId +
                ", trainerId=" + trainerId +
                ", serverTrainerId=" + serverTrainerId +
                ", distanceUnit='" + distanceUnit + '\'' +
                ", weightUnit='" + weightUnit + '\'' +
                ", timeUnit='" + timeUnit + '\'' +
                ", restUnit='" + restUnit + '\'' +
                ", rest=" + rest +
                ", time=" + time +
                ", weight=" + weight +
                ", distance=" + distance +
                ", repetitions=" + repetitions +
                '}';
    }
}
