package com.example.idaon;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText et_id, et_pw, et_pw2, et_name, et_nickname;
    private Button sign_in, id_check, nickname_check;
    private CheckBox all_agree;
    private AlertDialog dialog;
    private boolean validate = false;

    Dialog dialog01;
    Dialog dialog02;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dialog01 = new Dialog(RegisterActivity.this);
        dialog01.setContentView(R.layout.activity_agree1);

        findViewById(R.id.agree1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show();
            }
        });

        dialog02 = new Dialog(RegisterActivity.this);
        dialog02.setContentView(R.layout.activity_agree2);

        findViewById(R.id.agree2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show2();
            }
        });

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        et_id = findViewById(R.id.username);
        et_pw = findViewById(R.id.password);
        et_pw2 = findViewById(R.id.password2);
        et_name = findViewById(R.id.username2);
        et_nickname = findViewById(R.id.nickname);
        id_check = findViewById(R.id.button2);
        nickname_check = findViewById(R.id.nicknamevalidate);
        sign_in = findViewById(R.id.button);
        all_agree = findViewById(R.id.checkBox3);

        //아이디 중복확인
        id_check.setOnClickListener(view -> {
            String uid = et_id.getText().toString();
            if (validate) {
                return;
            }

            if (uid.equals("")) { //아이디가 빈칸이면
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                dialog = builder.setMessage("아이디를 입력하세요")
                        .setPositiveButton("확인", null).create();
                dialog.show();
                return;
            }

            // 아이디 중복체크
            Response.Listener<String> responseListener = response -> {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) { //아이디에 DB에 존재하지 않으면
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        dialog = builder.setMessage("사용할 수 있는 아이디입니다.")
                                .setPositiveButton("확인", null).create();
                        dialog.show();
                        validate = true;

                        et_id.setEnabled(false); //아이디값 고정
                        id_check.setVisibility(View.INVISIBLE);
//                                sendCodeBtn
                    } else { //아이디가 DB에 존재하면
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        dialog = builder.setMessage("이미 존재하는 아이디입니다.")
                                .setNegativeButton("확인", null).create();
                        dialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
            IdValidateRequest validateRequest = new IdValidateRequest(uid, responseListener);
            RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
            queue.add(validateRequest);
        });

        // 닉네임 중복확인
        nickname_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String unickname = et_nickname.getText().toString();
                if (validate) {
                    return;
                }

                if (unickname.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("닉네임을 입력하세요")
                            .setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }

                // 닉네임 중복체크
                Response.Listener<String> responseListener = response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) { //닉네임이 DB에 존재하지 않으면
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            dialog = builder.setMessage("사용할 수 있는 닉네임입니다.")
                                    .setPositiveButton("확인", null).create();
                            dialog.show();
                            validate = true;

                            et_nickname.setEnabled(false);
                            nickname_check.setVisibility(View.INVISIBLE);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            dialog = builder.setMessage("이미 존재하는 닉네임입니다.")
                                    .setNegativeButton("확인", null).create();
                            dialog.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
                NicknameValidateRequest validateRequest = new NicknameValidateRequest(unickname, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateRequest);
            }
        });

        // 회원가입 버튼
        this.sign_in.setOnClickListener(view -> {
            String uid = et_id.getText().toString();
            final String uname = et_name.getText().toString();
            final String unickname = et_nickname.getText().toString();
            final String upw = et_pw.getText().toString();
            String upw2 = et_pw2.getText().toString();

            Response.Listener<String> responseListener = response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean bool1 = jsonObject.getBoolean("success");

                    if (all_agree.isChecked()) {
                        if (upw.equals(upw2)) {
                            if(bool1) {
                                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "회원가입에 실패했습니다. 다시 확인해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "약관에 동의해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            RegisterRequest registerRequest = new RegisterRequest(uid, upw, uname, unickname, responseListener);
            RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
            queue.add(registerRequest);
        });
    }

    public void show(){
        dialog01.show();

        Button yesBtn = dialog01.findViewById(R.id.yesBtn);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog01.dismiss();
            }
        });
    }
    public void show2(){
        dialog02.show();

        Button yesBtn2 = dialog02.findViewById(R.id.yesBtn2);
        yesBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog02.dismiss();
            }
        });
    }

    //뒤로가기 버튼
    public void lbtn(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}

class RegisterRequest extends StringRequest {
    final static private String URL = "https://android-con.run.goorm.io/register.php";
    private Map<String, String> map;

    public RegisterRequest(String uid, String upw, String uname, String unickname, Response.Listener<String> responseListener) {
        super(Method.POST, URL, responseListener,null);

        map = new HashMap<>();
        map.put("uid", uid);
        map.put("upw", upw);
        map.put("uname", uname);
        map.put("unickname", unickname);
//        map.put("usex", usex);
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}

class IdValidateRequest extends StringRequest {
    final static private String URL = "https://android-con.run.goorm.io/UserValidate.php";
    private Map <String, String> map;

    public IdValidateRequest(String uid, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("uid", uid);
    }

    protected Map<String, String> getParams() {
        return map;
    }
}

class NicknameValidateRequest extends StringRequest {
    final static private String URL = "https://android-con.run.goorm.io/NicknameValidate.php";
    private Map <String, String> map;

    public NicknameValidateRequest(String unickname, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("unickname", unickname);
    }

    protected Map<String, String> getParams() {
        return map;
    }
}