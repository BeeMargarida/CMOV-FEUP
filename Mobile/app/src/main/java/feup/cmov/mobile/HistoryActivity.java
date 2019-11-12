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
        discountTextView.setText(String.format ("%.2f", discount)+"€");
    }

}
