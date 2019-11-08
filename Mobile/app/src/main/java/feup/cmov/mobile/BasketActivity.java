package feup.cmov.mobile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;

import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.UUID;

import feup.cmov.mobile.common.Preferences;
import feup.cmov.mobile.common.Product;

public class BasketActivity extends AppCompatActivity {

    public final int CAMERA_PERMISSIONS_CODE = 0;
    public Context context;

    private ArrayList<Product> basketP = new ArrayList<>();
    private BasketAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_basket);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setBasket();
                    setTotal();
                    setDiscount();
                    setVouchersSize();
                }
                catch (JSONException e) {
                    Toast.makeText(context, "An error occurred, please try again.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        t.start();

        //TODO: Chamada a API para atualizar preferencias (n vouchers, dicounts) - fazer com encriptação do user id

        Button addProductButton = findViewById(R.id.addProductButton);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(basketP.size() >= 10){
                    Toast.makeText(context, "You can only add 10 items to your basket.",Toast.LENGTH_SHORT).show();
                }
                else if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(BasketActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSIONS_CODE);
                } else {

                    Intent intent = new Intent(context, QRCodeActivity.class);
                    Bundle extras = new Bundle();
                    extras.putBoolean("isLoggedIn", getIntent().getExtras().getBoolean("isLoggedIn"));
                    intent.putExtras(extras);
                    startActivityForResult(intent, 0);

                }
            }
        });


        Button checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkboxDiscount = (CheckBox)findViewById(R.id.checkbox_discount);
                CheckBox checkboxVouchers = (CheckBox)findViewById(R.id.checkbox_vouchers);
                Boolean useDiscount = checkboxDiscount.isChecked();
                Boolean useVouchers = checkboxVouchers.isChecked();

                byte[] message = buildQRMessage(basketP, useDiscount, useVouchers);
                System.out.println(new String(message));
                Intent intent = new Intent(context, CheckoutActivity.class);
                Bundle extras = new Bundle();
                extras.putBoolean("isLoggedIn", getIntent().getExtras().getBoolean("isLoggedIn"));
                extras.putByteArray("data", message);
                intent.putExtras(extras);
                startActivity(intent);

                //TODO: Atualizar preferencias (n vouchers, discount)
            }
        });

        Button clearBasketButton = findViewById(R.id.clearBasketButton);
        clearBasketButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Preferences preferences = new Preferences(context);
                basketP = new ArrayList<>();
                preferences.saveBasket(basketP);
                try {
                    setBasket();
                } catch (JSONException e) {
                    Toast.makeText(context, "An error occurred while clearing the basket, please try again.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            setBasket();
            setTotal();
            setDiscount();
            setVouchersSize();
        }
        catch (JSONException e) {
            Toast.makeText(context, "An error occurred, please try again.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Preferences preferences = new Preferences(context);
        preferences.saveBasket(basketP);
    }

    private void setBasket() throws JSONException {
        Preferences preferences = new Preferences(context);
        basketP = preferences.getBasket();
        ListView listView=(ListView)findViewById(R.id.list);
        adapter = new BasketAdapter(basketP,getApplicationContext());
        listView.setAdapter(adapter);
    }

    private void setTotal() {
        double total = 0;
        for (int i = 0; i < basketP.size(); i++){
            total = total + basketP.get(i).getPrice();
        }
        TextView textviewTotal = findViewById(R.id.textview_total);
        textviewTotal.setText(String.format ("%.2f", total)+"€");
    }

    private void setDiscount(){
        Preferences preferences = new Preferences(context);
        float discount = preferences.getDiscount();
        CheckBox checkboxDiscount = findViewById(R.id.checkbox_discount);
        checkboxDiscount.setText("Use accumulated discount of " + Float.toString(discount) + "€");
    }

    private void setVouchersSize() throws JSONException {
        Preferences preferences = new Preferences(context);
        int vouchersSize = preferences.getVouchers().size();
        CheckBox checkboxVouchers = findViewById(R.id.checkbox_vouchers);
        checkboxVouchers.setText("Use one of the " + vouchersSize + " voucher");
    }

    private byte[] buildQRMessage(ArrayList<Product> products, boolean useDiscount, boolean useVoucher) {
        Preferences preferences = new Preferences(context);
        String userUUID = preferences.getUserUUID();
        UUID uuid = UUID.fromString(userUUID);

        ArrayList<UUID> prod_uuids = new ArrayList<>();
        for(int i = 0; i < products.size(); i++) {
            prod_uuids.add(products.get(i).getUuid());
        }
        ByteBuffer bb = ByteBuffer.allocate((prod_uuids.size()*16) + 16 + 1 + 1 + 1 + 512/8);
        bb.put((byte)prod_uuids.size());
        for(int i = 0; i < prod_uuids.size(); i++) {
            System.out.println(prod_uuids.get(i));
            bb.putLong(prod_uuids.get(i).getMostSignificantBits());
            bb.putLong(prod_uuids.get(i).getLeastSignificantBits());
        }
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        bb.put((byte)(useDiscount ? 1 : 0));
        bb.put((byte)(useVoucher ? 1 : 0));
        byte[] message = bb.array();

        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry("userKey", null);
            PrivateKey pri = ((KeyStore.PrivateKeyEntry)entry).getPrivateKey();

            Signature sg = Signature.getInstance("SHA256WithRSA");
            sg.initSign(pri);
            sg.update(message, 0, (prod_uuids.size()*16) + 16 + 1 + 1 + 1);
            sg.sign(message, (prod_uuids.size()*16) + 16 + 1 + 1 + 1, 512/8);
        }
        catch (Exception e) {
            System.out.println("AN ERROR OCCURRED: " + e.getMessage());
        }
        return message;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSIONS_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(context, QRCodeActivity.class);
                    Bundle extras = new Bundle();
                    extras.putBoolean("isLoggedIn", getIntent().getExtras().getBoolean("isLoggedIn"));
                    intent.putExtras(extras);
                    startActivityForResult(intent, 0);

                } else {
                    Toast.makeText(context, "Can't access the camera. Try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {

                String result = data.getData().toString();
                Product prod = new Product(result);
                basketP.add(prod);
                Preferences preferences = new Preferences(context);
                preferences.saveBasket(basketP);
                setTotal();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Toast.makeText(context, "QRCode isn't valid.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
