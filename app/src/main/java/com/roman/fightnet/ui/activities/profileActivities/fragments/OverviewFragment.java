package com.roman.fightnet.ui.activities.profileActivities.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;
import com.roman.fightnet.BR;
import com.roman.fightnet.IConstants;
import com.roman.fightnet.R;
import com.roman.fightnet.requests.models.AppUser;
import com.roman.fightnet.requests.models.City;
import com.roman.fightnet.requests.models.Country;
import com.roman.fightnet.requests.models.Loses;
import com.roman.fightnet.requests.models.Wins;
import com.roman.fightnet.requests.service.AuthService;
import com.roman.fightnet.requests.service.UserService;
import com.roman.fightnet.requests.service.util.UtilService;
import com.roman.fightnet.ui.activities.profileActivities.ProfileActivity;
import com.roman.fightnet.ui.util.GalleryImageAdapter;
import com.tiper.MaterialSpinner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import lombok.NoArgsConstructor;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.roman.fightnet.IConstants.chartColours;
import static com.roman.fightnet.IConstants.fightStyles;
import static com.roman.fightnet.IConstants.storage;

@NoArgsConstructor
public class OverviewFragment extends Fragment implements EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_FILE_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private String email;
    private Context context;
    private final UserService userService = UtilService.getUserService();
    private final AuthService authService = UtilService.getAuthService();
    private ImageView selectedImage;
    private View popupInputDialogView = null;
    private List<Country> countriesList = new ArrayList<>();
    private List<City> cityList = new ArrayList<>();
    private String country;
    private String city;
    private String preferredKind;
    private Uri fileUri;
    private File file;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            email = this.getArguments().getString("email");
        } else {
            email = IConstants.storage.getEmail();
        }
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview, container, false);
        userService.getFacebookToken().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                storage.setFacebookToken(response.body());
                userService.findUser(email).enqueue(new Callback<AppUser>() {
                    @Override
                    public void onResponse(Call<AppUser> call, Response<AppUser> response) {
                        context = container.getContext();
                        final AppUser user = response.body();
                        ((ProfileActivity) getActivity()).getToolbar().setTitle(user.getName() + " " + user.getSurname());
                        storage.setMainPhoto(user.getMainPhoto());
                        storage.setUserName(user.getName());
                        storage.setUserSurName(user.getSurname());
                        binding.setVariable(BR.user, user);
                        Wins wins = user.getWins();
                        Loses loses = user.getLoses();
                        int index = 0;
                        if (wins != null) {
                            List<SliceValue> pieDataWins = new ArrayList<>();

                            for (Field field : wins.getClass().getDeclaredFields()) {
                                try {
                                    final int point = (int) field.get(wins);
                                    if (point != 0) {
                                        pieDataWins.add(new SliceValue(point, Color.parseColor(chartColours.get(index))).setLabel(field.getAnnotation(SerializedName.class).value() + ":" + point));
                                        index++;
                                    }
                                } catch (final IllegalAccessException e) {
                                    Log.e("OverviewFragment", "Error during trying to get access to field of object(wins)", e);
                                } catch (final ClassCastException ignored) {
                                } catch (final NullPointerException ignored) {
                                }
                            }

                            PieChartData pieChartDataWins = new PieChartData(pieDataWins);
                            pieChartDataWins.setHasLabels(true).setValueLabelTextSize(14);
                            pieChartDataWins.setHasCenterCircle(true).setCenterText1("Wins (9)").setCenterText1FontSize(10).setCenterText1Color(Color.parseColor("#0097A7"));
                            ((PieChartView) container.findViewById(R.id.chartWins)).setPieChartData(pieChartDataWins);
                        }
                        if (loses != null) {
                            index = 0;
                            List<SliceValue> pieDataLoses = new ArrayList<>();
                            for (Field field : loses.getClass().getDeclaredFields()) {
                                try {
                                    final int point = (int) field.get(loses);
                                    if (point != 0) {
                                        pieDataLoses.add(new SliceValue(point, Color.parseColor(chartColours.get(index))).setLabel(field.getAnnotation(SerializedName.class).value() + ":" + point));
                                        index++;
                                    }
                                } catch (final IllegalAccessException e) {
                                    Log.e("OverviewFragment", "Error during trying to get access to field of object(loses)", e);
                                } catch (final ClassCastException ignored) {
                                } catch (final NullPointerException ignored) {
                                }
                            }
                            PieChartData pieChartDataLoses = new PieChartData(pieDataLoses);
                            pieChartDataLoses.setHasLabels(true).setValueLabelTextSize(14);
                            pieChartDataLoses.setHasCenterCircle(true).setCenterText1("Loses (9)").setCenterText1FontSize(10).setCenterText1Color(Color.parseColor("#0097A7"));
                            ((PieChartView) container.findViewById(R.id.chartLoses)).setPieChartData(pieChartDataLoses);
                        }
                        Gallery gallery = container.findViewById(R.id.gallery);
                        selectedImage = container.findViewById(R.id.imageView);
                        gallery.setSpacing(2);
                        gallery.setPadding(5, 2, 5, 2);
                        if (user.getPhotos() != null) {
                            final GalleryImageAdapter galleryImageAdapter = new GalleryImageAdapter(container.getContext(), user.getPhotos());
                            gallery.setAdapter(galleryImageAdapter);

                            new GalleryImageAdapter.DownLoadImageTask(selectedImage).execute(user.getPhotos().get(0) + storage.getFacebookToken());
                            gallery.setOnItemClickListener((parent, v, position, id) -> {
                                new GalleryImageAdapter.DownLoadImageTask(selectedImage).execute(user.getPhotos().get(position) + storage.getFacebookToken());
                            });
                        }
                        if (bundle == null) {
                            setOnClickListeners(binding, container, user);
                        } else {
                            container.findViewById(R.id.editDesription).setVisibility(View.GONE);
                            container.findViewById(R.id.btn_upload).setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<AppUser> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
        return binding.getRoot();
    }

    private void setOnClickListeners(ViewDataBinding binding, ViewGroup container, AppUser user) {
        container.findViewById(R.id.editDesription).setOnClickListener(v -> {
            popupInputDialogView = LayoutInflater.from(container.getContext()).inflate(R.layout.popup_edit_description_dialog, null);
            final EditText descriptionField = popupInputDialogView.findViewById(R.id.editDescription);
            final EditText weightField = popupInputDialogView.findViewById(R.id.editWeight);
            final EditText growthField = popupInputDialogView.findViewById(R.id.editGrowth);
            final MaterialSpinner countries = popupInputDialogView.findViewById(R.id.editCountry);
            final MaterialSpinner cititesSpinner = popupInputDialogView.findViewById(R.id.editCity);
            final MaterialSpinner preferableFightStyleSpinner = popupInputDialogView.findViewById(R.id.editPreferable);
            initSpinners(countries, cititesSpinner, preferableFightStyleSpinner, container, user);
            descriptionField.setText(user.getDescription());
            weightField.setText(user.getWeight());
            growthField.setText(user.getGrowth());
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(container.getContext());
            alertDialogBuilder.setTitle("Edit description");
            alertDialogBuilder.setIcon(R.drawable.russianlocale);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setView(popupInputDialogView);
            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            popupInputDialogView.findViewById(R.id.button_save_user_data).setOnClickListener(view -> {
                user.setDescription(descriptionField.getText().toString());
                user.setWeight(weightField.getText().toString());
                user.setGrowth(growthField.getText().toString());
                user.setCity(city);
                user.setCountry(country);
                user.setPreferredKind(preferredKind);
                binding.setVariable(BR.user, user);
                try {
                    userService.updateChangableInfoToUser(user);
                } catch (IOException e) {
                    Log.e("Overview", "Error during trying to update user's info");
                }
                alertDialog.cancel();
            });

            popupInputDialogView.findViewById(R.id.button_cancel_user_data).setOnClickListener(view -> alertDialog.cancel());
        });
        container.findViewById(R.id.btn_upload).setOnClickListener(view -> {
            //check if app has permission to access the external storage.
            if (EasyPermissions.hasPermissions(container.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showFileChooserIntent();

            } else {
                //If permission is not present request for the same.
                EasyPermissions.requestPermissions(container.getContext(), getString(R.string.read_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        });
    }
    private void showFileChooserIntent() {
        Intent fileManagerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        //Choose any file
        fileManagerIntent.setType("image/*");
        startActivityForResult(fileManagerIntent, REQUEST_FILE_CODE);

    }
    private void initSpinners(final MaterialSpinner countries, final  MaterialSpinner cititesSpinner, final  MaterialSpinner preferableFightStyleSpinner, final  ViewGroup container, final  AppUser user) {
        final ArrayAdapter<String> fightStylesAdapter = UtilService.setupStringAdapter(fightStyles, container.getContext());
        preferableFightStyleSpinner.setAdapter(fightStylesAdapter);
        preferableFightStyleSpinner.setSelection(fightStylesAdapter.getPosition(user.getPreferredKind()));
        preferableFightStyleSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner materialSpinner) {

            }

            @Override
            public void onItemSelected(MaterialSpinner materialSpinner, View view, int position, long l) {
                preferredKind = fightStyles.get(position);

            }
        });
        authService.getCountries().enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                countriesList = response.body();
                final List<String> countryNames = new ArrayList<>(countriesList.size());
                countryNames.add("Country");
                for (final Country country : countriesList) {
                    countryNames.add(country.getName());
                }
                final ArrayAdapter<String> countriesAdapter = UtilService.setupStringAdapter(countryNames, container.getContext());
                countries.setAdapter(countriesAdapter);
                countries.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                    @Override
                    public void onNothingSelected(MaterialSpinner materialSpinner) {

                    }

                    @Override
                    public void onItemSelected(MaterialSpinner materialSpinner, View view, int position, long l) {
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
                                        ArrayAdapter<String> adapterCities = UtilService.setupStringAdapter(cityNames, container.getContext());
                                        cititesSpinner.setAdapter(adapterCities);
                                        cititesSpinner.setSelection(adapterCities.getPosition(user.getCity()));
                                        cititesSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                                            @Override
                                            public void onNothingSelected(MaterialSpinner materialSpinner) {

                                            }

                                            @Override
                                            public void onItemSelected(MaterialSpinner materialSpinner, View view, int positionCity, long l) {
                                                city = cityNames.get(positionCity);

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<City>> call, Throwable t) {

                                }
                            });
                        } catch (Exception e) {
                            Log.e("Overview fragment", "Error during trying to setup cities spinner", e);
                        }
                    }
                });
                countries.setSelection(countriesAdapter.getPosition(user.getCountry()));
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILE_CODE && resultCode == Activity.RESULT_OK) {
            fileUri = data.getData();
            file = new File(UtilService.getPath(context, fileUri));
            userService.uploadPhoto(file, email, storage.getToken()).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call call, Response response) {
                    new AlertDialog.Builder(context)
                            .setTitle("Success")
                            .setMessage("You successfully upload photo. When photo pass the review you will see this photo in your gallery")
                            .setIcon(R.drawable.englishlocale)
                            .show();
                }

                @Override
                public void onFailure(Call call, Throwable t) {

                }
            });
        }
    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        showFileChooserIntent();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    private String getRealPathFromURIPath(Uri contentURI, ContentResolver resolver) {
        Cursor cursor = resolver.query(contentURI, null, null, null, null);
        String realPath = "";
        if (cursor == null) {
            realPath = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
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