package feup.cmov.mobile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Arrays;
import java.util.Hashtable;

public class CheckoutActivity extends AppCompatActivity {

    private final String TAG = "QR_Code";
    private final String ISO_SET = "ISO-8859-1";

    Context context;
    ImageView qrCodeImageview;
    String qr_content = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        qrCodeImageview = findViewById(R.id.img_qr_code_image);
        context = this;
        System.out.println(getIntent().getExtras().getBoolean("isLoggedIn"));

        Button goBack = findViewById(R.id.back_basket);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button checkoutDone = findViewById(R.id.checkout_done);
        checkoutDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                Bundle extras = new Bundle();
                System.out.println(getIntent().getExtras().getBoolean("isLoggedIn"));
                extras.putBoolean("isLoggedIn", getIntent().getExtras().getBoolean("isLoggedIn"));
                data.putExtras(extras);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        byte[] content = getIntent().getByteArrayExtra("data");
        System.out.println(Arrays.toString(content));
        int sign_size = 512/8;
        int mess_size = content.length - sign_size;

        try {
            qr_content = new String(content, ISO_SET);
            ByteBuffer bb = ByteBuffer.wrap(content);
            byte[] mess = new byte[mess_size];
            byte[] sign = new byte[sign_size];
            bb.get(mess, 0, mess_size);
            bb.get(sign, 0, sign_size);
            boolean verified = validate(mess, sign);
            if(verified) {
                Toast.makeText(this, "The QRCode is valid", Toast.LENGTH_SHORT).show();
            }
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
        }

        Thread t = new Thread(  new Runnable() {  // do the creation in a new thread to avoid ANR Exception
            public void run() {
                final Bitmap bitmap;
                try {
                    bitmap = encodeAsBitmap(qr_content);
                    runOnUiThread(new Runnable() {  // runOnUiThread method used to do UI task in main thread.
                        @Override
                        public void run() {
                            qrCodeImageview.setImageBitmap(bitmap);
                        }
                    });
                } catch (WriterException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        });
        t.start();
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        int DIMENSION = 600;
        BitMatrix result;

        Hashtable<EncodeHintType, String> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, ISO_SET);
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, DIMENSION, DIMENSION, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? getResources().getColor(R.color.colorPrimary) : Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    boolean validate(byte[] message, byte[] signature) {
        boolean verified = false;
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry("userKey", null);
            PublicKey pub = ((KeyStore.PrivateKeyEntry)entry).getCertificate().getPublicKey();
            Signature sg = Signature.getInstance("SHA256WithRSA");
            sg.initVerify(pub);
            sg.update(message);
            verified = sg.verify(signature);
        }
        catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
        return verified;
    }

}
