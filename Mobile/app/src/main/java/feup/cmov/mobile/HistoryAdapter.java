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
import java.util.ArrayList;

import feup.cmov.mobile.common.Purchase;

public class HistoryAdapter extends ArrayAdapter<Purchase> implements View.OnClickListener{

    private ArrayList<Purchase> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
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
        //TODO: DELETE PRODUCT
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

        return convertView;
    }

}