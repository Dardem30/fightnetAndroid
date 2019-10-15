package com.roman.fightnet.ui.activities.authActivities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.roman.fightnet.R;
import com.roman.fightnet.requests.models.City;
import com.roman.fightnet.requests.models.Country;
import com.roman.fightnet.requests.service.AuthService;
import com.roman.fightnet.requests.service.util.UtilService;
import com.tiper.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.roman.fightnet.IConstants.fightStyles;
import static com.roman.fightnet.IConstants.storage;

public class SignupActivity extends AppCompatActivity {
    private final AuthService authService = UtilService.getAuthService();
    private static final String TAG = "SignupActivity";
    private List<Country> countriesList = new ArrayList<>();
    private List<City> cityList = new ArrayList<>();

    @BindView(R.id.input_name)
    EditText name;
    @BindView(R.id.input_surname)
    EditText surname;
    @BindView(R.id.input_email)
    EditText email;
    @BindView(R.id.weight)
    EditText weight;
    @BindView(R.id.growth)
    EditText growth;
    @BindView(R.id.description)
    EditText description;
    @BindView(R.id.input_password)
    EditText password;
    @BindView(R.id.input_reEnterPassword)
    EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;
    String preferredKind;
    String country;
    String city;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        final MaterialSpinner preferableFightStyleSpinner = findViewById(R.id.preferableFightStyle);
        preferableFightStyleSpinner.setAdapter(UtilService.setupStringAdapter(fightStyles, this));
        preferableFightStyleSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner materialSpinner) {

            }

            @Override
            public void onItemSelected(MaterialSpinner materialSpinner, View view, int position, long l) {
                preferredKind = fightStyles.get(position);
            }
        });
        // TODO parallel stream
        try {
            authService.getCountries().enqueue(new Callback<List<Country>>() {
                @Override
                public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                    countriesList = response.body();
                    MaterialSpinner countries = findViewById(R.id.countries);
                    final List<String> countryNames = new ArrayList<>(countriesList.size());
                    countryNames.add("Country");
                    for (final Country country: countriesList) {
                        countryNames.add(country.getName());
                    }
                    countries.setAdapter(UtilService.setupStringAdapter(countryNames, SignupActivity.this));
                    countries.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(MaterialSpinner materialSpinner, View view, int position, long l) {
                            country = countryNames.get(position);
                            try {
                                authService.getCities(country).enqueue(new Callback<List<City>>() {
                                    @Override
                                    public void onResponse(Call<List<City>> call, Response<List<City>> responseCity) {
                                        cityList = responseCity.body();
                                        if (cityList != null) {
                                            final MaterialSpinner cititesSpinner = findViewById(R.id.citites);
                                            final List<String> cityNames = new ArrayList<>(cityList.size());
                                            cityNames.add("City");
                                            for (final City city : cityList) {
                                                cityNames.add(city.getName());
                                            }
                                            cititesSpinner.setAdapter(UtilService.setupStringAdapter(cityNames, SignupActivity.this));
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
                                Log.e(TAG, "Error during trying to setup cities spinner", e);
                            }
                        }

                        @Override
                        public void onNothingSelected(MaterialSpinner materialSpinner) {

                        }
                    });
                }

                @Override
                public void onFailure(Call<List<Country>> call, Throwable t) {

                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error during trying to setup countries spinner", e);
        }

        _signupButton.setOnClickListener(v -> signup());

        _loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        });
    }


    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        authService.sendCode(this, name, surname, email, weight, growth, description, password, preferredKind, country, city).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.toString().equals("false")) {
                    Toast.makeText(getBaseContext(), "Sorry but user with this email already exist if you forgot the code please try again in few days", Toast.LENGTH_LONG).show();
                    onSignupFailed();
                    finish();
                } else {
                    progressDialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), SendCodeActivity.class);
                    storage.setEmail(email.getText().toString());
                    startActivityForResult(intent, 0);
                    finish();
                    overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, "Error during trying to save user", t);
                onSignupFailed();
                finish();
            }
        });
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Please fill all the fields", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = name.getText().toString();
        String useremail = email.getText().toString();
        String userpassword = password.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (username.isEmpty()) {
            name.setError("Please fill this field");
            valid = false;
        } else {
            name.setError(null);
        }


        if (useremail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(useremail).matches()) {
            email.setError("enter a valid email address");
            valid = false;
        } else {
            email.setError(null);
        }

        if (userpassword.isEmpty() || userpassword.length() < 4 || userpassword.length() > 15) {
            password.setError("between 4 and 15 alphanumeric characters");
            valid = false;
        } else {
            password.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 5 || reEnterPassword.length() > 15 || !(reEnterPassword.equals(userpassword))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }
}