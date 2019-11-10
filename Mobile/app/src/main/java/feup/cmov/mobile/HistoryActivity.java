package feup.cmov.mobile;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import feup.cmov.mobile.common.Preferences;
import feup.cmov.mobile.common.Product;
import feup.cmov.mobile.common.Purchase;

public class HistoryActivity extends AppCompatActivity{

    public Context context;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_history);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            setHistory();
            setDiscount();
            setVouchersSize();
        } catch (Exception e) {
            Toast.makeText(context, "There was a problem getting info, please try again.", Toast.LENGTH_LONG).show();
        }
    }

    private void setHistory() throws JSONException, ParseException {
        Preferences preferences = new Preferences(context);
        ArrayList<Purchase> purchases = preferences.getPurchases();

        ListView listView=(ListView)findViewById(R.id.list);
        adapter = new HistoryAdapter(purchases,getApplicationContext());
        listView.setAdapter(adapter);

        //TODO: Delete
        /*Product product1 = new Product(UUID.randomUUID(),"Arroz", 12.3f);
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

        Purchase purchase1 = new Purchase(UUID.randomUUID(), purchase1v, new Date (2019,12,31),56.33f, 12.3f);
        Purchase purchase2 = new Purchase(UUID.randomUUID(), purchase2v, new Date (2019,12,31),392.39f,78.8f);
        Purchase purchase3 = new Purchase(UUID.randomUUID(), purchase3v, new Date (2019,12,31),329.39f,329.39f);

        ArrayList <Purchase> history = new ArrayList();

        history.add(purchase1);
        history.add(purchase2);
        history.add(purchase3);

        ListView listView=(ListView)findViewById(R.id.list);
        adapter = new HistoryAdapter(history,getApplicationContext());
        listView.setAdapter(adapter);*/
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

}
