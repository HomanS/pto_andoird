package nu.pto.androidapp.base.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nu.pto.androidapp.base.db.dao.QuestionDao;

@DatabaseTable(tableName = "question", daoClass = QuestionDao.class)
public class Question {

    @DatabaseField(id = true)
    public Integer id;

    @DatabaseField
    public String question;

}
