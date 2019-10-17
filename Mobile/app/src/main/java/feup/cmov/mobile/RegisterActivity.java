package feup.cmov.mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText name, username, email, password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        name = (EditText)findViewById(R.id.input_name);
        username = (EditText)findViewById(R.id.input_username);
        email = (EditText)findViewById(R.id.input_email);
        password = (EditText)findViewById(R.id.input_password);

        Button registerButton = (Button) findViewById(R.id.register_btn);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameString = name.getText().toString();
                String usernameString = username.getText().toString();
                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();

                //TODO: Send request to sign in

                //TODO: Receive UUID _id, username, name and Supermarket Public Key

                //TODO: Store received info
            }
        });

    }


}
