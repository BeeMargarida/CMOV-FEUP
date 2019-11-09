package feup.cmov.mobile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.ArrayList;
import java.util.UUID;
import feup.cmov.mobile.common.Preferences;
import feup.cmov.mobile.common.Product;
import feup.cmov.mobile.common.Purchase;
import feup.cmov.mobile.operations.SupermarketOperation;

public class HistoryActivity extends AppCompatActivity{

    public Context context;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_history);

        //TODO: Chamada à API para obter as transferencias
        //TODO: Set history
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            setDiscount();
            setVouchersSize();
            setHistory();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setVouchersSize() throws JSONException {
        Preferences preferences = new Preferences(context);

        int vouchersSize = preferences.getVouchers().size();

        TextView vouchersSizeTextView = findViewById(R.id.textview_vouchers_size);
        vouchersSizeTextView.setText(Integer.toString(vouchersSize));
    }

    private void setDiscount(){
        Preferences preferences = new Preferences(context);
        float discount = preferences.getDiscount();
        TextView discountTextView = findViewById(R.id.textview_discount);
        discountTextView.setText(Float.toString(discount)+"€");
    }

    private void setHistory(){

        //TODO: Chamada à API para obter as transferencias

        //TODO: Delete
        Product product1 = new Product(UUID.randomUUID(),"Arroz", 12.3f);
        Product product2 = new Product(UUID.randomUUID(),"Peixe", 15.6f);
        Product product3 = new Product(UUID.randomUUID(),"Maças", 11.5f);
        Product product4 = new Product(UUID.randomUUID(),"Cereais", 12.3f);
        Product product5 = new Product(UUID.randomUUID(),"Bananas", 15.6f);
        Product product6 = new Product(UUID.randomUUID(),"Yogurtes", 11.5f);
        Product product7 = new Product(UUID.randomUUID(),"Queijo", 12.3f);
        Product product8 = new Product(UUID.randomUUID(),"Manteiga", 15.6f);
        Product product9 = new Product(UUID.randomUUID(),"Água", 11.5f);
        Product product10 = new Product(UUID.randomUUID(),"Batatas", 12.3f);
        Product product11 = new Product(UUID.randomUUID(),"Cenouras", 12.3f);
        Product product12 = new Product(UUID.randomUUID(),"Vinho", 15.6f);
        Product product13 = new Product(UUID.randomUUID(),"Bolachas", 11.5f);
        Product product14 = new Product(UUID.randomUUID(),"Pizza", 12.3f);
        Product product15 = new Product(UUID.randomUUID(),"Sopa", 15.6f);

        ArrayList <Product> purchase1v = new ArrayList<>();
        ArrayList <Product> purchase2v = new ArrayList<>();
        ArrayList <Product> purchase3v = new ArrayList<>();

        purchase1v.add(product1);
        purchase1v.add(product2);
        purchase1v.add(product3);
        purchase1v.add(product4);
        purchase1v.add(product5);
        purchase2v.add(product6);
        purchase2v.add(product7);
        purchase2v.add(product8);
        purchase2v.add(product9);
        purchase2v.add(product10);
        purchase3v.add(product11);
        purchase3v.add(product12);
        purchase3v.add(product13);
        purchase3v.add(product14);
        purchase3v.add(product15);

        Purchase purchase1 = new Purchase(UUID.randomUUID(), "12-09-2018", purchase1v, 56.33f, 12.3f);
        Purchase purchase2 = new Purchase(UUID.randomUUID(), "23-12-2019", purchase2v, 392.39f,78.8f);
        Purchase purchase3 = new Purchase(UUID.randomUUID(), "2-03-2020", purchase3v, 329.39f,329.39f);

        ArrayList <Purchase> history = new ArrayList();

        history.add(purchase1);
        history.add(purchase2);
        history.add(purchase3);

        ListView listView=(ListView)findViewById(R.id.list);
        adapter = new HistoryAdapter(history,getApplicationContext());
        listView.setAdapter(adapter);
    }
}
