package com.roman.fightnet.ui.activities.profileActivities.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roman.fightnet.IConstants;
import com.roman.fightnet.R;
import com.roman.fightnet.requests.models.Conversation;
import com.roman.fightnet.requests.service.UserService;
import com.roman.fightnet.requests.service.util.UtilService;
import com.roman.fightnet.ui.util.ConverstionsAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.roman.fightnet.IConstants.storage;

public class MessageFragment extends Fragment {
    private final UserService userService = UtilService.getUserService();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_message, container, false);
        userService.getConversations(IConstants.storage.getEmail(), storage.getToken()).enqueue(new Callback<List<Conversation>>() {
            @Override
            public void onResponse(Call<List<Conversation>> call, Response<List<Conversation>> response) {
                final RecyclerView conversations = view.findViewById(R.id.conversations);
                conversations.addItemDecoration(new DividerItemDecoration(container.getContext(),
                        DividerItemDecoration.VERTICAL));
                conversations.setLayoutManager(new LinearLayoutManager(container.getContext()));
                conversations.setAdapter(new ConverstionsAdapter(response.body(), getFragmentManager()));
            }

            @Override
            public void onFailure(Call<List<Conversation>> call, Throwable t) {

            }
        });

        return view;
    }
}
