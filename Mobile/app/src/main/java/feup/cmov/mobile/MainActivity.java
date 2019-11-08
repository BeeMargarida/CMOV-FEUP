package feup.cmov.mobile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import org.json.JSONException;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;

import feup.cmov.mobile.auth.LogInActivity;
import feup.cmov.mobile.auth.RegisterActivity;
import feup.cmov.mobile.common.Preferences;
import feup.cmov.mobile.common.PubKey;

public class MainActivity extends AppCompatActivity {

    public Context context;
    private boolean isLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check if user is logged in or registered
        final Preferences preferences = new Preferences(this);
        if(!preferences.getRegisterStatus()) {
            startActivity(new Intent(this, RegisterActivity.class));
        }
        else if(!isLoggedIn) {
            startActivityForResult(new Intent(this, LogInActivity.class), 0);
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
                Intent intent = new Intent(context, HistoryActivity.class);
                Bundle extras = new Bundle();
                extras.putBoolean("isLoggedIn", isLoggedIn);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        basketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BasketActivity.class);
                Bundle extras = new Bundle();
                extras.putBoolean("isLoggedIn", isLoggedIn);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoggedIn = false;
                startActivityForResult(new Intent(context, LogInActivity.class), 0);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(getIntent().getExtras() != null && getIntent().getExtras().getBoolean("isLoggedIn")) {
            isLoggedIn = getIntent().getExtras().getBoolean("isLoggedIn");
        }
        if(!isLoggedIn) {
            startActivityForResult(new Intent(this, LogInActivity.class), 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Preferences preferences = new Preferences(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Preferences preferences = new Preferences(this);
        preferences.changeLogStatus(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                this.isLoggedIn = true;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                this.isLoggedIn = false;
            }
        }
    }

    PubKey getPubKey() {
        PubKey pkey = new PubKey();
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry("userKey", null);
            PublicKey pub = ((KeyStore.PrivateKeyEntry)entry).getCertificate().getPublicKey();
            pkey.modulus = ((RSAPublicKey)pub).getModulus().toByteArray();
            pkey.exponent = ((RSAPublicKey)pub).getPublicExponent().toByteArray();
        }
        catch (Exception e) {
            Log.d("DEBUG", e.getMessage());
        }
        return pkey;
    }

    byte[] getPrivExp() {
        byte[] exp = null;

        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry("userKey", null);
            PrivateKey priv = ((KeyStore.PrivateKeyEntry)entry).getPrivateKey();
            exp = ((RSAPrivateKey)priv).getPrivateExponent().toByteArray();
        }
        catch (Exception e) {
            Log.d("DEBUG", e.getMessage());
        }
        if (exp == null)
            exp = new byte[0];
        return exp;
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
