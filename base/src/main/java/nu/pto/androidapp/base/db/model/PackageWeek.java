package nu.pto.androidapp.base.db.model;


import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.PackageWeekDao;

@DatabaseTable(tableName = "package_week", daoClass = PackageWeekDao.class)
public class PackageWeek {


    @Expose
    @DatabaseField(id = true, dataType = DataType.INTEGER_OBJ)
    public Integer id;

    @Expose
    @DatabaseField(dataType = DataType.INTEGER)
    public int duration;

}
