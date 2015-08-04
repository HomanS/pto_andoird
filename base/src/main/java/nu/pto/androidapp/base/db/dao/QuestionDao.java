package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import nu.pto.androidapp.base.db.model.Question;


public class QuestionDao extends BaseDaoImpl<Question, Integer> {

    public QuestionDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Question.class);
    }


}
