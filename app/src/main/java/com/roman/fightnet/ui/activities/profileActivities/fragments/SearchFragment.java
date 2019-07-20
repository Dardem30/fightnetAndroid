package com.roman.fightnet.ui.activities.profileActivities.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.roman.fightnet.BR;
import com.roman.fightnet.R;
import com.roman.fightnet.databinding.FragmentSearchBinding;
import com.roman.fightnet.requests.models.AppUser;
import com.roman.fightnet.requests.models.City;
import com.roman.fightnet.requests.models.Country;
import com.roman.fightnet.requests.models.SearchResponse;
import com.roman.fightnet.requests.models.searchCriteria.UserSearchCriteria;
import com.roman.fightnet.requests.service.AuthService;
import com.roman.fightnet.requests.service.UserService;
import com.roman.fightnet.requests.service.util.UtilService;
import com.roman.fightnet.ui.util.UserAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.roman.fightnet.IConstants.fightStyles;
import static com.roman.fightnet.IConstants.storage;

public class SearchFragment extends Fragment {
    private final AuthService authService = UtilService.getAuthService();
    private final UserService userService = UtilService.getUserService();
    private final UserSearchCriteria searchCriteria = new UserSearchCriteria();
    private List<Country> countriesList = new ArrayList<>();
    private List<City> cityList = new ArrayList<>();
    String preferredKind;
    String country;
    String city;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        searchCriteria.setSearcherEmail(storage.getEmail());
        final FragmentSearchBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        binding.setVariable(BR.searchCriteria, searchCriteria);
        final View view = binding.getRoot();
        final EditText searchField = view.findViewById(R.id.searchField);
        searchField.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (searchField.getRight() - searchField.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    search(container);
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
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    search(container);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                System.out.println("test");
            }
        });
        final Spinner preferableFightStyleSpinner = view.findViewById(R.id.searchPreferableFightStyle);
        preferableFightStyleSpinner.setAdapter(UtilService.setupStringAdapter(fightStyles, container.getContext()));
        preferableFightStyleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                preferredKind = fightStyles.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // TODO parallel stream
        try {
            authService.getCountries().enqueue(new Callback<List<Country>>() {
                @Override
                public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                    countriesList = response.body();
                    container.findViewById(R.id.searchFromSearchPanel).setOnClickListener(view1 -> search(container));
                    Spinner countries = view.findViewById(R.id.searchCountries);
                    final Spinner cititesSpinner = view.findViewById(R.id.searchCitites);
                    final List<String> countryNames = new ArrayList<>(countriesList.size());
                    countryNames.add("Country");
                    for (final Country country : countriesList) {
                        countryNames.add(country.getName());
                    }
                    countries.setAdapter(UtilService.setupStringAdapter(countryNames, container.getContext()));
                    countries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            country = countryNames.get(position);
                            try {
                                authService.getCities(country).enqueue(new Callback<List<City>>() {
                                    @Override
                                    public void onResponse(Call<List<City>> call, Response<List<City>> responseCity) {
                                        cityList = responseCity.body();
                                        if (cityList != null) {
                                            final List<String> cityNames = new ArrayList<>(cityList.size());
                                            cityNames.add("City");
                                            for (final City city : cityList) {
                                                cityNames.add(city.getName());
                                            }
                                            cititesSpinner.setAdapter(UtilService.setupStringAdapter(cityNames, container.getContext()));
                                            cititesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> parent, View view, int positionCity, long id) {
                                                    city = cityNames.get(positionCity);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<City>> call, Throwable t) {

                                    }
                                });
                            } catch (Exception e) {
                                Log.e("SearchFragment", "Error during trying to setup cities spinner", e);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }

                @Override
                public void onFailure(Call<List<Country>> call, Throwable t) {

                }
            });

        } catch (Exception e) {
            Log.e("SearchFragment", "Error during trying to setup countries spinner", e);
        }
        search(container);
        return view;
    }
    private void search(final ViewGroup container) {
        if (city != null && !city.equals("City")) {
            searchCriteria.setCity(city);
        }
        if (country != null && !country.equals("Country")) {
            searchCriteria.setCountry(country);
        }
        if (preferredKind != null && !preferredKind.equals("Preferable fight style")) {
            searchCriteria.setPreferredKind(preferredKind);
        }
        userService.listUsers(searchCriteria).enqueue(new Callback<SearchResponse<AppUser>>() {
            @Override
            public void onResponse(Call<SearchResponse<AppUser>> call, Response<SearchResponse<AppUser>> response) {
                RecyclerView recyclerView = container.findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
                recyclerView.setAdapter(new UserAdapter(container.getContext(), response.body().getRecords()));
            }

            @Override
            public void onFailure(Call<SearchResponse<AppUser>> call, Throwable t) {
                System.out.println("erorro");
            }
        });
    }
}
