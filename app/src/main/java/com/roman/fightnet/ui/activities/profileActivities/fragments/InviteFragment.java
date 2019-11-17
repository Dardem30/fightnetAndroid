package com.roman.fightnet.ui.activities.profileActivities.fragments;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.roman.fightnet.R;
import com.roman.fightnet.databinding.FragmentInvitesBinding;
import com.roman.fightnet.requests.models.AppUser;
import com.roman.fightnet.requests.models.Invite;
import com.roman.fightnet.requests.models.SearchResponse;
import com.roman.fightnet.requests.models.searchCriteria.InvitesSearchCriteria;
import com.roman.fightnet.requests.service.UserService;
import com.roman.fightnet.requests.service.util.UtilService;
import com.roman.fightnet.ui.util.GalleryImageAdapter;
import com.roman.fightnet.util.CalendarUtil;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.roman.fightnet.IConstants.storage;

public class InviteFragment extends Fragment {

    private UserService userService = UtilService.getUserService();
    private final InvitesSearchCriteria invitesSearchCriteria = new InvitesSearchCriteria();
    private int page = 1;
    private int index = 0;
    private int maxPage;
    private int maxIndex;
    private boolean isLoading = false;
    private List<Invite> inviteList;
    private static int MIN_SWIPE_DISTANCE_X = 100;
    private static int MAX_SWIPE_DISTANCE_X = 1000;
    private FragmentInvitesBinding invitesBinding;
    private ImageView photoInviter;
    private Invite invite;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        invitesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_invites, container, false);
        final View view = invitesBinding.getRoot();
        photoInviter = view.findViewById(R.id.inviterPhoto);
        invitesSearchCriteria.setEmail(storage.getEmail());
        invitesSearchCriteria.setPage(page);
        userService.getInvites(invitesSearchCriteria).enqueue(new Callback<SearchResponse<Invite>>() {
            @Override
            public void onResponse(Call<SearchResponse<Invite>> call, Response<SearchResponse<Invite>> response) {
                if (response.body() != null) {
                    inviteList = response.body().getRecords();
                    if (inviteList != null && inviteList.size() != 0) {
                        maxPage = response.body().getCount() / 3 + (response.body().getCount() % 3 != 0 ? 1 : 0);
                        maxIndex = inviteList.size() - 1;
                        invite = inviteList.get(index);
                        invite.setDisplayDate(CalendarUtil.formatDateTime(new Date(Long.valueOf(invite.getDate()))));
                        invitesBinding.setInvite(invite);
                        new GalleryImageAdapter.DownLoadImageTask(photoInviter).execute(invite.getFighterInviter().getMainPhoto() + storage.getFacebookToken());
                        final GestureDetectorCompat gestureDetectorCompat = new GestureDetectorCompat(container.getContext(), new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                                if (isLoading) {
                                    return super.onFling(e1, e2, velocityX, velocityY);
                                }
                                isLoading = true;
                                final float deltaX = e1.getX() - e2.getX();
                                final float deltaXAbs = Math.abs(deltaX);
                                if ((deltaXAbs >= MIN_SWIPE_DISTANCE_X) && (deltaXAbs <= MAX_SWIPE_DISTANCE_X)) {
                                    if (deltaX > 0) {
                                        index++;
                                    } else {
                                        index--;
                                    }
                                }
                                if (index < 0) {
                                    if (page != 1) {
                                        page--;
                                        index = 2;
                                        searchInvites();
                                        return super.onFling(e1, e2, velocityX, velocityY);
                                    } else {
                                        index = 0;
                                    }
                                }
                                if (index > maxIndex) {
                                    if (page != maxPage) {
                                        page++;
                                        index = 0;
                                        searchInvites();
                                        return super.onFling(e1, e2, velocityX, velocityY);
                                    } else {
                                        index = maxIndex;
                                    }
                                }
                                invite = inviteList.get(index);
                                invite.setDisplayDate(CalendarUtil.formatDateTime(new Date(Long.valueOf(invite.getDate()))));
                                invitesBinding.setInvite(invite);
                                new GalleryImageAdapter.DownLoadImageTask(photoInviter).execute(invite.getFighterInviter().getMainPhoto() + storage.getFacebookToken());
                                isLoading = false;
                                return super.onFling(e1, e2, velocityX, velocityY);
                            }
                        });
                        setOnTouchListeners(gestureDetectorCompat, view.findViewById(R.id.swipe_tip_layout), view.findViewById(R.id.invite_comment),
                                view.findViewById(R.id.inviter_photo_layout), view.findViewById(R.id.invite_info_layout),
                                view.findViewById(R.id.inviter_surname_layout), view.findViewById(R.id.inviter_name_layout));
                        initButtonListeners(view);
                    } else {
                        final Invite dummyInvite = new Invite();
                        final AppUser dummyUser = new AppUser();
                        dummyUser.setName("You");
                        dummyUser.setSurname("have no");
                        dummyInvite.setFightStyle("any invites");
                        dummyInvite.setComment("YET!!");
                        dummyInvite.setFighterInviter(dummyUser);
                        invitesBinding.setInvite(dummyInvite);
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchResponse<Invite>> call, Throwable t) {
                Log.e("Invites", "Error during trying to get invites for user", t);
            }
        });
        return view;
    }

    private void initButtonListeners(final View view) {
        view.findViewById(R.id.accept_invite).setOnClickListener(v -> {
            isLoading = true;
            invite.setAccepted(true);
            userService.acceptInvite(invite);
            if (index >= maxIndex) {
                index--;
            }
            if (index < 0) {
                if (page != 1) {
                    page--;
                    index = 2;
                    searchInvites();
                } else {
                    final Invite dummyInvite = new Invite();
                    final AppUser dummyUser = new AppUser();
                    dummyUser.setName("You");
                    dummyUser.setSurname("have no");
                    dummyInvite.setFightStyle("any invites");
                    dummyInvite.setComment("YET");
                    dummyInvite.setFighterInviter(dummyUser);
                    invitesBinding.setInvite(dummyInvite);
                    index = 0;
                }
            } else {
                searchInvites();
            }
        });
        view.findViewById(R.id.dialog_invite).setOnClickListener(v -> {
            final DialogFragment fragment = new DialogFragment();
            final Bundle bundle = new Bundle();
            bundle.putString("email", invite.getFighterInviter().getEmail());
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        });
        view.findViewById(R.id.show_place).setOnClickListener(v -> {
            final MapFragment fragment = new MapFragment();
            final Bundle bundle = new Bundle();
            bundle.putDouble("longitude", invite.getLongitude());
            bundle.putDouble("latitude", invite.getLatitude());
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        });
        view.findViewById(R.id.decline_invite).setOnClickListener(v -> {
            isLoading = true;
            userService.declineInvite(invite.getId().toString());
            if (index >= maxIndex) {
                index--;
            }
            if (index < 0) {
                if (page != 1) {
                    page--;
                    index = 2;
                    searchInvites();
                } else {
                    final Invite dummyInvite = new Invite();
                    final AppUser dummyUser = new AppUser();
                    dummyUser.setName("You");
                    dummyUser.setSurname("have no");
                    dummyInvite.setFightStyle("any invites");
                    dummyInvite.setComment("YET");
                    dummyInvite.setFighterInviter(dummyUser);
                    invitesBinding.setInvite(dummyInvite);
                    index = 0;
                }
            } else {
                searchInvites();
            }
        });
    }

    private void searchInvites() {
        invitesSearchCriteria.setPage(page);
        userService.getInvites(invitesSearchCriteria).enqueue(new Callback<SearchResponse<Invite>>() {
            @Override
            public void onResponse(Call<SearchResponse<Invite>> call, Response<SearchResponse<Invite>> response) {
                inviteList = response.body().getRecords();
                maxIndex = inviteList.size() - 1;
                invite = inviteList.get(index);
                invite.setDisplayDate(CalendarUtil.formatDateTime(new Date(Long.valueOf(invite.getDate()))));
                invitesBinding.setInvite(invite);
                isLoading = false;
            }

            @Override
            public void onFailure(Call<SearchResponse<Invite>> call, Throwable t) {
                Log.e("Invites", "Error during trying to get invites for user", t);
            }
        });
    }

    private void setOnTouchListeners(final GestureDetectorCompat gestureDetectorCompat, final View... views) {
        for (final View view : views) {
            view.setOnTouchListener((v, event) -> {
                gestureDetectorCompat.onTouchEvent(event);
                return true;
            });
        }
    }
}
