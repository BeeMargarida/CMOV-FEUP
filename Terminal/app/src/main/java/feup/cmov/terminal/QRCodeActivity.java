package feup.cmov.terminal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView = new ZXingScannerView(QRCodeActivity.this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(QRCodeActivity.this);
        mScannerView.startCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mScannerView != null) {
            mScannerView.setResultHandler(null);
            mScannerView.stopCameraPreview();
            mScannerView.stopCamera();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mScannerView != null) {
            mScannerView.setResultHandler(null);
            mScannerView.stopCameraPreview();
            mScannerView.stopCamera();
        }
    }

    @Override
    public void handleResult(Result result) {

        Intent data = new Intent();
        try {
            String msg = result.getText();
            byte[] msgByte = msg.getBytes("ISO-8859-1");

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://6168e437.ngrok.io/supermarket/checkout";
            JSONObject checkout = new JSONObject();
            checkout.put("basket", msg);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, checkout, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("CHECKOUT", "Checkout successful");
                            try {

                                Intent intent = new Intent(context, ResultActivity.class);
                                Bundle extras = new Bundle();
                                System.out.println(response.getDouble("totalPrice"));
                                extras.putDouble("totalPrice", response.getDouble("totalPrice"));
                                intent.putExtras(extras);
                                startActivity(intent);

                            } catch(JSONException e) {
                                Toast.makeText(context, "There was an error, please try again later.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("CHECKOUT", "Checkout not successful");
                            finish();
                        }
                    });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Access the RequestQueue through your singleton class.
            queue.add(jsonObjectRequest);

        } catch(JSONException e) {
            Toast.makeText(this, "There was a problem creating the request, please try again.", Toast.LENGTH_SHORT).show();
        } catch (UnsupportedEncodingException e){
            Toast.makeText(this, "There was a problem creating the request, please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
