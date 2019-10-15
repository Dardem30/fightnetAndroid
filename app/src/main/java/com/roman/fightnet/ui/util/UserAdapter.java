package com.roman.fightnet.ui.util;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.roman.fightnet.R;
import com.roman.fightnet.requests.models.AppUser;
import com.roman.fightnet.ui.activities.profileActivities.fragments.MapFragment;
import com.roman.fightnet.ui.activities.profileActivities.fragments.OverviewFragment;

import java.util.ArrayList;
import java.util.List;

import static com.roman.fightnet.IConstants.storage;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<AppUser> mDataSet = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    private final FragmentManager fragmentManager;


    public UserAdapter(Context context, List<AppUser> dataSet, FragmentManager fragmentManager) {
        mContext = context;
        mDataSet = dataSet;
        mInflater = LayoutInflater.from(context);
        this.fragmentManager = fragmentManager;

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
        holder.frontLayout.setOnClickListener(view1 -> {
            final OverviewFragment fragment = new OverviewFragment();
            final Bundle bundle = new Bundle();
            bundle.putString("email", user.getEmail());
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        });
        holder.fightButton.setOnClickListener(v -> {
            final MapFragment fragment = new MapFragment();
            final Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        });
    }

    public List<AppUser> getmDataSet() {
        return mDataSet;
    }

    public void setmDataSet(List<AppUser> mDataSet) {
        this.mDataSet = mDataSet;
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public int updateDataSet(final List<AppUser> records, final RecyclerView view) {
        mDataSet.addAll(records);
        view.scrollToPosition(getItemCount() - records.size() - 1);
        return getItemCount();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView name, place, weight, growth, preferableKind;
        private SwipeRevealLayout swipeLayout;
        private View frontLayout;
        private TextView textView;
        private ImageView imageView;
        private Button fightButton;

        public UserViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            imageView = view.findViewById(R.id.userProfilePhoto);
            preferableKind = view.findViewById(R.id.preferableKind);
            place = view.findViewById(R.id.place);
            weight = view.findViewById(R.id.userWeight);
            growth = view.findViewById(R.id.userGrowth);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            frontLayout = itemView.findViewById(R.id.user_card_layout);
            textView = itemView.findViewById(R.id.text);
            fightButton = itemView.findViewById(R.id.fightButton);
        }
    }
}