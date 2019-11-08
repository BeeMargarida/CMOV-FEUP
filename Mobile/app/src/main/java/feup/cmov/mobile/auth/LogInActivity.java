package feup.cmov.mobile.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import feup.cmov.mobile.MainActivity;
import feup.cmov.mobile.R;
import feup.cmov.mobile.common.Preferences;

public class LogInActivity extends AppCompatActivity {

    public Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_log_in);

        Button logInButton = findViewById(R.id.submit);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences preferences = new Preferences(context);
                long storedPassword = preferences.getPassword();

                EditText passwordInput = findViewById(R.id.password);
                String passwordString = passwordInput.getText().toString();

                if(passwordString.equals("")) {
                    Toast.makeText(context, "Please insert your PIN Code.", Toast.LENGTH_SHORT).show();
                }
                else if(Long.parseLong(passwordString) == storedPassword){
                    setResult(RESULT_OK);
                    finish();
                }
                else {
                    Toast.makeText(context, "Wrong PIN Code", Toast.LENGTH_SHORT).show();
                    passwordInput.getText().clear();
                }

            }
        });
    }
}
