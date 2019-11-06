package feup.cmov.mobile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import java.util.ArrayList;

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

        try {
            setBasket();
            setTotal();
            setDiscount();
            setVouchersSize();
        }
        catch (JSONException e) {
            Toast.makeText(context, "An error occurred, please try again.",Toast.LENGTH_SHORT).show();
        }

        //TODO: Chamada a API para atualizar preferencias (n vouchers, dicounts)

        Button addProductButton = findViewById(R.id.addProductButton);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(BasketActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSIONS_CODE);
                } else {
                    startActivityForResult(new Intent(context, QRCodeActivity.class), 0);
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


                //TODO: Criar QRCode
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSIONS_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(new Intent(context, QRCodeActivity.class), 0);
                } else {
                    Toast.makeText(context, "Can't access the camera. Try again later.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        System.out.println(requestCode);

        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {

                System.out.println(data.getData().toString());
                String result = data.getData().toString();
                Product prod = new Product(result);

                System.out.println(prod.getUuid().toString());

                basketP.add(prod);
                Preferences preferences = new Preferences(context);
                preferences.saveBasket(basketP);

                setTotal();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(context, "QRCode isn't valid.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
