package nu.pto.androidapp.base.db.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import nu.pto.androidapp.base.db.dao.ClientDao;

import java.util.ArrayList;
import java.util.List;

@DatabaseTable(tableName = "client", daoClass = ClientDao.class)
public class Client extends Syncable {


    @Expose
    @SerializedName(value = "client_id")
    @DatabaseField(generatedId = true, dataType = DataType.INTEGER_OBJ, columnName = "client_id")
    public Integer clientId;

    @Expose
    @SerializedName(value = "server_client_id")
    @DatabaseField(dataType = DataType.INTEGER_OBJ, canBeNull = false, columnName = "server_client_id", index = true)
    public Integer serverClientId;


    @Expose
    @DatabaseField(dataType = DataType.INTEGER_OBJ, canBeNull = false, columnName = "trainer_id", index = true)
    @SerializedName(value = "trainer_id")
    public Integer trainerId = 0;

    @Expose
    @DatabaseField(dataType = DataType.INTEGER_OBJ, canBeNull = false, columnName = "server_trainer_id", index = true)
    @SerializedName(value = "server_trainer_id")
    public Integer serverTrainerId;

    @Expose
    @DatabaseField(columnName = "first_name")
    @SerializedName(value = "first_name")
    public String firstName;

    @Expose
    @DatabaseField(columnName = "last_name")
    @SerializedName(value = "last_name")
    public String lastName;

    @Expose
    @DatabaseField
    public String email;

    @Expose
    @DatabaseField
    public String address;

    @Expose
    @DatabaseField(columnName = "co_address")
    @SerializedName(value = "co_address")
    public String coAddress;

    @Expose
    @DatabaseField(columnName = "postal_code")
    @SerializedName(value = "postal_code")
    public String postalCode;

    @Expose
    @DatabaseField
    public String city;

    @Expose
    @DatabaseField
    public String photo;

    @Expose(serialize = true, deserialize = false)
    @SerializedName(value = "photo_base64")
    @DatabaseField(columnName = "photo_base64", defaultValue = "")
    public String photoBase64;

    @Expose
    @DatabaseField
    public String password;


    @Expose
    @SerializedName(value = "password_changed")
    @DatabaseField(columnName = "password_changed", dataType = DataType.INTEGER)
    public int passwordChanged;

    @Expose
    @DatabaseField(dataType = DataType.INTEGER)
    public int gender;

    @Expose
    @SerializedName(value = "year_of_birth")
    @DatabaseField(dataType = DataType.INTEGER, columnName = "year_of_birth")
    public int yearOfBirth;

    @Expose
    @SerializedName(value = "phone_number")
    @DatabaseField(columnName = "phone_number")
    public String phoneNumber;

    @Expose
    @DatabaseField
    public String note;

    //***********package part ********************

    @Expose
    @DatabaseField(columnName = "package_id", index = true, dataType = DataType.INTEGER_OBJ)
    @SerializedName(value = "package_id")
    public Integer packageId;

    @Expose
    @DatabaseField(dataType = DataType.INTEGER_OBJ, columnName = "server_package_id")
    @SerializedName(value = "server_package_id")
    public Integer serverPackageId;

    @Expose
    @DatabaseField(dataType = DataType.LONG, columnName = "package_start_date")
    @SerializedName(value = "package_start_date")
    public long packageStartDate;

    @Expose
    @DatabaseField(dataType = DataType.LONG_OBJ, columnName = "package_end_date")
    @SerializedName(value = "package_end_date")
    public Long packageEndDate;

    @Expose
    @DatabaseField(dataType = DataType.LONG, columnName = "purchase_date")
    @SerializedName(value = "purchase_date")
    public long purchaseDate;

    //***********questions part ********************

    @Expose
    @DatabaseField(columnName = "question1_answer")
    @SerializedName(value = "question1_answer")
    public String question1Answer;

    @Expose
    @DatabaseField(columnName = "question2_answer")
    @SerializedName(value = "question2_answer")
    public String question2Answer;

    @Expose
    @DatabaseField(columnName = "question3_answer")
    @SerializedName(value = "question3_answer")
    public String question3Answer;

    @Expose
    @DatabaseField(columnName = "question4_answer")
    @SerializedName(value = "question4_answer")
    public String question4Answer;

    @Expose
    @SerializedName(value = "question_answer")
    public List<QuestionAnswer> questionsAnswers = new ArrayList<QuestionAnswer>();

    @Expose(serialize = false, deserialize = false)
    @DatabaseField(dataType = DataType.INTEGER, columnName = "is_eleiko", canBeNull = false, defaultValue = "0", persisted = false)
    @SerializedName(value = "is_eleiko")
    public int isEleiko;

    @Expose
    @DatabaseField(dataType = DataType.INTEGER, columnName = "is_subscribed", defaultValue = "0")
    @SerializedName(value = "is_subscribed")
    public int isSubscribed;


    @Override
    public String toString() {
        return "Client{" +
                "clientId=" + clientId +
                ", serverClientId=" + serverClientId +
                ", trainerId=" + trainerId +
                ", serverTrainerId=" + serverTrainerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", coAddress='" + coAddress + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", city='" + city + '\'' +
                ", photo='" + photo + '\'' +
                ", photoBase64='" + photoBase64 + '\'' +
                ", password='" + password + '\'' +
                ", passwordChanged=" + passwordChanged +
                ", gender=" + gender +
                ", yearOfBirth=" + yearOfBirth +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", note='" + note + '\'' +
                ", packageId=" + packageId +
                ", serverPackageId=" + serverPackageId +
                ", packageStartDate=" + packageStartDate +
                ", packageEndDate=" + packageEndDate +
                ", purchaseDate=" + purchaseDate +
                ", question1Answer='" + question1Answer + '\'' +
                ", question2Answer='" + question2Answer + '\'' +
                ", question3Answer='" + question3Answer + '\'' +
                ", question4Answer='" + question4Answer + '\'' +
                ", questionsAnswers=" + questionsAnswers +
                ", isEleiko=" + isEleiko +
                ", isSubscribed=" + isSubscribed +
                '}';
    }


}
