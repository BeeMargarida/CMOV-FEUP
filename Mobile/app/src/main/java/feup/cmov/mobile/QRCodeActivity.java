package feup.cmov.mobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import feup.cmov.mobile.common.Preferences;
import feup.cmov.mobile.common.Product;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScannerView = new ZXingScannerView(QRCodeActivity.this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(QRCodeActivity.this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mScannerView != null) {
            mScannerView.stopCamera();
        }
    }

    @Override
    public void handleResult(Result result) {

        try {
            Intent data = new Intent();

            // Get supermarket public key
            Preferences preferences = new Preferences(this);
            String supermarketPublicKey = preferences.getSupermarketPublicKey();

            supermarketPublicKey = supermarketPublicKey.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");

            byte[] dataBytes = Base64.decode(supermarketPublicKey.getBytes(), 0);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(dataBytes);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey pub = fact.generatePublic(spec);

            // Convert QRCode result from Hex to ByteArrays
            byte[] msgBytes = hexStringToByteArray(result.getText());

            // Decrypt QRCode result
            byte[] clearTag;
            Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, pub);
            clearTag = cipher.doFinal(msgBytes);

            // Get info of products
            ByteBuffer tag = ByteBuffer.wrap(clearTag);
            int tId = tag.getInt();
            long most = tag.getLong();
            long less = tag.getLong();
            UUID id = new UUID(most, less);
            int euros = tag.getInt();
            int cents = tag.getInt();
            byte l = tag.get();
            byte[] bName = new byte[l];
            tag.get(bName);
            String name = new String(bName, StandardCharsets.ISO_8859_1);

            System.out.println(id.toString());
            Product prod = new Product(id, name, (float)(euros + cents*0.01));
            data.setData(Uri.parse(prod.toString()));
            setResult(RESULT_OK, data);

        }
        catch (Exception e) {
            setResult(RESULT_CANCELED, null);
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
