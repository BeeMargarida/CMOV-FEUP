package feup.cmov.mobile.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.security.auth.x500.X500Principal;

import feup.cmov.mobile.MainActivity;
import feup.cmov.mobile.R;
import feup.cmov.mobile.common.Preferences;
import feup.cmov.mobile.common.PubKey;
import feup.cmov.mobile.operations.RegisterOperation;

public class RegisterActivity extends AppCompatActivity implements RegisterOperation.Register {

    private EditText name, username, email, password, cardName, cardNumber, cardCvc;
    private Spinner cardMonth, cardYear;
    private Context context;
    private Long passwordToSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_register);

        ArrayAdapter<CharSequence> adapterMonth = ArrayAdapter.createFromResource(this, R.array.months, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterYear = ArrayAdapter.createFromResource(this,
                R.array.years, android.R.layout.simple_spinner_item);

        name = (EditText) findViewById(R.id.input_name);
        username = (EditText) findViewById(R.id.input_username);
        email = (EditText) findViewById(R.id.input_email);
        password = (EditText) findViewById(R.id.input_password);
        cardName = (EditText) findViewById(R.id.card_name);
        cardNumber = (EditText) findViewById(R.id.card_number);
        cardCvc = (EditText) findViewById(R.id.card_cvc);
        cardMonth = (Spinner) findViewById(R.id.card_month);
        cardYear = (Spinner) findViewById(R.id.card_year);

        cardMonth.setAdapter(adapterMonth);
        cardYear.setAdapter(adapterYear);


        Button registerButton = (Button) findViewById(R.id.register_btn);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    String nameString = name.getText().toString();
                    String usernameString = username.getText().toString();
                    String emailString = email.getText().toString();
                    String passwordString = password.getText().toString();
                    String cardNameString = cardName.getText().toString();
                    String cardNumberString = cardNumber.getText().toString();
                    String cardCvcString = cardCvc.getText().toString();
                    String cardMonthValue = cardMonth.getSelectedItem().toString();
                    String cardYearValue = cardYear.getSelectedItem().toString();


                    if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(usernameString) || TextUtils.isEmpty(emailString)
                            || TextUtils.isEmpty(passwordString) || TextUtils.isEmpty(cardNameString)
                            || TextUtils.isEmpty(cardNumberString) || TextUtils.isEmpty(cardCvcString)
                            || TextUtils.isEmpty(cardMonthValue) || TextUtils.isEmpty(cardYearValue)) {

                        throw new Exception("Please complete all fields.");

                    } else {

                        int cardCvcValue = Integer.parseInt(cardCvcString);
                        int cardYearNumber = Integer.parseInt(cardYearValue);
                        passwordToSave = Long.parseLong(passwordString);
                        int cardMonthNumber = Arrays.asList(getResources().getStringArray(R.array.months)).indexOf(cardMonthValue) + 1;

                        if(cardMonthNumber == 0) {
                            throw new Exception("An error happened, please try again.");
                        }

                        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
                        if(cardYearNumber < currentYear || (cardYearNumber == currentYear && cardMonthNumber < currentMonth)) {
                            throw new Exception("Payment Card Expired, please insert other.");
                        }

                        // generate private/public key
                        generateAndStoreKeys();

                        // make request to server
                        RegisterOperation registerOperation = new RegisterOperation(context,
                                nameString, usernameString, emailString, cardNameString, cardNumberString,
                                cardMonthNumber, cardYearNumber, cardCvcValue, Base64.encodeToString(getPubKey().publicKey.getEncoded(), Base64.DEFAULT));

                        Thread thread = new Thread(registerOperation);
                        thread.start();

                    }

                }
                catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error generating private/public keys, please try again.", Toast.LENGTH_LONG).show();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void generateAndStoreKeys(){
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry("key1", null);
            //PublicKey pub = ks.getCertificate("key1").getPublicKey();
            if (entry == null) {
                Calendar start = new GregorianCalendar();
                Calendar end = new GregorianCalendar();
                end.add(Calendar.YEAR, 20);
                KeyPairGenerator kgen = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                AlgorithmParameterSpec spec = new KeyPairGeneratorSpec.Builder(this)
                        .setKeySize(512)
                        .setAlias("key1")
                        .setSubject(new X500Principal("CN=" + "key1"))   // Usually the full name of the owner (person or organization)
                        .setSerialNumber(BigInteger.valueOf(12121212))
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                kgen.initialize(spec);
                kgen.generateKeyPair();
            }
        }
        catch (Exception ex) {
            Toast.makeText(context, "Problem generating the user keys.", Toast.LENGTH_LONG).show();
        }
    }

    PubKey getPubKey() {
        PubKey pkey = new PubKey();
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            PublicKey pub;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pub = ks.getCertificate("key1").getPublicKey();
            } else {
                KeyStore.Entry entry = ks.getEntry("key1", null);
                pub = ((KeyStore.PrivateKeyEntry)entry).getCertificate().getPublicKey();
            }

            pkey.publicKey = pub;
            pkey.modulus = ((RSAPublicKey)pub).getModulus().toByteArray();
            pkey.exponent = ((RSAPublicKey)pub).getPublicExponent().toByteArray();
        }
        catch (Exception e) {
            Log.d("DEBUG", e.getMessage());
        }
        return pkey;
    }

    @Override
    public void done(boolean success, JSONObject response, String error) {
        if(!success) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
        }
        else {
            try {
                Log.d("REGISTRATION", "Finishing Registration process");

                // store user uuid and supermarket public key
                Preferences preferences = new Preferences(context);
                preferences.registerIn(response.getString("_id"), response.getString("supermarket_public_key"));
                preferences.savePassword(passwordToSave);

                Log.d("REGISTRATION", "Saved user preferences");

                // redirect to main page
                startActivity(new Intent(context, MainActivity.class));
                finish();

            } catch (JSONException e) {
                System.out.println(e.getMessage());
                Toast.makeText(context, "Something went wrong, please try registering again", Toast.LENGTH_LONG).show();
            }
        }
    }

}
