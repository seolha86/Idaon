package com.example.idaon;

import static com.android.volley.VolleyLog.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import android.content.SharedPreferences;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity {
    private EditText username, password;
    private Button mbtn, rbtn, logout_kakao;

    ProgressBar progressBar;
    static RequestQueue requestQueue;

    private SharedPreferences appData;
    private boolean saveLoginData;
    private CheckBox checkBox;
    private String userID, userPW;

    //kakao 로그인
    private ImageView login_kakao;
    private SessoinCallback sessoinCallback = new SessoinCallback();
    Session session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        rbtn = findViewById(R.id.btn2); //회원가입 버튼
        mbtn = findViewById(R.id.write); //로그인 버튼
        login_kakao = findViewById(R.id.btn_KakaoLogin); //카카오 로그인
//        logout_kakao = findViewById(R.id.btn_KakaoLogout); //카카오 로그아웃
        progressBar = findViewById(R.id.loading);
        checkBox = findViewById(R.id.checkBox);

        if(saveLoginData) {
            username.setText(userID);
            password.setText(userPW);
            checkBox.setChecked(saveLoginData);
        }

        if (SaveSharedPreference.getSaveLoginData(this, "Save_login")) {
            username.setText(SaveSharedPreference.getUserID(this));
            password.setText(SaveSharedPreference.getUserPW(this));

            Map<String, String> loginInfo = SaveSharedPreference.getLogin(this);

            if (!loginInfo.isEmpty()) {
                String userID = loginInfo.get("userID");
                String userPW = loginInfo.get("userPW");
            }
        } else {
            SaveSharedPreference.clearShared(this);
        }

        if (SaveSharedPreference.getSaveLoginData(this, "not_save")) {
            username.setText("");
            password.setText("");
        }

        session = Session.getCurrentSession();
        session.addCallback((ISessionCallback) sessoinCallback);

        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        // 아이디 비밀번호 찾기
        findViewById(R.id.findId).setOnClickListener(view -> {
            startActivity(new Intent(this, FindId.class));
        });

        // 회원가입 버튼
        rbtn.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // 로그인 버튼
        mbtn.setOnClickListener(view -> {
            String userID = username.getText().toString();
            String userPW = password.getText().toString();
            SaveSharedPreference.setLogin(this, userID, userPW);
            save();

            Response.Listener<String> responseListener = response -> {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success =jsonResponse.getBoolean("success");
                    if(success){
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        String userID1 = jsonResponse.optString("userID");
                        String userPW1 = jsonResponse.optString("userPW");
                        Toast.makeText(getApplicationContext(), SaveSharedPreference.getUserID(this) + "님 환영합니다!", Toast.LENGTH_SHORT).show();
                        intent.putExtra("userID", userID1);
                        intent.putExtra("userPW", userPW1);

                        if (checkBox.isChecked()) {
                            SaveSharedPreference.setSaveLoginData(this, "Save_login", checkBox.isChecked());

                            if(saveLoginData) {
                                username.setText(userID);
                                password.setText(userPW);
                                checkBox.setChecked(saveLoginData);
                            }
                        } else {
                            SaveSharedPreference.setSaveLoginData(this, "not_save", checkBox.isChecked());
                            SaveSharedPreference.clearShared(this);
                        }
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("로그인에 실패하였습니다.")
                                .setNegativeButton("다시 시도",null)
                                .create()
                                .show();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            };

            LoginRequest loginRequest = new LoginRequest(userID, userPW, responseListener);
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(loginRequest);
        });

        login_kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);
            }
        });

//        logout_kakao.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                UserManagement.getInstance()
//                        .requestLogout(new LogoutResponseCallback() {
//                            @Override
//                            public void onCompleteLogout() {
//                                Toast.makeText(LoginActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
//        });
    }

    private void updateKakaoLoginUi() {

        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                // 로그인이 되어있으면
                if (user != null) {
                    // 유저의 아이디
                    Log.d(TAG,"invoke: id" + user.getId());
                    Log.d(TAG,"invoke: email" + user.getKakaoAccount().getEmail());
                    Log.d(TAG,"invoke: id" + user.getKakaoAccount().getProfile().getNickname());
                    Log.d(TAG,"invoke: id" + user.getKakaoAccount().getGender());
                    Log.d(TAG,"invoke: id" + user.getKakaoAccount().getAgeRange());
                } else {
                    // 로그인이 안 되어 있을 때
                    Log.d(TAG, "로그인이 안 되어 있습니다.");
                }
                return null;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //세션 콜백 삭제
        Session.getCurrentSession().removeCallback((ISessionCallback) sessoinCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        //카카오톡 스토리 간편 로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode,data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void save() {
        SharedPreferences.Editor editor = appData.edit();
        editor.putBoolean("SAVE_LOGIN_DATA", checkBox.isChecked());
        editor.putString("ID", username.getText().toString().trim());
        editor.putString("PWD", password.getText().toString().trim());

        editor.apply();
    }

    private void load() {
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        userID = appData.getString("ID","");
        userPW = appData.getString("PWD","");
    }
}


class LoginRequest extends StringRequest {
    final static private String URL = "https://android-con.run.goorm.io/login.php";
    private Map<String, String> map;

    public LoginRequest(String userID, String userPW, Response.Listener<String> listener) {
        super(Method.POST, URL, listener,null);

        map = new HashMap<>();
        map.put("uid", userID);
        map.put("upw", userPW);
    }

    protected Map<String, String> getParams() {
        return map;
    }
}