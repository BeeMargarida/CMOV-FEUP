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
import java.util.UUID;

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
        final Preferences preferences = new Preferences(this);
        if(!preferences.getRegisterStatus()) {
            startActivity(new Intent(this, RegisterActivity.class));
        }
        else if(!preferences.getLoginStatus()) {
            startActivity(new Intent(this, LogInActivity.class));
        }

        context = this;

        //TODO: DELETE
        try {
            testPreferences();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button historyButton = findViewById(R.id.historyButton);
        Button basketButton = findViewById(R.id.basketButton);
        Button logoutButton = findViewById(R.id.logoutButton);

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

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.changeLogStatus(false);
                startActivity(new Intent(context, LogInActivity.class));
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
        /*ArrayList <Product> basket = new ArrayList();
        Product product1 = new Product(UUID.randomUUID(),"um", 12.3f);
        Product product2 = new Product(UUID.randomUUID(),"dois", 15.6f);
        Product product3 = new Product(UUID.randomUUID(),"tres", 11.5f);
        Product product4 = new Product(UUID.randomUUID(),"quatro", 12.3f);
        Product product5 = new Product(UUID.randomUUID(),"cinco", 15.6f);
        Product product6 = new Product(UUID.randomUUID(),"seis", 11.5f);
        Product product7 = new Product(UUID.randomUUID(),"sete", 12.3f);
        Product product8 = new Product(UUID.randomUUID(),"oito", 15.6f);
        Product product9 = new Product(UUID.randomUUID(),"nove", 11.5f);
        Product product10 = new Product(UUID.randomUUID(),"dez", 12.3f);
        Product product11 = new Product(UUID.randomUUID(),"onze", 15.6f);
        Product product12 = new Product(UUID.randomUUID(),"doze", 11.5f);
        Product product13 = new Product(UUID.randomUUID(),"treze", 12.3f);
        basket.add(product1);
        basket.add(product2);
        basket.add(product3);
        basket.add(product4);
        basket.add(product5);
        basket.add(product6);
        basket.add(product7);
        basket.add(product8);
        basket.add(product9);
        basket.add(product10);
        basket.add(product11);
        basket.add(product12);
        basket.add(product13);
        preferences.saveBasket(basket);
        ArrayList<Product> basketP = preferences.getBasket();
        for (int i = 0; i < basketP.size(); i++) {
            Log.d("TEST", basketP.get(i).getName() + " " + basketP.get(i).getPrice());
        }*/

    }
}
