package nu.pto.androidapp.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.db.model.Client;
import nu.pto.androidapp.base.db.model.Message;
import nu.pto.androidapp.base.db.model.Trainer;
import nu.pto.androidapp.base.ui.PtoTextView;
import nu.pto.androidapp.base.util.DbImageLoader;
import nu.pto.androidapp.base.util.SyncStatus;
import nu.pto.androidapp.base.util.Utils;


public class MessageAdapter extends BaseAdapter {


    private Context mContext;
    private ArrayList<Message> messages;
    private Client client;
    private Trainer trainer;

    public MessageAdapter(Context context, Client client, Trainer trainer) {
        this.mContext = context;
        this.messages = new ArrayList<Message>();
        this.client = client;
        this.trainer = trainer;
    }

    /**
     * @param messages
     */
    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem(int position) {
        return messages.get(messages.size() - position - 1);
    }

    @Override
    public long getItemId(int position) {
        return messages.size() - position - 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String imageId = "";
        Message message = getItem(position);
        if (convertView != null && convertView.getTag().equals("trainer") && message.fromTrainer == 1) {

        } else if (convertView != null && convertView.getTag().equals("client") && message.fromTrainer == 0) {

        } else {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (message.fromTrainer == 1) {
                // message from trainer
                convertView = inflater.inflate(R.layout.item_fragment_message_from_trainer, null);
                imageId = trainer.photo;
                convertView.setTag("trainer");
            } else {
                // message from client
                convertView = inflater.inflate(R.layout.item_fragment_message_from_client, null);
                imageId = client.photo;
                convertView.setTag("client");
            }
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view_photo);
        DbImageLoader.getInstance().load(mContext, imageId, imageView);

        PtoTextView messageText = (PtoTextView) convertView.findViewById(R.id.text_view_message);
        messageText.setText(message.message);

        PtoTextView messageDate = (PtoTextView) convertView.findViewById(R.id.text_view_message_date);
        messageDate.setText(Utils.formatSecondsToDateForMessages(message.createdDate));

        PtoTextView messageStatus = (PtoTextView) convertView.findViewById(R.id.text_view_message_status);
        if (messageStatus != null) {
            if (message.syncStatus == SyncStatus.NEW){
                messageStatus.setText(mContext.getString(R.string.message_status_sending));
            }else{
                messageStatus.setText(mContext.getString(R.string.message_status_sent));
            }

            if (message.received == 1){
                messageStatus.setText(mContext.getString(R.string.message_status_received));
            }

            if (message.read == 1){
                messageStatus.setText(mContext.getString(R.string.message_status_read));
            }
        }

        return convertView;
    }
}