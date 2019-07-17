package com.roman.fightnet.ui.activities.authActivities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.roman.fightnet.R;
import com.roman.fightnet.requests.service.AuthService;
import com.roman.fightnet.requests.service.util.UtilService;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.roman.fightnet.IConstants.storage;

public class SendCodeActivity extends AppCompatActivity {
    private final AuthService service = UtilService.getAuthService();

    @BindView(R.id.code)
    EditText code;
    @BindView(R.id.btn_send_code)
    Button sendCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_code);
        ButterKnife.bind(this);
        Toast.makeText(getBaseContext(), "Check your email for code", Toast.LENGTH_LONG).show();
        sendCode.setOnClickListener(v -> service.registration(storage.getEmail(), code.getText().toString()).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.toString().equals("false")) {
                    Toast.makeText(getBaseContext(), "Wrong code", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                finish();
            }
        }));
    }
}
