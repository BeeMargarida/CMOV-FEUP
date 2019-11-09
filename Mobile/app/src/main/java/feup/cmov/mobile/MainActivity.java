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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.UUID;

import feup.cmov.mobile.auth.LogInActivity;
import feup.cmov.mobile.auth.RegisterActivity;
import feup.cmov.mobile.common.Preferences;
import feup.cmov.mobile.common.PubKey;
import feup.cmov.mobile.operations.SupermarketOperation;

public class MainActivity extends AppCompatActivity implements SupermarketOperation.Supermarket {

    public Context context;
    private boolean isLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        // check if user is logged in or registered
        final Preferences preferences = new Preferences(this);
        if(!preferences.getRegisterStatus()) {
            startActivity(new Intent(this, RegisterActivity.class));
        }
        else if(!isLoggedIn) {
            startActivityForResult(new Intent(this, LogInActivity.class), 0);
        }

        //TODO: DELETE
        /*try {
            testPreferences();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

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
        else {
            try {
                SupermarketOperation supermarketOperation = new SupermarketOperation(context, getUserUUID(), getUserKey());
                Thread thread = new Thread(supermarketOperation);
                thread.start();

            } catch (Exception e) {
                Toast.makeText(context, "There was a problem getting the user information, please try again.", Toast.LENGTH_SHORT).show();
            }
        }
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

    private byte[] getUserUUID() {
        Preferences preferences = new Preferences(context);
        String userUUIDString = preferences.getUserUUID();
        UUID userUUID = UUID.fromString(userUUIDString);

        ByteBuffer bb = ByteBuffer.allocate(16);
        bb.putLong(userUUID.getMostSignificantBits());
        bb.putLong(userUUID.getLeastSignificantBits());
        return bb.array();
    }

    private PrivateKey getUserKey() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException, CertificateException, IOException {
        KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
        ks.load(null);
        KeyStore.Entry entry = ks.getEntry("userKey", null);
        PrivateKey pri = ((KeyStore.PrivateKeyEntry)entry).getPrivateKey();
        return pri;
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

    @Override
    public void done(boolean success, int requestCode, JSONObject response, String error) {
        if(!success) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
        }
        else {

            switch (requestCode) {
                case 0:
                    try {

                        JSONArray vouchers = response.getJSONArray("vouchers");
                        Preferences preferences = new Preferences(context);
                        preferences.saveVouchers(vouchers);

                    } catch(JSONException e) {
                        Toast.makeText(context, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    try {

                        float discount = (float) response.getDouble("discount");
                        Preferences preferences = new Preferences(context);
                        preferences.saveDiscount(discount);

                    } catch(JSONException e) {
                        Toast.makeText(context, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    try {

                        JSONArray transactions = response.getJSONArray("transactions");
                        Preferences preferences = new Preferences(context);

                    } catch(JSONException e) {
                        Toast.makeText(context, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
