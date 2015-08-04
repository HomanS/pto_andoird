package nu.pto.androidapp.base.db.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.DietDao;

@DatabaseTable(tableName = "diet", daoClass = DietDao.class)
public class Diet extends Syncable {

    //***************************************************************************************

    @Expose
    @SerializedName(value = "diet_id")
    @DatabaseField(generatedId = true, columnName = "diet_id", dataType = DataType.INTEGER_OBJ)
    public Integer dietId;

    @Expose
    @SerializedName(value = "server_diet_id")
    @DatabaseField(columnName = "server_diet_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverDietId;


    //***************************************************************************************

    @Expose
    @SerializedName(value = "diet_category_id")
    @DatabaseField(columnName = "diet_category_id", dataType = DataType.INTEGER_OBJ, canBeNull = false)
    public Integer dietCategoryId;

    @Expose
    @SerializedName(value = "server_diet_category_id")
    @DatabaseField(columnName = "server_diet_category_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverDietCategoryId;

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
    @DatabaseField(dataType = DataType.INTEGER_OBJ, columnName = "client_id")
    public Integer clientId;

    @Expose
    @SerializedName(value = "server_client_id")
    @DatabaseField(columnName = "server_client_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverClientId;

    //***************************************************************************************

    @Expose
    @DatabaseField(dataType = DataType.STRING)
    public String title;

    @Expose
    @DatabaseField(dataType = DataType.STRING)
    public String description;

    @Expose
    @DatabaseField(dataType = DataType.STRING)
    public String photo;

    //***************************************************************************************


}
