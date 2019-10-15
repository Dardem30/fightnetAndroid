package com.roman.fightnet.ui.util;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roman.fightnet.R;
import com.roman.fightnet.requests.models.Conversation;
import com.roman.fightnet.ui.activities.profileActivities.fragments.DialogFragment;
import com.roman.fightnet.util.CalendarUtil;

import java.util.Date;
import java.util.List;

import static com.roman.fightnet.IConstants.storage;

public class ConverstionsAdapter extends RecyclerView.Adapter<ConverstionsAdapter.ConverstionsViewHolder> {
    private List<Conversation> mDataSet;
    private final FragmentManager fragmentManager;



    public ConverstionsAdapter(List<Conversation> dataSet, FragmentManager fragmentManager) {
        mDataSet = dataSet;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ConverstionsAdapter.ConverstionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_conversation, parent, false);

        return new ConverstionsAdapter.ConverstionsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ConverstionsAdapter.ConverstionsViewHolder holder, int position) {
        Conversation conversation = mDataSet.get(position);
        final int chars = conversation.getText().length();
        holder.titleName.setText(conversation.getTitleName());
        holder.lastMessage.setText(chars < 30 ? conversation.getText() : conversation.getText().substring(0, 30) + "...");
        holder.createTime.setText(CalendarUtil.formatCreateTimeForMessage(new Date(conversation.getDate())));
        if (conversation.getPhoto() != null) {
            new GalleryImageAdapter.DownLoadImageTask(holder.imageView).execute(conversation.getPhoto() + storage.getFacebookToken());
        } else {
            holder.imageView.setImageResource(R.drawable.nophoto);
        }
        holder.rowConversation.setOnClickListener(v -> {
            final DialogFragment fragment = new DialogFragment();
            final Bundle bundle = new Bundle();
            bundle.putString("email", storage.getEmail().equals(conversation.getUserResiver()) ? conversation.getUserSender() : conversation.getUserResiver());
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class ConverstionsViewHolder extends RecyclerView.ViewHolder {
        private TextView titleName, lastMessage, createTime;
        private ImageView imageView;
        private LinearLayout rowConversation;

        public ConverstionsViewHolder(View view) {
            super(view);
            titleName = view.findViewById(R.id.listview_item_title);
            lastMessage = view.findViewById(R.id.listview_item_short_description);
            imageView = view.findViewById(R.id.listview_image);
            createTime = view.findViewById(R.id.createTime);
            rowConversation = view.findViewById(R.id.rowConversation);
        }
    }
}