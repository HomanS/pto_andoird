package nu.pto.androidapp.base.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;

import nu.pto.androidapp.base.AppActivity;
import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.Updateable;
import nu.pto.androidapp.base.adapter.MessageAdapter;
import nu.pto.androidapp.base.db.dao.ClientDao;
import nu.pto.androidapp.base.db.dao.MessageDao;
import nu.pto.androidapp.base.db.dao.TrainerDao;
import nu.pto.androidapp.base.db.model.Client;
import nu.pto.androidapp.base.db.model.Message;
import nu.pto.androidapp.base.db.model.Trainer;
import nu.pto.androidapp.base.util.Constants;
import nu.pto.androidapp.base.util.SharedPreferencesHelper;
import nu.pto.androidapp.base.util.SyncStatus;
import nu.pto.androidapp.base.util.Utils;


public class MessagesFragment extends BaseFragment implements Updateable {

    private ListView messagesListView;
    private Button messageSendButton;
    private EditText messageEditText;

    private final int maxLineCount = 4;
    private MessageAdapter messageAdapter;

    private Client client;
    private Trainer trainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ClientDao clientDao = getDatabaseHelper().getClientDao();
            TrainerDao trainerDao = getDatabaseHelper().getTrainerDao();

            client = clientDao.getClientByClientId(SharedPreferencesHelper.getInt(getActivity(), SharedPreferencesHelper.KEY_CLIENT_ID));
            trainer = trainerDao.getTrainerByTrainerId(SharedPreferencesHelper.getInt(getActivity(), SharedPreferencesHelper.KEY_TRAINER_ID));

            messageAdapter = new MessageAdapter(getActivity(), client, trainer);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        messagesListView = (ListView) getActivity().findViewById(R.id.list_view_messages);
        messageSendButton = (Button) getActivity().findViewById(R.id.message_send_fragment_messages);
        messageEditText = (EditText) getActivity().findViewById(R.id.message_text_fragment_messages);

        messageEditText.setText(SharedPreferencesHelper.getString(getActivity(),"last_inserted_message"));

        messagesListView.setAdapter(messageAdapter);
    }


    @Override
    public void onStart() {
        super.onStart();


        updateView();
    }

    @Override
    public void onResume() {
        super.onResume();
        getBaseActivity().setTitle(getString(R.string.string_messages));

        // sync
        getBaseActivity().doSync();
    }

    @Override
    public void onDestroyView() {

        SharedPreferencesHelper.putString(getActivity(),"last_inserted_message",messageEditText.getText().toString());


        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void updateView() {
        ((AppActivity) getActivity()).setUnreadMessagesCount(0);
        try {
            final MessageDao messageDao = getDatabaseHelper().getMessageDao();


            ArrayList<Message> messages = messageDao.getNMessagesByClientId(-1, client.clientId);

            messageDao.markMessagesUsRead(client.clientId);

            messageAdapter.setMessages(messages);

            messagesListView.setSelection(messageAdapter.getCount() - 1);

            messageEditText.addTextChangedListener(new TextWatcher() {

                int minHeight = 0;

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int newHeight = messageEditText.getLineHeight() * messageEditText.getLineCount();
                    ViewGroup.LayoutParams lp = messageEditText.getLayoutParams();
                    if (minHeight == 0) {
                        minHeight = messageEditText.getHeight();
                    }
                    if (newHeight < minHeight) {
                        newHeight = minHeight;
                    }
                    if (messageEditText.getLineCount() <= maxLineCount) {
                        lp.height = newHeight;
                        messageEditText.setLayoutParams(lp);
                    }
                }
            });


            messageSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String messageText = messageEditText.getText().toString();
                    if (messageText.trim().length() > 0) {
                        getBaseActivity().sendGoogleAnalyticsData(Constants.ANALYTICS_EVENT_CHAT);
                        Message newMessage = new Message();
                        newMessage.fromTrainer = 0;
                        newMessage.fromId = client.clientId;
                        newMessage.toId = trainer.trainerId;
                        newMessage.serverFromId = client.serverClientId;
                        newMessage.serverToId = trainer.serverTrainerId;
                        newMessage.message = messageText;
                        newMessage.read = 0;
                        newMessage.received = 0;
                        newMessage.createdDate = Utils.getCurrentTimestamp();
                        newMessage.updatedDate = Utils.getCurrentTimestamp();
                        newMessage.syncStatus = SyncStatus.NEW;

                        try {
                            messageDao.create(newMessage);

                            messageEditText.setText("");
                            SharedPreferencesHelper.putString(getActivity(),"last_inserted_message","");

                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(messageEditText.getWindowToken(), 0);
                            getBaseActivity().doSync();

                            updateView();

                        } catch (SQLException e) {
                            e.printStackTrace();
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(getActivity().getString(R.string.string_message))
                                    .setMessage(getActivity().getString(R.string.string_message_was_not_send))
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        }
                    } else {
                        new AlertDialog.Builder(getBaseActivity())
                                .setTitle(getActivity().getString(R.string.string_message))
                                .setMessage(getActivity().getString(R.string.string_cannot_send_empty_message))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
