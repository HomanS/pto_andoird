package nu.pto.androidapp.base.db.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.MuscleGroupDao;

@DatabaseTable(tableName = "muscle_groups", daoClass = MuscleGroupDao.class)
public class MuscleGroup {


    @Expose
    @DatabaseField(id = true, dataType = DataType.INTEGER_OBJ)
    public Integer id;

    @Expose
    @SerializedName(value = "muscle_group_name")
    @DatabaseField(columnName = "muscle_group_name")
    public String muscleGroupName;

    @Expose
    @DatabaseField
    public String image;

}
