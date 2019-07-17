package com.roman.fightnet.ui.activities.profileActivities.fragments;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.roman.fightnet.BR;
import com.roman.fightnet.R;
import com.roman.fightnet.requests.models.searchCriteria.UserSearchCriteria;
import com.roman.fightnet.requests.service.UserService;
import com.roman.fightnet.requests.service.util.UtilService;

public class SearchFragment extends Fragment {
    private final UserService userService = UtilService.getUserService();
    private final UserSearchCriteria searchCriteria = new UserSearchCriteria();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        binding.setVariable(BR.searchCriteria, searchCriteria);
        final View view = binding.getRoot();
        final EditText searchField = view.findViewById(R.id.searchField);
        searchField.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (searchField.getRight() - searchField.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    Toast.makeText(container.getContext(), "map", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            return false;
        });
        LinearLayout llBottomSheet = view.findViewById(R.id.bottom_sheet);

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheetBehavior.setPeekHeight(140);

        bottomSheetBehavior.setHideable(false);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                System.out.println(bottomSheet);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                searchCriteria.setPageNum(1);
                System.out.println(searchCriteria.getWidth());
                System.out.println("slide");
            }
        });
        return view;
    }
}
