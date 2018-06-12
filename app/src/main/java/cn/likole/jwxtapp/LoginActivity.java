package cn.likole.jwxtapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import static cn.likole.jwxtapp.JwxtTool.jwxt;

/**
 * Created by likole on 6/3/18.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText et_username;
    private EditText et_password;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindView();
        readData();
    }

    private void bindView() {
        et_username = (EditText) findViewById(R.id.et_username);
        et_password =(EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                login();
            }
        });
    }

    private void readData() {
        SharedPreferences read = getSharedPreferences("JwxtAPP", MODE_PRIVATE);
        et_username.setText(read.getString("et_username", ""));
        et_password.setText(read.getString("et_password", ""));
    }

    private void saveData() {
        SharedPreferences.Editor editor = getSharedPreferences("JwxtAPP",
                MODE_PRIVATE).edit();
        editor.putString("et_username", et_username.getText().toString());
        editor.putString("et_password", et_password.getText().toString());
        editor.commit();
    }

    private void login() {
        final String username = et_username.getText().toString();
        final String password = et_password.getText().toString();
        final String captcha = "error";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (jwxt.login(username, password, captcha)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
