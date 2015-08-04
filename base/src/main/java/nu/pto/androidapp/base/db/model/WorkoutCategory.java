package nu.pto.androidapp.base.db.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.WorkoutCategoryDao;

@DatabaseTable(tableName = "workout_category", daoClass = WorkoutCategoryDao.class)
public class WorkoutCategory extends Syncable {


    @Expose
    @SerializedName(value = "workout_category_id")
    @DatabaseField(generatedId = true, columnName = "workout_category_id", dataType = DataType.INTEGER_OBJ)
    public Integer workoutCategoryId;

    @Expose
    @SerializedName(value = "server_workout_category_id")
    @DatabaseField(columnName = "server_workout_category_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverWorkoutCategoryId;

    @Expose(deserialize = false, serialize = false)
    @DatabaseField(dataType = DataType.INTEGER_OBJ, columnName = "trainer_id")
    public Integer trainerId = 0;

    @Expose
    @SerializedName(value = "server_trainer_id")
    @DatabaseField(columnName = "server_trainer_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverTrainerId;

    @Expose
    @SerializedName(value = "category_name")
    @DatabaseField(columnName = "category_name")
    public String categoryName;


}
