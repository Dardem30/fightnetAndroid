package com.roman.fightnet.ui.activities.profileActivities.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.roman.fightnet.R;
import com.roman.fightnet.requests.models.Message;
import com.roman.fightnet.requests.models.SocketMessage;
import com.roman.fightnet.requests.service.SocketService;
import com.roman.fightnet.requests.service.UserService;
import com.roman.fightnet.requests.service.util.UtilService;
import com.roman.fightnet.ui.activities.profileActivities.ProfileActivity;
import com.roman.fightnet.ui.util.MessageListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;

import static com.roman.fightnet.IConstants.storage;

public class DialogFragment extends Fragment {
    private final UserService userService = UtilService.getUserService();
    private final SocketService socketService = UtilService.getSocketService();
    private ProfileActivity activity;
    private StompClient mStompClient;
    private RecyclerView mMessageRecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (ProfileActivity) getActivity();
        final View view = inflater.inflate(R.layout.fragment_dialog, container, false);
        final String email = this.getArguments().getString("email");

        userService.getDialog(storage.getEmail(), email, storage.getToken()).enqueue(new Callback<List<Message>>() {
            @SuppressLint("CheckResult")
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                mMessageRecycler = view.findViewById(R.id.reyclerview_message_list);
                mMessageRecycler.setLayoutManager(new LinearLayoutManager(container.getContext()));
                mMessageRecycler.setAdapter(new MessageListAdapter(container.getContext(), response.body()));
                mMessageRecycler.scrollToPosition(response.body().size() - 1);
                try {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", storage.getToken());
                    mStompClient = Stomp.over(Stomp.ConnectionProvider.JWS, "ws://fightnet.herokuapp.com/socket/websocket", headers);
                    List<StompHeader> list = new ArrayList<>();
                    list.add(new StompHeader("Authorization", storage.getToken()));
                    mStompClient.connect(list);
                    mStompClient.topic("/socket-publisher/" + storage.getEmail()).subscribe(object -> messageReceived(object));
                    view.findViewById(R.id.button_chatbox_send).setOnClickListener(v -> {
                        final EditText textField = view.findViewById(R.id.edittext_chatbox);
                        if (textField.getText().toString() != null && !textField.getText().toString().trim().equals("")) {
                            final SocketMessage message = new SocketMessage();
                            message.setText(textField.getText().toString().trim());
                            textField.setText(null);
                            message.setUserResiver(email);
                            message.setPhoto(storage.getMainPhoto());
                            message.setUserSender(storage.getEmail());
                            socketService.sendMessage(message);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {

            }
        });
        return view;
    }
    public void messageReceived(final StompMessage message) {
        activity.runOnUiThread(() -> {
            final Message socketMessage = new Message((SocketMessage) UtilService.castJsonToObject(message.getPayload(), SocketMessage.class));
            ((MessageListAdapter) mMessageRecycler.getAdapter()).updateDataSet(socketMessage, mMessageRecycler);
        });
    }
}
