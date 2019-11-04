package feup.cmov.mobile.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;

import feup.cmov.mobile.R;

public class Preferences {

    private Context context;
    private SharedPreferences sharedPreferences;

    public Preferences(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.login), Context.MODE_PRIVATE);
    }

    public void saveKey(PrivateKey key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.private_key), Base64.encodeToString(key.getEncoded(), Base64.DEFAULT));
        editor.commit();
    }

    public void savePassword(long password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(context.getResources().getString(R.string.password), password);
        editor.commit();
    }

    public long getPassword() {
        return sharedPreferences.getLong(context.getResources().getString(R.string.password), -1);
    }

    public void registerIn(String userUUID, String supermarketPublicKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.supermarket_public_key), supermarketPublicKey);
        editor.putString(context.getResources().getString(R.string.user_uuid), userUUID);
        editor.putBoolean(context.getResources().getString(R.string.login_status), true);
        editor.commit();
    }

    public void changeLogStatus(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getResources().getString(R.string.login_status), value);
        editor.commit();
    }

    public boolean getRegisterStatus() {
        return !sharedPreferences.getString(context.getResources().getString(R.string.user_uuid), "").equals("");
    }

    public boolean getLoginStatus() {
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.login_status), false);
    }

    public void saveVouchers(ArrayList<String> vouchers){
        JSONArray jsArray = new JSONArray(vouchers);
        String jsonString = jsArray.toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("vouchers" , jsonString);
        editor.commit();
    }

    /*public ArrayList<String> getVouchers() throws JSONException {
        String jsonString = sharedPreferences.getString("vouchers", (new JSONArray()).toString());
        JSONArray arr = new JSONArray(jsonString);
        ArrayList<String> vouchers = new ArrayList<String>();
        for(int i = 0; i < arr.length(); i++){
            Log.d("TEST", arr.getJSONObject(i).getString("name"));
            //vouchers.add(arr.getJSONObject(i).getString("name"));
        }

        return vouchers;
    }*/

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

    public void saveDiscount(float discount){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("discount", discount);
        editor.commit();
    }

    public float getDiscount(){
        float discount = sharedPreferences.getFloat("discount", 0);
        return discount;
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
            basket.add(getProductFromString(basketString.get(i)));
        }

        return basket;
    }

    public Product getProductFromString(String productString){
        String[] productArr = productString.split("-");

        String name= productArr[0];
        float price = Float.parseFloat(productArr[1]);

        Product product = new Product(name, price);

        return product;
    }
}
