package feup.cmov.mobile.operations;

import android.app.Activity;
import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterOperation implements Runnable {

    String name, username, email, cardName, cardMonth, cardYear, userPublicKey;
    int cardNumber, cardCvc;
    public Register register;

    public interface Register {
        void done(String response);
    }

    public RegisterOperation(Context context, String name, String username, String email,
                             String cardName, int cardNumber, String cardMonth,
                             String cardYear, int cardCvc, String userPublicKey) {

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

        URL url;
        HttpURLConnection connection = null;

        try {

            url = new URL("http://localhost:8081/auth/signup");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");

            JSONObject auth = new JSONObject();
            auth.put("name", this.name);
            auth.put("username", this.username);
            auth.put("email", this.email);
            auth.put("cardName", this.cardName);
            auth.put("cardNumber", this.cardNumber);
            auth.put("cardMonth", this.cardMonth);
            auth.put("cardYear", this.cardYear);
            auth.put("cardCvc", this.cardCvc);
            auth.put("userPublicKey", this.userPublicKey);

            OutputStreamWriter writer= new OutputStreamWriter(connection.getOutputStream());
            writer.write(auth.toString());
            writer.flush();
            writer.close();

            String response = "";
            int responseCode = connection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }

                register.done(response);
            }

        }
        catch(Exception e) {
            register.done("");
        }
        finally {
            if(connection != null) {
                connection.disconnect();
            }
        }

    }
}
