package feup.cmov.mobile.operations;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.security.Signature;

import feup.cmov.mobile.common.Utils;

public class SupermarketOperation implements Runnable, Utils {

    public Supermarket supermarket;
    public byte[] userUUID;
    public PrivateKey userKey;

    public interface Supermarket {
        void done(boolean success, int requestCode, JSONObject response, String error);
    }

    public SupermarketOperation(Context context, byte[] userUUID, PrivateKey userKey) {

        this.userUUID = userUUID;
        this.userKey = userKey;

        try {
            Activity activity = (Activity)context;
            supermarket = (SupermarketOperation.Supermarket) activity;

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        try {
            ByteBuffer bb = ByteBuffer.allocate(16 + 512/8);
            bb.put(userUUID);
            byte[] request = bb.array();

            Signature sg = Signature.getInstance("SHA256WithRSA");
            sg.initSign(userKey);
            sg.update(request, 0, 16);
            sg.sign(request, 16, 512/8);

            RequestQueue queue = Volley.newRequestQueue((Context) supermarket);
            String url = URL + "/supermarket/vouchers" + "?user_id=\"" + new String(Base64.encode(request, Base64.URL_SAFE)).replaceAll("\n", "") + "\"";

            JsonObjectRequest jsonObjectRequestVoucher = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            supermarket.done(true, 0, response, "");
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            supermarket.done(false, 0,null, "There was a problem getting your vouchers, please try again.");
                        }
                    });

            // Access the RequestQueue through your singleton class.
            queue.add(jsonObjectRequestVoucher);

            url = URL + "/supermarket/discount" + "?user_id=\"" + new String(Base64.encode(request, Base64.URL_SAFE)).replaceAll("\n", "") + "\"";
            JsonObjectRequest jsonObjectRequestDiscount = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            supermarket.done(true, 1, response, "");
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            supermarket.done(false, 1, null, "There was a problem getting your vouchers, please try again.");
                        }
                    });

            // Access the RequestQueue through your singleton class.
            queue.add(jsonObjectRequestDiscount);

            url = URL + "/supermarket/list" + "?user_id=\"" + new String(Base64.encode(request, Base64.URL_SAFE)).replaceAll("\n", "") + "\"";
            JsonObjectRequest jsonObjectRequestTransactions = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            supermarket.done(true, 2, response, "");
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            supermarket.done(false, 2, null, "There was a problem getting your vouchers, please try again.");
                        }
                    });

            // Access the RequestQueue through your singleton class.
            queue.add(jsonObjectRequestTransactions);

        }
        catch (Exception e) {
            System.out.println("AN ERROR OCCURRED: " + e.getMessage());
        }




    }
}
