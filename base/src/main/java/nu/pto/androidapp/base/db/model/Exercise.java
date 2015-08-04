package nu.pto.androidapp.base.db.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.ExerciseDao;


@DatabaseTable(tableName = "exercise", daoClass = ExerciseDao.class)
public class Exercise extends Syncable {


    @Expose
    @SerializedName(value = "exercise_id")
    @DatabaseField(generatedId = true, columnName = "exercise_id", dataType = DataType.INTEGER_OBJ)
    public Integer exerciseId;

    @Expose
    @SerializedName(value = "server_exercise_id")
    @DatabaseField(columnName = "server_exercise_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverExerciseId;

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

    @Expose
    @DatabaseField(dataType = DataType.INTEGER_OBJ, columnName = "trainer_id", canBeNull = false)
    public Integer trainerId = 0;

    @Expose
    @SerializedName(value = "server_trainer_id")
    @DatabaseField(columnName = "server_trainer_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverTrainerId;


    //***************************************************************************************
    @Expose
    @SerializedName(value = "template_exercise_id")
    @DatabaseField(columnName = "template_exercise_id", dataType = DataType.INTEGER_OBJ)
    public Integer templateExerciseId;


    @Expose
    @SerializedName(value = "predefined_exercise_id")
    @DatabaseField(columnName = "predefined_exercise_id", dataType = DataType.INTEGER_OBJ)
    public Integer predefinedExerciseId;


    @Expose
    @SerializedName(value = "muscle_group_id")
    @DatabaseField(columnName = "muscle_group_id", dataType = DataType.INTEGER_OBJ)
    public Integer muscleGroupId;


    @Expose
    @SerializedName(value = "exercise_type_id")
    @DatabaseField(columnName = "exercise_type_id", dataType = DataType.INTEGER_OBJ)
    public Integer exerciseTypeId;


//***************************************************************************************

    @Expose
    @SerializedName(value = "exercise_name")
    @DatabaseField(dataType = DataType.STRING, columnName = "exercise_name")
    public String exerciseName;

    @Expose
    @SerializedName(value = "video_url")
    @DatabaseField(dataType = DataType.STRING, columnName = "video_url")
    public String videoUrl;

    @Expose
    @SerializedName(value = "video_description")
    @DatabaseField(dataType = DataType.STRING, columnName = "video_description")
    public String videoDescription;


    @Expose
    @SerializedName(value = "images_description")
    @DatabaseField(dataType = DataType.STRING, columnName = "images_description")
    public String imagesDescription;

    @Expose
    @SerializedName(value = "images_ids")
    @DatabaseField(dataType = DataType.STRING, columnName = "images_ids")
    public String imagesIds;

    @Expose
    @DatabaseField
    public String images;


    @Expose
    @DatabaseField(dataType = DataType.INTEGER)
    public int order;

    @Override
    public String toString() {
        return "Exercise{" +
                "exerciseId=" + exerciseId +
                ", serverExerciseId=" + serverExerciseId +
                ", workoutId=" + workoutId +
                ", serverWorkoutId=" + serverWorkoutId +
                ", trainerId=" + trainerId +
                ", serverTrainerId=" + serverTrainerId +
                ", templateExerciseId=" + templateExerciseId +
                ", predefinedExerciseId=" + predefinedExerciseId +
                ", muscleGroupId=" + muscleGroupId +
                ", exerciseTypeId=" + exerciseTypeId +
                ", exerciseName='" + exerciseName + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", videoDescription='" + videoDescription + '\'' +
                ", imagesDescription='" + imagesDescription + '\'' +
                ", imagesIds='" + imagesIds + '\'' +
                ", images='" + images + '\'' +
                ", order=" + order +
                '}';
    }
}
