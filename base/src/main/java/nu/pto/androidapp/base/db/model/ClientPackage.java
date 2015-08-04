package nu.pto.androidapp.base.db.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.ClientPackageDao;

@DatabaseTable(tableName = "client_package", daoClass = ClientPackageDao.class)
public class ClientPackage extends Syncable {

    @Expose
    @SerializedName(value = "client_package_id")
    @DatabaseField(generatedId = true, columnName = "client_package_id", dataType = DataType.INTEGER_OBJ)
    public Integer clientPackageId;

    @Expose
    @SerializedName(value = "server_client_package_id")
    @DatabaseField(columnName = "server_client_package_id", dataType = DataType.INTEGER_OBJ)
    public Integer serverClientPackageId;


    @Expose
    @SerializedName(value = "package_id")
    @DatabaseField(dataType = DataType.INTEGER_OBJ, columnName = "package_id")
    public Integer packageId;

    @Expose
    @SerializedName(value = "server_package_id")
    @DatabaseField(index = true, dataType = DataType.INTEGER_OBJ, columnName = "server_package_id")
    public Integer serverPackageId;


    @Expose
    @SerializedName(value = "client_id")
    @DatabaseField(dataType = DataType.INTEGER_OBJ, columnName = "client_id")
    public Integer clientId;

    @Expose
    @SerializedName(value = "server_client_id")
    @DatabaseField(index = true, dataType = DataType.INTEGER_OBJ, columnName = "server_client_id")
    public Integer serverClientId;


    @Expose
    @SerializedName(value = "trainer_id")
    @DatabaseField(dataType = DataType.INTEGER_OBJ, columnName = "trainer_id")
    public Integer trainerId = 0;

    @Expose
    @SerializedName(value = "server_trainer_id")
    @DatabaseField(index = true, dataType = DataType.INTEGER_OBJ, columnName = "server_trainer_id")
    public Integer serverTrainerId;


    @Expose
    @DatabaseField(dataType = DataType.LONG, columnName = "package_start_date")
    @SerializedName(value = "package_start_date")
    private long packageStartDate;

    @Expose
    @DatabaseField(dataType = DataType.LONG, columnName = "package_end_date")
    @SerializedName(value = "package_end_date")
    private long packageEndDate;

    @Expose
    @DatabaseField(dataType = DataType.LONG, columnName = "purchase_date")
    @SerializedName(value = "purchase_date")
    private long purchaseDate;


    @Expose
    @SerializedName(value = "_photos")
    @DatabaseField(columnName = "photos")
    public String photos;


    @Expose
    @SerializedName(value = "question1_answer")
    @DatabaseField(columnName = "question1_answer")
    public String question1Answer;

    @Expose
    @SerializedName(value = "question2_answer")
    @DatabaseField(columnName = "question2_answer")
    public String question2Answer;

    @Expose
    @SerializedName(value = "question3_answer")
    @DatabaseField(columnName = "question3_answer")
    public String question3Answer;

    @Expose
    @SerializedName(value = "question4_answer")
    @DatabaseField(columnName = "question4_answer")
    public String question4Answer;

    @Expose
    @SerializedName(value = "answer_1")
    @DatabaseField(columnName = "answer_1")
    public String answer1;

    @Expose
    @SerializedName(value = "answer_2")
    @DatabaseField(columnName = "answer_2")
    public String answer2;

    @Expose
    @SerializedName(value = "answer_3")
    @DatabaseField(columnName = "answer_3")
    public String answer3;

    @Expose
    @SerializedName(value = "answer_4")
    @DatabaseField(columnName = "answer_4")
    public String answer4;

    @Expose
    @SerializedName(value = "answer_5")
    @DatabaseField(columnName = "answer_5")
    public String answer5;

    @Expose
    @SerializedName(value = "answer_6")
    @DatabaseField(columnName = "answer_6")
    public String answer6;

    @Expose
    @SerializedName(value = "answer_7")
    @DatabaseField(columnName = "answer_7")
    public String answer7;

    @Expose
    @SerializedName(value = "answer_8")
    @DatabaseField(columnName = "answer_8")
    public String answer8;

    @Expose
    @SerializedName(value = "answer_9")
    @DatabaseField(columnName = "answer_9")
    public String answer9;

    @Expose
    @SerializedName(value = "answer_10")
    @DatabaseField(columnName = "answer_10")
    public String answer10;

    @Expose
    @SerializedName(value = "answer_11")
    @DatabaseField(columnName = "answer_11")
    public String answer11;

    @Expose
    @SerializedName(value = "answer_12")
    @DatabaseField(columnName = "answer_12")
    public String answer12;

    @Expose
    @SerializedName(value = "answer_13")
    @DatabaseField(columnName = "answer_13")
    public String answer13;

    @Expose
    @SerializedName(value = "answer_14")
    @DatabaseField(columnName = "answer_14")
    public String answer14;

    @Expose
    @SerializedName(value = "answer_15")
    @DatabaseField(columnName = "answer_15")
    public String answer15;

    @Expose
    @SerializedName(value = "answer_16")
    @DatabaseField(columnName = "answer_16")
    public String answer16;

    @Expose
    @SerializedName(value = "answer_17")
    @DatabaseField(columnName = "answer_17")
    public String answer17;

    @Expose
    @SerializedName(value = "answer_18")
    @DatabaseField(columnName = "answer_18")
    public String answer18;


    @Expose
    @DatabaseField(dataType = DataType.INTEGER, canBeNull = false, defaultValue = "0")
    public int pending;


}
