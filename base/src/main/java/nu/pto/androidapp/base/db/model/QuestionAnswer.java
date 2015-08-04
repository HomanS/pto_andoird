package nu.pto.androidapp.base.db.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuestionAnswer {

    public QuestionAnswer() {

    }

    public QuestionAnswer(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    @Expose(serialize = false)
    @SerializedName(value = "server_question_id")
    public Integer serverQuestionId;

    @Expose
    public String question;

    @Expose
    public String answer;

}
