package nu.pto.androidapp.base.db.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.PackageLevelDao;

@DatabaseTable(tableName = "package_level", daoClass = PackageLevelDao.class)
public class PackageLevel {


    @Expose
    @DatabaseField(id = true, dataType = DataType.INTEGER_OBJ)
    public Integer id;

    @Expose
    @SerializedName(value = "level_name")
    @DatabaseField(columnName = "level_name")
    public String levelName;


}
