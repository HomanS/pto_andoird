package nu.pto.androidapp.base.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nu.pto.androidapp.base.db.model.Message;
import nu.pto.androidapp.base.util.SyncStatus;


public class MessageDao extends BaseDaoImpl<Message, Integer> {

    public MessageDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Message.class);
    }


    /**
     * @param serverMessageId
     * @return
     */
    public Message getMessageByServerMessageId(int serverMessageId) throws SQLException {
        PreparedQuery<Message> preparedQuery = queryBuilder().where().eq("server_message_id", serverMessageId).prepare();
        return this.queryForFirst(preparedQuery);
    }

    public Message getMessageByMessageId(int messageId) throws SQLException {
        PreparedQuery<Message> preparedQuery = queryBuilder().where().eq("message_id", messageId).prepare();
        return this.queryForFirst(preparedQuery);
    }


    /**
     * get clients last message
     *
     * @param clientId client's id
     * @return last message
     * @throws java.sql.SQLException
     */
    public Message getClientLastMessageByClientId(int clientId) throws SQLException {
        PreparedQuery<Message> preparedQueryClientLastMessage = queryBuilder()
                .orderBy("created_date", false)
                .where().eq("from_id", clientId).and().eq("from_trainer", 0)
                .prepare();
        Message clientLastMessage = this.queryForFirst(preparedQueryClientLastMessage);
        PreparedQuery<Message> preparedQueryTrainerLastMessage = queryBuilder()
                .orderBy("created_date", false)
                .where().eq("to_id", clientId).and().eq("from_trainer", 1)
                .prepare();
        Message trainerLastMessage = this.queryForFirst(preparedQueryTrainerLastMessage);

        if (clientLastMessage != null) {
            if (trainerLastMessage != null) {
                if (clientLastMessage.updatedDate > trainerLastMessage.updatedDate) {
                    return clientLastMessage;
                } else {
                    return trainerLastMessage;
                }
            } else {
                return clientLastMessage;
            }
        } else {
            return trainerLastMessage;
        }
    }

    public ArrayList<Message> getNMessagesByClientId(long n, int clientId) throws SQLException {
        PreparedQuery<Message> preparedQuery;
        if (n == -1) {
            preparedQuery = queryBuilder().orderBy("created_date", false).where().eq("from_id", clientId).or().eq("to_id", clientId).prepare();
        } else {
            preparedQuery = queryBuilder().limit(n).orderBy("created_date", false).where().eq("from_id", clientId).or().eq("to_id", clientId).prepare();
        }

        return (ArrayList) this.query(preparedQuery);
    }

    public void markMessagesUsRead(int clientId) throws SQLException {

        PreparedQuery<Message> preparedQueryUnreadMessages = queryBuilder().where().eq("to_id", clientId).and().eq("from_trainer", 1).and().eq("read", 0).prepare();
        ArrayList<Message> messages = (ArrayList) this.query(preparedQueryUnreadMessages);

        for (Message message : messages) {
            if (message.read == 0) {
                message.read = 1;
                message.syncStatus = SyncStatus.CHANGED;
                this.update(message);
            }
        }

    }


    public List<Message> getUpdatedOrNewMessages() throws SQLException {
        PreparedQuery<Message> preparedQuery = queryBuilder().where().ne("sync_status", SyncStatus.SYNCED).prepare();
        return this.query(preparedQuery);
    }


    public int getUnreadMessagesCount(int clientId) throws SQLException {
        PreparedQuery<Message> preparedQuery = queryBuilder().where().eq("to_id", clientId).and().eq("from_trainer", 1).and().eq("read", 0).prepare();
        ArrayList<Message> messages = (ArrayList) this.query(preparedQuery);
        if (messages != null) {
            return messages.size();
        }
        return 0;
    }
}
