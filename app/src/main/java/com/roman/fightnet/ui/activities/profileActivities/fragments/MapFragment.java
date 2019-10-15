package com.roman.fightnet.ui.activities.profileActivities.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.roman.fightnet.R;
import com.roman.fightnet.databinding.FragmentMapBinding;
import com.roman.fightnet.requests.models.AppUser;
import com.roman.fightnet.requests.models.Marker;
import com.roman.fightnet.requests.models.searchCriteria.MapSearchCriteria;
import com.roman.fightnet.requests.service.UserService;
import com.roman.fightnet.requests.service.util.UtilService;
import com.roman.fightnet.ui.activities.profileActivities.ProfileActivity;
import com.roman.fightnet.util.CalendarUtil;
import com.tiper.MaterialSpinner;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.roman.fightnet.IConstants.PREFERABLE_KIND;
import static com.roman.fightnet.IConstants.fightStyles;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private final UserService userService = UtilService.getUserService();
    private final MapSearchCriteria searchCriteria = new MapSearchCriteria();
    private GoogleMap googleMap;
    private String preferredKind;
    private com.google.android.gms.maps.model.Marker currentMarker;
    private ProfileActivity profileActivity;
    private double inviteLatitude;
    private double inviteLongitude;
    private boolean isInvite = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            final AppUser user = (AppUser) bundle.getSerializable("user");
            if (user != null) {
                isInvite = true;
                profileActivity = (ProfileActivity) getActivity();
                profileActivity.initInvite(user);
            } else {
                inviteLatitude = bundle.getDouble("latitude");
                inviteLongitude = bundle.getDouble("longitude");
            }
        }
        final FragmentMapBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
        binding.setVariable(com.roman.fightnet.BR.searchCriteria, searchCriteria);

        final View view = binding.getRoot();
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
        final MaterialSpinner preferableFightStyleSpinner = view.findViewById(R.id.searchPreferableFightStyleOnMap);
        preferableFightStyleSpinner.setAdapter(UtilService.setupStringAdapter(fightStyles, container.getContext()));
        preferableFightStyleSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner materialSpinner) {

            }

            @Override
            public void onItemSelected(MaterialSpinner materialSpinner, View view, int position, long l) {
                preferredKind = fightStyles.get(position);

            }
        });

        view.findViewById(R.id.searchMarkersFromSearchPanel).setOnClickListener(v -> searchMarkers());

        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottom_map_search_criteria_layout));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(140);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    searchMarkers();
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });
        UtilService.setupDatepickersDate(container.getContext(), view.findViewById(R.id.startDate), view.findViewById(R.id.endDate));
        return binding.getRoot();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setOnMapClickListener(latLng -> {
            if (currentMarker != null) {
                currentMarker.remove();
            }
            currentMarker = googleMap.addMarker(new MarkerOptions().position(latLng));
            if (isInvite) {
                profileActivity.setCoordinate(latLng.latitude, latLng.longitude);
            }
        });
        this.googleMap = googleMap;
        searchMarkers();
    }
    private void searchMarkers() {
        googleMap.clear();
        if (preferredKind != null && !preferredKind.equals(PREFERABLE_KIND)) {
            searchCriteria.setFightStyle(preferredKind);
        }
        userService.getMarkers(searchCriteria).enqueue(new Callback<List<Marker>>() {
            @Override
            public void onResponse(Call<List<Marker>> call, Response<List<Marker>> response) {
                if (inviteLatitude != 0 && inviteLongitude != 0) {
                    final LatLng inviteLatLng = new LatLng(inviteLatitude, inviteLongitude);
                    googleMap.addMarker(new MarkerOptions().position(inviteLatLng));
                    final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(new LatLngBounds.Builder().include(inviteLatLng).build(), -400);
                    googleMap.animateCamera(cameraUpdate);
                    inviteLatitude = 0;
                    inviteLongitude = 0;
                }
                if (response != null && response.body() != null) {
                    for (final Marker marker: response.body()) {
                        final LatLng latLng = new LatLng(marker.getLatitude(), marker.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(marker.getFighterInviter().getName() + " " + marker.getFighterInviter().getSurname() + " vs "
                                + marker.getFighterInvited().getName() + " " + marker.getFighterInvited().getName() + "(" + CalendarUtil.formatDateTime(marker.getDate()) + ")"));
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Marker>> call, Throwable t) {
                Log.e("MapFragment", "Error during trying to load markers", t);
            }
        });
    }
}
