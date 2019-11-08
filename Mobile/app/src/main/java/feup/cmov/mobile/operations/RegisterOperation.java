package feup.cmov.mobile.operations;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class RegisterOperation implements Runnable {

    String name, username, email, cardName, cardNumber, userPublicKey;
    int cardCvc, cardMonth, cardYear;
    public Register register;

    public interface Register {
        void done(boolean success, JSONObject response, String error);
    }

    public RegisterOperation(Context context, String name, String username, String email,
                             String cardName, String cardNumber, int cardMonth,
                             int cardYear, int cardCvc, String userPublicKey) {

        this.name = name;
        this.username = username;
        this.email = email;
        this.cardName = cardName;
        this.cardNumber = cardNumber;
        this.cardMonth = cardMonth;
        this.cardYear = cardYear;
        this.cardCvc = cardCvc;
        this.userPublicKey = userPublicKey;

        try {
            Activity activity = (Activity)context;
            register = (Register) activity;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            RequestQueue queue = Volley.newRequestQueue((Context) register);
            String url = "http://319c5d33.ngrok.io/auth/signup";
            JSONObject auth = new JSONObject();
            auth.put("name", this.name);
            auth.put("username", this.username);
            auth.put("email", this.email);
            auth.put("card_name", this.cardName);
            auth.put("card_number", this.cardNumber);
            auth.put("card_month", this.cardMonth);
            auth.put("card_year", this.cardYear);
            auth.put("card_cvc", this.cardCvc);
            auth.put("user_public_key", this.userPublicKey);


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, auth, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("REGISTER", "Registration successful");
                            register.done(true, response, "");

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("REGISTER", "Registration not successful");

                            try {
                                if(error.networkResponse != null) {
                                    String errorMessage = (new JSONObject(new String(error.networkResponse.data))).getJSONObject("error").getString("message");
                                    register.done(false, null, errorMessage);
                                } else {
                                    register.done(false, null, "Error in the registration process, please try again.");
                                }
                            }
                            catch (JSONException e) {
                                register.done(false, null, "Error in the registration process, please try again.");
                            }
                        }
                    });

            // Access the RequestQueue through your singleton class.
            queue.add(jsonObjectRequest);
        }
        catch (JSONException e) {
            register.done(false, null, "Error in the registration process, please try again.");
        }


    }
}
