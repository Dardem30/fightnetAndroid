package com.roman.fightnet.ui.activities.authActivities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.roman.fightnet.IConstants;
import com.roman.fightnet.R;
import com.roman.fightnet.ui.activities.profileActivities.ProfileActivity;
import com.roman.fightnet.requests.service.AuthService;
import com.roman.fightnet.requests.service.util.UtilService;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private final AuthService authService = UtilService.getAuthService();

    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        _loginButton.setOnClickListener(v -> login());

        _signupLink.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
            startActivityForResult(intent, 0);
            finish();
            overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        });
    }
    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        final String email = _emailText.getText().toString();

        authService.login(email, _passwordText.getText().toString()).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call call, Response response) {
                final String token = response.headers().get("Authorization");
                if (token == null) {
                    onLoginFailed();
                } else {
                    IConstants.storage.setToken(token);
                    IConstants.storage.setEmail(email);
                    startActivityForResult(new Intent(getApplicationContext(), ProfileActivity.class), 0);
                    finish();
                    overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progressDialog.dismiss();
                onLoginFailed();
            }
        });
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
