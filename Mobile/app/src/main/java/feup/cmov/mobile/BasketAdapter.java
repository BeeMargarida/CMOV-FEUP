package feup.cmov.mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import feup.cmov.mobile.common.Product;

public class BasketAdapter extends ArrayAdapter<Product> implements View.OnClickListener{

    private ArrayList<Product> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView productName;
        TextView productPrice;
    }

    public BasketAdapter(ArrayList<Product> data, Context context) {
        super(context, R.layout.basket_product, data);
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
        Product product = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.basket_product, parent, false);
            viewHolder.productName = (TextView) convertView.findViewById(R.id.product_name);
            viewHolder.productPrice = (TextView) convertView.findViewById(R.id.product_price);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        /*Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;*/

        viewHolder.productName.setText(product.getName());
        viewHolder.productPrice.setText(String.format ("%.2f", product.getPrice()) +"€");

        // Return the completed view to render on screen
        return convertView;
    }

    public void insertProduct(ArrayList<Product> products) {
        this.clear();
        this.addAll(products);
        this.notifyDataSetChanged();
    }

}