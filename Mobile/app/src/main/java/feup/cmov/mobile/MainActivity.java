package feup.cmov.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

import feup.cmov.mobile.auth.LogInActivity;
import feup.cmov.mobile.auth.RegisterActivity;
import feup.cmov.mobile.BasketActivity;
import feup.cmov.mobile.common.Preferences;
import feup.cmov.mobile.common.Product;

public class MainActivity extends AppCompatActivity {

    public Context context;
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

        context = this;

        //TODO: DELETE
        try {
            testPreferences();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button historyButton = findViewById(R.id.historyButton);
        Button basketButton = findViewById(R.id.basketButton);

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, HistoryActivity.class));
            }
        });

        basketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, BasketActivity.class));
            }
        });



        // TODO: logout
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

    void testPreferences() throws JSONException {
        Preferences preferences = new Preferences(context);

        //VOUCHERS
        ArrayList <String> vouchers = new ArrayList();
        vouchers.add("1a2b3c");
        vouchers.add("4d6e7f");
        vouchers.add("8g9h0j");
        vouchers.add("10g11h12j");
        preferences.saveVouchers(vouchers);
        ArrayList <String> vouchersP = preferences.getVouchers();
        for (int i = 0; i < vouchersP.size(); i++) {
            Log.d("TEST", vouchersP.get(i));
        }

        //DISCOUNT
        float discount = 12.4f;
        preferences.saveDiscount(discount);
        float discountP = preferences.getDiscount();
        Log.d("TEST", Float.toString(discountP));


        //BASKET
        ArrayList <Product> basket = new ArrayList();
        Product product1 = new Product("um", 12.3f);
        Product product2 = new Product("dois", 15.6f);
        Product product3 = new Product("tres", 11.5f);
        basket.add(product1);
        basket.add(product2);
        basket.add(product3);
        preferences.saveBasket(basket);
        ArrayList<Product> basketP = preferences.getBasket();
        for (int i = 0; i < basketP.size(); i++) {
            Log.d("TEST", basketP.get(i).getName() + " " + basketP.get(i).getPrice());
        }

    }
}
