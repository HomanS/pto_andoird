package nu.pto.androidapp.base.db.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.DietCategoryDao;

@DatabaseTable(tableName = "diet_category", daoClass = DietCategoryDao.class)
public class DietCategory extends Syncable {


    @Expose
    @SerializedName(value = "diet_category_id")
    @DatabaseField(generatedId = true, columnName = "diet_category_id", dataType = DataType.INTEGER_OBJ)
    public Integer dietCategoryId;

    @Expose
    @SerializedName(value = "server_diet_category_id")
    @DatabaseField(columnName = "server_diet_category_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverDietCategoryId;


    @Expose
    @SerializedName(value = "category_name")
    @DatabaseField(columnName = "category_name")
    public String categoryName;


}
