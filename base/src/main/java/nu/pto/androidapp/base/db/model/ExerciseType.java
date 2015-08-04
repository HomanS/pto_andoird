package nu.pto.androidapp.base.db.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.ExerciseTypeDao;

@DatabaseTable(tableName = "exercise_type", daoClass = ExerciseTypeDao.class)
public class ExerciseType {


    @Expose
    @DatabaseField(id = true, dataType = DataType.INTEGER_OBJ)
    public Integer id;

    @Expose
    @SerializedName(value = "exercise_type_name")
    @DatabaseField(columnName = "exercise_type_name")
    public String exerciseTypeName;


}
