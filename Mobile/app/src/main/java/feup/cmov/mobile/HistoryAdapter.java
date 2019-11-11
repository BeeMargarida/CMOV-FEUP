package feup.cmov.mobile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import feup.cmov.mobile.common.Preferences;
import feup.cmov.mobile.common.Product;
import feup.cmov.mobile.common.Purchase;

public class HistoryAdapter extends ArrayAdapter<Purchase> implements View.OnClickListener{

    private ArrayList<Purchase> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        LinearLayout item;
        TextView purchaseID;
        TextView purchaseDate;
        LinearLayout purchaseProducts;
        TextView totalPrice;
        TextView paidPrice;
    }

    public HistoryAdapter(ArrayList<Purchase> data, Context context) {
        super(context, R.layout.history_purchase, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {


    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Purchase purchase = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.history_purchase, parent, false);
            viewHolder.purchaseID = (TextView) convertView.findViewById(R.id.purchase_id);
            viewHolder.purchaseDate = (TextView) convertView.findViewById(R.id.purchase_date);
            viewHolder.totalPrice = (TextView) convertView.findViewById(R.id.purchase_price);
            viewHolder.paidPrice = (TextView) convertView.findViewById(R.id.purchase_paid_price);
            viewHolder.purchaseProducts = (LinearLayout)  convertView.findViewById(R.id.purchase_prdsucts);
            viewHolder.item = (LinearLayout) convertView.findViewById(R.id.item);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.purchaseID.setText(purchase.getUuid().toString().substring(0, 6));
        viewHolder.purchaseDate.setText(purchase.getDateString());
        viewHolder.totalPrice.setText(Float.toString(purchase.getTotalPrice()) +"€");
        viewHolder.paidPrice.setText(Float.toString(purchase.getPaidPrice()) +"€");
        viewHolder.purchaseProducts.removeAllViews();

        for(int i = 0; i < purchase.getProducts().size(); i++){
            TextView textView = new TextView(this.mContext);
            textView.setText(purchase.getProducts().get(i).getName() + " - " + purchase.getProducts().get(i).getPrice()+"€");
            textView.setTextSize((float) 17);
            textView.setTextColor(Color.parseColor("#003845"));
            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "raleway.ttf");
            textView.setTypeface(font);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,0,10);
            textView.setLayoutParams(params);

            viewHolder.purchaseProducts.addView(textView);
        }

        //TODO: EXTRA FEATURE
        viewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout itemView = (LinearLayout) v;
                TextView objectID = (TextView) itemView.findViewById(R.id.purchase_id);

                Toast.makeText(mContext, "ID: " + objectID.getText(), Toast.LENGTH_SHORT).show();

                Preferences preferences = new Preferences(mContext);
                ArrayList<Purchase> history = new ArrayList<>();
                try {
                    history = preferences.getPurchases();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                ArrayList<Product> newProducts = new ArrayList<>();
                for(int i=0; i<history.size(); i++){
                    String id = history.get(i).getUuid().toString().substring(0, 6);
                    if(objectID.getText().equals(id)){
                        newProducts = history.get(i).getProducts();
                        break;
                    }
                }

                try {
                    ArrayList<Product> basketP = null;
                    basketP = preferences.getBasket();

                    if (basketP.size() + newProducts.size() <= 10) {
                        for (int i = 0; i < newProducts.size(); i++) {
                            basketP.add(newProducts.get(i));
                        }

                        preferences.saveBasket(basketP);
                        Toast.makeText(mContext, "Products added to your basket with success.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(mContext, "You can only have 10 items in basket.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(mContext, "There was a problem adding products to basket, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return convertView;
    }

}