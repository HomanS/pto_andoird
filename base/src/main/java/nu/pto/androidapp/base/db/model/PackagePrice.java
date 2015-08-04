package nu.pto.androidapp.base.db.model;


import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.PackagePriceDao;

@DatabaseTable(tableName = "package_price", daoClass = PackagePriceDao.class)
public class PackagePrice {


    @Expose
    @DatabaseField(id = true, dataType = DataType.INTEGER_OBJ)
    public Integer id;

    @Expose
    @DatabaseField(dataType = DataType.INTEGER)
    public int price;

}
