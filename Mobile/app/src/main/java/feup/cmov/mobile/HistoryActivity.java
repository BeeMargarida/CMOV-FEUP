package feup.cmov.mobile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.util.ArrayList;

import feup.cmov.mobile.common.Preferences;

public class HistoryActivity extends AppCompatActivity {

    public Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_history);

        //TODO: Chamada a API para atualizar preferencias (n vouchers, dicounts)

        setDiscount();
        try {
            setVouchersSize();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //TODO: Chamada à API para obter as transferencias
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
