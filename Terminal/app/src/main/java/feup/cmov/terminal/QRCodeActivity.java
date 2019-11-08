package feup.cmov.terminal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;


import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            byte[] msgBytes = hexStringToByteArray(result.getText());

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://319c5d33.ngrok.io/supermarket/checkout";
            JSONObject checkout = new JSONObject();
            checkout.put("basket", msgBytes);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, checkout, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("CHECKOUT", "Checkout successful");

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("CHECKOUT", "Checkout not successful");
                        }
                    });

            // Access the RequestQueue through your singleton class.
            queue.add(jsonObjectRequest);

        } catch(JSONException e) {
            Toast.makeText(this, "There was a problem creating the request, please try again.", Toast.LENGTH_SHORT).show();
        }


        finish();
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
