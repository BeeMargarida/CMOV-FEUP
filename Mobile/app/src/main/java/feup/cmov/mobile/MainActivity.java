package feup.cmov.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import feup.cmov.mobile.auth.LogInActivity;
import feup.cmov.mobile.auth.RegisterActivity;
import feup.cmov.mobile.BasketActivity;
import feup.cmov.mobile.common.Preferences;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check if user is logged in or registered
        /*Preferences preferences = new Preferences(this);
        if(!preferences.getRegisterStatus()) {
            startActivity(new Intent(this, RegisterActivity.class));
        }
        else if(!preferences.getLoginStatus()) {
            startActivity(new Intent(this, LogInActivity.class));
        }*/

        //TEST INTERFACE//
        startActivity(new Intent(this, BasketActivity.class));

        //TODO: Add buttons to Basket and History activities, as well as logout
        // para fazer logout é só fazer o que os métodos abaixo estão a fazer e a fazer novo
        // start activity para LogInActivity
    }


    @Override
    protected void onPause() {
        super.onPause();
        Preferences preferences = new Preferences(this);
        preferences.changeLogStatus(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Preferences preferences = new Preferences(this);
        preferences.changeLogStatus(false);
    }
}
