package feup.cmov.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import feup.cmov.mobile.auth.LogInActivity;
import feup.cmov.mobile.auth.RegisterActivity;
import feup.cmov.mobile.common.Preferences;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check if user is logged in or registered
        Preferences preferences = new Preferences(this);
        if(!preferences.getRegisterStatus()) {
            startActivity(new Intent(this, RegisterActivity.class));
        }
        else if(!preferences.getLoginStatus()) {
            startActivity(new Intent(this, LogInActivity.class));
        }

        // TODO: Add menu

        // TODO: Implement QRCode reading, checkout and listing of transactions
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Preferences preferences = new Preferences(this);
        preferences.changeLogStatus(false);
    }
}
