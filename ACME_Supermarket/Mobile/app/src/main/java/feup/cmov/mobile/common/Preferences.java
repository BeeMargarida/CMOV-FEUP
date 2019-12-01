package feup.cmov.mobile.common;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import feup.cmov.mobile.R;

public class Preferences {

    private Context context;
    private SharedPreferences sharedPreferences;

    public Preferences(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.login), Context.MODE_PRIVATE);
    }

    public void savePassword(long password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(context.getResources().getString(R.string.password), password);
        editor.commit();
    }

    public long getPassword() {
        return sharedPreferences.getLong(context.getResources().getString(R.string.password), -1);
    }

    public String getSupermarketPublicKey() {
        return sharedPreferences.getString(context.getResources().getString(R.string.supermarket_public_key), "");
    }

    public void registerIn(String userUUID, String supermarketPublicKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.supermarket_public_key), supermarketPublicKey);
        editor.putString(context.getResources().getString(R.string.user_uuid), userUUID);
        editor.putBoolean(context.getResources().getString(R.string.login_status), true);
        editor.commit();
    }

    public String getUserUUID() {
        return sharedPreferences.getString(context.getResources().getString(R.string.user_uuid), "");
    }

    public boolean getRegisterStatus() {
        return !sharedPreferences.getString(context.getResources().getString(R.string.user_uuid), "").equals("");
    }

    public void saveVouchers(ArrayList<String> vouchers){
        JSONArray jsArray = new JSONArray(vouchers);
        String jsonString = jsArray.toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("vouchers" , jsonString);
        editor.commit();
    }

    public void saveVouchers(JSONArray vouchers) {
        String jsonString = vouchers.toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("vouchers" , jsonString);
        editor.commit();
    }

    public ArrayList<String> getVouchers() throws JSONException {
        String jsonString = sharedPreferences.getString("vouchers", (new JSONArray()).toString());
        JSONArray arr = new JSONArray(jsonString);
        ArrayList<String> vouchers = new ArrayList<String>();

        if (arr != null) {
            for (int i=0;i<arr.length();i++){
                vouchers.add(arr.getString(i));
            }
        }

        return vouchers;
    }

    public String getVoucher() throws JSONException {
        ArrayList<String> vouchers = getVouchers();
        if(vouchers.size() != 0) {
            JSONObject obj = new JSONObject(vouchers.get(0));
            return obj.getString("_id");
        }
        return null;
    }

    public void saveDiscount(float discount){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("discount", discount);
        editor.commit();
    }

    public float getDiscount(){
        float discount = sharedPreferences.getFloat("discount", 0);
        return discount;
    }

    public void savePurchases(JSONArray transactions) {
        String jsonString = transactions.toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("transactions" , jsonString);
        editor.commit();
    }

    public ArrayList<Purchase> getPurchases() throws JSONException, ParseException {
        String jsonString = sharedPreferences.getString("transactions", (new JSONArray()).toString());
        JSONArray arr = new JSONArray(jsonString);
        ArrayList<Purchase> transactions = new ArrayList<>();

        if (arr != null) {
            for (int i = 0;i < arr.length(); i++){
                JSONObject obj = new JSONObject(arr.getString(i));
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",  Locale.ENGLISH);
                UUID uuid = UUID.fromString(obj.getString("_id"));
                System.out.println(obj.getString("createdAt"));
                Date date = format.parse(obj.getString("createdAt"));
                float totalPrice = (float) obj.getDouble("total_price");
                float paidPrice = (float) obj.getDouble("paid_price");

                ArrayList<Product> products = new ArrayList<>();
                JSONArray prods = obj.getJSONArray("products");
                for (int j = 0; j < prods.length(); j++) {
                    Product product = getProduct(prods.getJSONObject(j));
                    products.add(product);
                }
                transactions.add(new Purchase(uuid, products, date, totalPrice, paidPrice));
            }
        }

        return transactions;
    }

    public Product getProduct(JSONObject obj) throws JSONException{
        UUID uuid = UUID.fromString(obj.getString("_id"));
        String name = obj.getString("name");
        float price = (float) obj.getDouble("price");
        return new Product(uuid, name, price);
    }

    public void saveBasket(ArrayList<Product> basket){
        ArrayList<String> basketString = new ArrayList<>();
        for(int i = 0; i < basket.size(); i++){
            basketString.add(basket.get(i).toString());
        }
        JSONArray jsArray = new JSONArray(basketString);
        String jsonString = jsArray.toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("basket" , jsonString);
        editor.commit();
    }

    public ArrayList<Product> getBasket() throws JSONException {
        String jsonString = sharedPreferences.getString("basket", (new JSONArray()).toString());
        JSONArray arr = new JSONArray(jsonString);
        ArrayList<String> basketString = new ArrayList<>();

        if (arr != null) {
            for (int i=0;i<arr.length();i++){
                basketString.add(arr.getString(i));
            }
        }

        ArrayList<Product> basket = new ArrayList<Product>();
        for(int i = 0; i < basketString.size(); i++){
            basket.add(new Product(basketString.get(i)));
        }

        return basket;
    }
}
