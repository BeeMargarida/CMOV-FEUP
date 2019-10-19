package feup.cmov.mobile.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.security.PrivateKey;

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

    public void logIn(String userUUID, String supermarketPublicKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.supermarket_public_key), supermarketPublicKey);
        editor.putString(context.getResources().getString(R.string.user_uuid), userUUID);
        editor.commit();
    }

}
