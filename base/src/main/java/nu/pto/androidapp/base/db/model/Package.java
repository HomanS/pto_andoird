package nu.pto.androidapp.base.db.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.PackageDao;

@DatabaseTable(tableName = "package", daoClass = PackageDao.class)
public class Package extends Syncable {

    @Expose
    @SerializedName(value = "package_id")
    @DatabaseField(generatedId = true, dataType = DataType.INTEGER_OBJ, columnName = "package_id")
    public Integer packageId;

    @Expose
    @SerializedName(value = "server_package_id")
    @DatabaseField(index = true, dataType = DataType.INTEGER_OBJ, columnName = "server_package_id")
    public Integer serverPackageId;


    @Expose
    @SerializedName(value = "package_category_id")
    @DatabaseField(dataType = DataType.INTEGER_OBJ, columnName = "package_category_id")
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
    @SerializedName(value = "package_level_id")
    @DatabaseField(index = true, dataType = DataType.INTEGER_OBJ, columnName = "package_level_id")
    public Integer packageLevelId;


    @Expose
    @DatabaseField
    public String name;

    @Expose
    @DatabaseField
    public String description;


    @Expose
    @DatabaseField(dataType = DataType.INTEGER)
    public int duration;

    @Expose
    @DatabaseField(dataType = DataType.INTEGER)
    public int price;

    @Expose
    @SerializedName(value = "vat_percent")
    @DatabaseField(dataType = DataType.INTEGER, columnName = "vat_percent")
    public int vat_percent;

    @Expose
    @SerializedName(value = "payment_type")
    @DatabaseField(columnName = "payment_type")
    public String paymentType;

    @Expose
    @SerializedName(value = "question1")
    @DatabaseField(columnName = "question1")
    public String question1;

    @Expose
    @SerializedName(value = "question2")
    @DatabaseField(columnName = "question2")
    public String question2;

    @Expose
    @SerializedName(value = "question3")
    @DatabaseField(columnName = "question3")
    public String question3;

    @Expose
    @SerializedName(value = "question4")
    @DatabaseField(columnName = "question4")
    public String question4;


    @Expose
    @DatabaseField(dataType = DataType.INTEGER, canBeNull = false, defaultValue = "0")
    public int active;


}
