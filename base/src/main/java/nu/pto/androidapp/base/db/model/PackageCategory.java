package nu.pto.androidapp.base.db.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.PackageCategoryDao;

@DatabaseTable(tableName = "package_category", daoClass = PackageCategoryDao.class)
public class PackageCategory extends Syncable {


    @Expose
    @SerializedName(value = "package_category_id")
    @DatabaseField(generatedId = true, dataType = DataType.INTEGER_OBJ, columnName = "package_category_id")
    public Integer packageCategoryId;

    @Expose
    @SerializedName(value = "server_package_category_id")
    @DatabaseField(index = true, dataType = DataType.INTEGER_OBJ, columnName = "server_package_category_id")
    public Integer serverPackageCategoryId;


    @Expose
    @SerializedName(value = "trainer_id")
    @DatabaseField(dataType = DataType.INTEGER_OBJ, columnName = "trainer_id")
    public Integer trainerId = 0;

    @Expose
    @SerializedName(value = "server_trainer_id")
    @DatabaseField(index = true, dataType = DataType.INTEGER_OBJ, columnName = "server_trainer_id")
    public Integer serverTrainerId;


    @Expose
    @SerializedName(value = "category_name")
    @DatabaseField(columnName = "category_name")
    public String categoryName;

    @Expose
    @SerializedName(value = "css_class")
    @DatabaseField(columnName = "css_class")
    public String cssClass;

    @Expose
    @DatabaseField
    public String photo;


}
