package feup.cmov.mobile;

import android.content.Context;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.util.ArrayList;

import feup.cmov.mobile.common.Preferences;
import feup.cmov.mobile.common.Product;

public class BasketActivity extends AppCompatActivity {

    public Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_basket);

        //TODO: Chamada a API para atualizar preferencias (n vouchers, dicounts)

        try {
            setBasket();
            setVouchersSize();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setDiscount();


        Button addProductButton = findViewById(R.id.addProductButton);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Ler QRCode
                //TODO: Mostrar info do produto
                //TODO: Adicionar à lista de produtos
                //TODO: Atualizar soma total
            }
        });


        Button checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Verificar se quer discount
                //TODO: Verificar se quer voucher
                //TODO: Criar QRCode
                //TODO: Atualizar preferencias (n vouchers, discount)
            }
        });
    }


    //TODO: Quando sair da atividade, atualizar basket nas preferencias

    private void setBasket() throws JSONException {
        Preferences preferences = new Preferences(context);
        ArrayList<Product> basketP = preferences.getBasket();
        ListView listView=(ListView)findViewById(R.id.list);
        BasketAdapter adapter = new BasketAdapter(basketP,getApplicationContext());
        listView.setAdapter(adapter);
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

}
