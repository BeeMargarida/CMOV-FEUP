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
}
