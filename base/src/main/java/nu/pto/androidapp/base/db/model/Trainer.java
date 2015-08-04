package nu.pto.androidapp.base.db.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.TrainerDao;

@DatabaseTable(tableName = "trainer", daoClass = TrainerDao.class)
public class Trainer extends Syncable {

    @Expose
    @SerializedName(value = "trainer_id")
    @DatabaseField(generatedId = true, dataType = DataType.INTEGER_OBJ, columnName = "trainer_id")
    public Integer trainerId;

    @Expose
    @SerializedName(value = "server_trainer_id")
    @DatabaseField(index = true, dataType = DataType.INTEGER_OBJ, columnName = "server_trainer_id")
    public Integer serverTrainerId;


    @Expose
    @SerializedName(value = "first_name")
    @DatabaseField(columnName = "first_name", canBeNull = false)
    public String firstName;

    @Expose
    @SerializedName(value = "last_name")
    @DatabaseField(columnName = "last_name", canBeNull = false)
    public String lastName;

    @Expose
    @DatabaseField(canBeNull = false)
    public String email;

    @Expose
    @DatabaseField(width = 24)
    public String photo;

    @Expose(serialize = true, deserialize = false)
    @SerializedName(value = "photo_base64")
    @DatabaseField(columnName = "photo_base64", defaultValue = "")
    public String photoBase64;

    @Expose
    @DatabaseField(dataType = DataType.INTEGER)
    public int gender;

    @Expose
    @DatabaseField
    public String password;

    @Expose
    @SerializedName(value = "password_changed")
    @DatabaseField(columnName = "password_changed", dataType = DataType.INTEGER, defaultValue = "0", canBeNull = false)
    public int passwordChanged;

    @Expose
    @SerializedName(value = "year_of_birth")
    @DatabaseField(dataType = DataType.INTEGER, columnName = "year_of_birth")
    public int yearOfBirth;

    @Expose
    @DatabaseField(persisted = false)
    public String address;

    @Expose
    @SerializedName(value = "phone_number")
    @DatabaseField(columnName = "phone_number", persisted = false)
    public String phoneNumber;

    @Expose
    @SerializedName(value = "is_eleiko")
    @DatabaseField(columnName = "is_eleiko", dataType = DataType.INTEGER, canBeNull = false, defaultValue = "0")
    public int isEleiko;

    @Expose
    @DatabaseField(dataType = DataType.INTEGER, canBeNull = false, defaultValue = "0")
    public int active;


	/*
    @DatabaseField(persisted = false, columnName = "card_number")
	String cardNumber;

	@DatabaseField
	String bank;

	@DatabaseField
	String fee;

	@DatabaseField(columnName = "keep_percent")
	String keepPercent;

	@DatabaseField(columnName = "url_name")
	String urlName;

	@DatabaseField(columnName = "cover_photo")
	String coverPhoto;

	@DatabaseField
	String company;

	@DatabaseField(columnName = "postal_code")
	String postalCode;

	@DatabaseField
	String city;

	@DatabaseField(persisted = false)
	String specialization;

	@DatabaseField(persisted = false)
	String knownFrom;

	@DatabaseField(persisted = false)
	String background;

	@DatabaseField(persisted = false)
	String education;

	@DatabaseField(persisted = false, columnName = "training_tips")
	String trainingTips;

	@DatabaseField(persisted = false, columnName = "facebook_link")
	String facebookLink;

	@DatabaseField(persisted = false, columnName = "twitter_link")
	String twitterLink;

	@DatabaseField(persisted = false, columnName = "instagram_link")
	String instagramLink;

	@DatabaseField(persisted = false, columnName = "blogger_link")
	String bloggerLink;

	@DatabaseField(persisted = false, columnName = "website_link")
	String websiteLink;

	@DatabaseField(persisted = false, columnName = "text_color")
	String textColor;

	@DatabaseField(persisted = false, columnName = "headline_color")
	String headlineColor;

	@DatabaseField(persisted = false, columnName = "font_family")
	String fontFamily;

	@DatabaseField(persisted = false, columnName = "font_size", dataType = DataType.INTEGER)
	int fontSize;

	@DatabaseField(persisted = false, columnName = "presentation_title")
	String presentationTitle;

	@DatabaseField(persisted = false, columnName = "presentation_text")
	String presentationText;

	@DatabaseField(persisted = false, columnName = "thankyou_page_text")
	String thankyouPageText;

	@DatabaseField(persisted = false, columnName = "email_text")
	String emailText;

	@DatabaseField(persisted = false, columnName = "sponsor_title")
	String sponsorTitle;

	@DatabaseField(persisted = false, columnName = "sponsor_text")
	String sponsorText;

	@DatabaseField(persisted = false, columnName = "sponsor_images")
	String sponsorImages;

	@DatabaseField(persisted = false, columnName = "widget_width", dataType = DataType.INTEGER)
	int widgetWidth;

	@DatabaseField(persisted = false, columnName = "widget_height", dataType = DataType.INTEGER)
	int widgetHeight;

	@DatabaseField(persisted = false, columnName = "last_login_date")
	String lastLoginDate;
*/

}
