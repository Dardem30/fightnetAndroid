package com.roman.fightnet.ui.activities.profileActivities.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import com.roman.fightnet.databinding.FragmentFightBinding;
import com.roman.fightnet.requests.models.AppUser;
import com.roman.fightnet.requests.models.Invite;
import com.roman.fightnet.requests.models.SearchResponse;
import com.roman.fightnet.requests.models.searchCriteria.InvitesSearchCriteria;
import com.roman.fightnet.requests.service.UserService;
import com.roman.fightnet.requests.service.util.UtilService;
import com.roman.fightnet.ui.util.GalleryImageAdapter;
import com.roman.fightnet.util.CalendarUtil;

import java.io.File;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.roman.fightnet.IConstants.storage;

public class FightFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

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
    private FragmentFightBinding fightBinding;
    private ImageView photoInviter;
    private Invite invite;
    private static final int REQUEST_FILE_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private Context context;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fightBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_fight, container, false);
        final View view = fightBinding.getRoot();
        photoInviter = view.findViewById(R.id.inviterPhoto);
        invitesSearchCriteria.setEmail(storage.getEmail());
        invitesSearchCriteria.setPage(page);
        userService.getPlannedFights(invitesSearchCriteria).enqueue(new Callback<SearchResponse<Invite>>() {
            @Override
            public void onResponse(Call<SearchResponse<Invite>> call, Response<SearchResponse<Invite>> response) {
                if (response.body() != null) {
                    context = container.getContext();
                    inviteList = response.body().getRecords();
                    if (inviteList != null && inviteList.size() != 0) {
                        maxPage = response.body().getCount() / 3 + (response.body().getCount() % 3 != 0 ? 1 : 0);
                        maxIndex = inviteList.size() - 1;
                        invite = inviteList.get(index);
                        invite.setDisplayDate(CalendarUtil.formatDateTime(new Date(Long.valueOf(invite.getDate()))));
                        invite.setDisplayUser(invite.getFighterInvited().getEmail().equals(storage.getEmail()) ? invite.getFighterInviter() : invite.getFighterInvited());
                        fightBinding.setInvite(invite);
                        new GalleryImageAdapter.DownLoadImageTask(photoInviter).execute(invite.getDisplayUser().getMainPhoto() + storage.getFacebookToken());
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
                                invite.setDisplayUser(invite.getFighterInvited().getEmail().equals(storage.getEmail()) ? invite.getFighterInviter() : invite.getFighterInvited());
                                fightBinding.setInvite(invite);
                                new GalleryImageAdapter.DownLoadImageTask(photoInviter).execute(invite.getDisplayUser().getMainPhoto() + storage.getFacebookToken());
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
                        dummyInvite.setFightStyle("any planned fights");
                        dummyInvite.setComment("YET!!");
                        dummyInvite.setDisplayUser(dummyUser);
                        fightBinding.setInvite(dummyInvite);
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
        view.findViewById(R.id.dialog_invite).setOnClickListener(v -> {
            if (isLoading) {
                return;
            }
            final DialogFragment fragment = new DialogFragment();
            final Bundle bundle = new Bundle();
            bundle.putString("email", invite.getDisplayUser().getEmail());
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        });
        view.findViewById(R.id.show_place).setOnClickListener(v -> {
            if (isLoading) {
                return;
            }
            final MapFragment fragment = new MapFragment();
            final Bundle bundle = new Bundle();
            bundle.putDouble("longitude", invite.getLongitude());
            bundle.putDouble("latitude", invite.getLatitude());
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        });
        view.findViewById(R.id.upload_video).setOnClickListener(v -> {
            if (isLoading) {
                return;
            }
            //check if app has permission to access the external storage.
            if (EasyPermissions.hasPermissions(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showFileChooserIntent();
            } else {
                //If permission is not present request for the same.
                EasyPermissions.requestPermissions(context, getString(R.string.read_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });
    }

    private void searchInvites() {
        invitesSearchCriteria.setPage(page);
        userService.getPlannedFights(invitesSearchCriteria).enqueue(new Callback<SearchResponse<Invite>>() {
            @Override
            public void onResponse(Call<SearchResponse<Invite>> call, Response<SearchResponse<Invite>> response) {
                inviteList = response.body().getRecords();
                maxIndex = inviteList.size() - 1;
                invite = inviteList.get(index);
                invite.setDisplayUser(invite.getFighterInvited().getEmail().equals(storage.getEmail()) ? invite.getFighterInviter() : invite.getFighterInvited());
                invite.setDisplayDate(CalendarUtil.formatDateTime(new Date(Long.valueOf(invite.getDate()))));
                fightBinding.setInvite(invite);
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
                if (isLoading) {
                    return false;
                }
                gestureDetectorCompat.onTouchEvent(event);
                return true;
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILE_CODE && resultCode == Activity.RESULT_OK) {
            try {
                isLoading = true;
                fightBinding.getRoot().findViewById(R.id.uploading).setVisibility(View.VISIBLE);
                final File file = new File(UtilService.getPath(context, data.getData()));
                userService.uploadVideo(file, invite.getFighterInviter().getEmail(), invite.getFighterInvited().getEmail(), invite.getId(), invite.getFightStyle(), storage.getToken()).enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        new AlertDialog.Builder(context)
                                .setTitle("Success")
                                .setMessage("You successfully uploaded video. When video will pass the review you will be able to find this video on tab 'Videos'")
                                .setIcon(R.drawable.englishlocale)
                                .show();
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
                                fightBinding.setInvite(dummyInvite);
                                index = 0;
                                isLoading = false;
                            }
                        } else {
                            searchInvites();
                        }
                        fightBinding.getRoot().findViewById(R.id.uploading).setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {}
                });

            } catch (Exception e) {
                Log.e("FightFragment", "Error during trying to upload video", e);
            }
        }
    }

    private void showFileChooserIntent() {
        Intent fileManagerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        //Choose any file
        fileManagerIntent.setType("*/*");
        startActivityForResult(fileManagerIntent, REQUEST_FILE_CODE);

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        showFileChooserIntent();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    private String getRealPathFromURIPath(Uri contentURI, ContentResolver resolver) throws Exception {
        Cursor cursor = resolver.query(contentURI, null, null, null, null);
        String realPath = "";
        if (cursor == null) {
            realPath = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
            realPath = cursor.getString(idx);
        }
        if (cursor != null) {
            cursor.close();
        }

        return realPath;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
