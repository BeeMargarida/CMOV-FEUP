package feup.cmov.mobile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.UUID;

import feup.cmov.mobile.auth.LogInActivity;
import feup.cmov.mobile.auth.RegisterActivity;
import feup.cmov.mobile.common.Preferences;
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
                        preferences.savePurchases(transactions);

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
