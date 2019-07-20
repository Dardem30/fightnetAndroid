package com.roman.fightnet.ui.util;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.roman.fightnet.R;
import com.roman.fightnet.requests.models.AppUser;

import java.util.ArrayList;
import java.util.List;

import static com.roman.fightnet.IConstants.storage;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<AppUser> mDataSet = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();


    public UserAdapter(Context context, List<AppUser> dataSet) {
        mContext = context;
        mDataSet = dataSet;
        mInflater = LayoutInflater.from(context);

        // uncomment if you want to open only one row at a time
        // binderHelper.setOpenOnlyOne(true);
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_list, parent, false);

        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        AppUser user = mDataSet.get(position);

        holder.name.setText(user.getName() + " " + user.getSurname());
        holder.place.setText(user.getCity() + ", " + user.getCountry());
        holder.preferableKind.setText(user.getPreferredKind());
        holder.growth.setText(user.getGrowth());
        holder.weight.setText(user.getWeight());
        if (user.getMainPhoto() != null) {
            new GalleryImageAdapter.DownLoadImageTask(holder.imageView).execute(user.getMainPhoto() + storage.getFacebookToken());
        } else {
            holder.imageView.setImageResource(R.drawable.nophoto);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView name, place, weight, growth, preferableKind;
        private SwipeRevealLayout swipeLayout;
        private View frontLayout;
        private View deleteLayout;
        private TextView textView;
        private ImageView imageView;

        public UserViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            imageView = view.findViewById(R.id.userProfilePhoto);
            preferableKind = view.findViewById(R.id.preferableKind);
            place = view.findViewById(R.id.place);
            weight = view.findViewById(R.id.userWeight);
            growth = view.findViewById(R.id.userGrowth);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            frontLayout = itemView.findViewById(R.id.front_layout);
            deleteLayout = itemView.findViewById(R.id.delete_layout);
            textView = itemView.findViewById(R.id.text);
        }
    }
}