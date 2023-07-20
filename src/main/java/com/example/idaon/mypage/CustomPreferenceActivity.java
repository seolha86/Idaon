package com.example.idaon.mypage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.preference.PreferenceScreen;

import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.idaon.LoginActivity;
import com.example.idaon.R;
import com.example.idaon.RegisterActivity;
import com.example.idaon.SaveSharedPreference;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CustomPreferenceActivity extends PreferenceActivity{

    Preference push;
    Preference use;
    Preference privacy;
    Preference version;
    Preference withdrawal;

    Dialog dialog01;
    Dialog dialog02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        push = (Preference)findPreference("push");
        push.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(CustomPreferenceActivity.this , PushPreferenceActivity.class);
                startActivity(intent);
                return true;
            }
        });

        version = (Preference)findPreference("version");
        version.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(CustomPreferenceActivity.this , VersionActivity.class);
                startActivity(intent);
                return false;
            }
        });

        withdrawal = (Preference)findPreference("end");
        withdrawal.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            String uid = SaveSharedPreference.getUserID(CustomPreferenceActivity.this);

            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomPreferenceActivity.this);
                builder.setMessage("탈퇴하시겠습니까?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", ((dialog, which) -> {
                            Response.Listener<String> responseListener = response -> {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean success = jsonObject.getBoolean("success");

                                    if (success) {
                                        Toast.makeText(CustomPreferenceActivity.this, "탈퇴 완료", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(CustomPreferenceActivity.this, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            };
                            WithdrawRequest withdrawRequest = new WithdrawRequest(uid, responseListener);
                            RequestQueue queue = Volley.newRequestQueue(CustomPreferenceActivity.this);
                            queue.add(withdrawRequest);
                        }))
                        .setNegativeButton("No", (dialog, which) -> dialog.cancel());
                AlertDialog alert = builder.create();
                alert.setTitle("회원탈퇴");
                alert.show();
                SaveSharedPreference.clearShared(CustomPreferenceActivity.this);

                return true;
            }
        });

        dialog01 = new Dialog(CustomPreferenceActivity.this);
        dialog01.setContentView(R.layout.activity_agree1);

        use = (Preference)findPreference("use");
        use.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                show();
                return false;
            }
        });

        dialog02 = new Dialog(CustomPreferenceActivity.this);
        dialog02.setContentView(R.layout.activity_agree2);

        privacy = (Preference) findPreference("privacy");
        privacy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                show2();
                return false;
            }
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
}

class WithdrawRequest extends StringRequest {
    final static private String URL = "https://android-con.run.goorm.io/user_withdraw.php";
    private Map<String, String> map;

    public WithdrawRequest(String uid, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("uid", uid);
    }

    protected Map<String, String> getParams() {
        return map;
    }
}