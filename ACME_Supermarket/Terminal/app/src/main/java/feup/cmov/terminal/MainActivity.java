package feup.cmov.terminal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public final int CAMERA_PERMISSIONS_CODE = 0;
    Context context;
    String errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        Button checkoutButton = findViewById(R.id.checkout_btn);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSIONS_CODE);
                } else {

                    Intent intent = new Intent(context, QRCodeActivity.class);
                    startActivityForResult(intent, 0);

                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(errorMessage != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.errorMessage = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSIONS_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(context, QRCodeActivity.class);
                    startActivityForResult(intent, 0);

                } else {
                    Toast.makeText(context, "Can't access the camera. Try again later.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if(bundle != null && bundle.getString("error") != null) {
                errorMessage = bundle.getString("error");
            }
        }
    }
}
