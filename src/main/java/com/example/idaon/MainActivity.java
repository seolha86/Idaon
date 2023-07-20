package com.example.idaon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.idaon.home.CommunityFragment;
import com.example.idaon.item.ItemFragment;
import com.example.idaon.map.MapFragment;
import com.example.idaon.mypage.MypageFragment;
import com.example.idaon.service.ServiceFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionBar;
    BottomNavigationView navigationView;
    Menu menu;

    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID = "101";

    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getHashKey();

        createNotificationChannel();
        getToken();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24);

        navigationView = findViewById(R.id.bottom_nav);
        menu = navigationView.getMenu();
        navigationView.setSelectedItemId(R.id.navigation_service);
        navigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        getSupportFragmentManager().beginTransaction().add(R.id.container, new ServiceFragment()).addToBackStack(null).commit();

    }

    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }

//
    private class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_community:
                    item.setIcon(R.drawable.books);
                    menu.findItem(R.id.navigation_item).setIcon(R.drawable.ic_outline_shopping_cart_24);
                    menu.findItem(R.id.navigation_map).setIcon(R.drawable.ic_outline_location_on_24);
                    menu.findItem(R.id.navigation_service).setIcon(R.drawable.ic_outline_home_24);
                    menu.findItem(R.id.navigation_mypage).setIcon(R.drawable.ic_outline_person_24);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new CommunityFragment()).commit();
                    break;
                case R.id.navigation_item:
                    item.setIcon(R.drawable.shopping);
                    menu.findItem(R.id.navigation_community).setIcon(R.drawable.ic_outline_library_books_24);
                    menu.findItem(R.id.navigation_map).setIcon(R.drawable.ic_outline_location_on_24);
                    menu.findItem(R.id.navigation_service).setIcon(R.drawable.ic_outline_home_24);
                    menu.findItem(R.id.navigation_mypage).setIcon(R.drawable.ic_outline_person_24);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new ItemFragment()).commit();
                    break;
                case R.id.navigation_map:
                    item.setIcon(R.drawable.location);
                    menu.findItem(R.id.navigation_community).setIcon(R.drawable.ic_outline_library_books_24);
                    menu.findItem(R.id.navigation_item).setIcon(R.drawable.ic_outline_shopping_cart_24);
                    menu.findItem(R.id.navigation_service).setIcon(R.drawable.ic_outline_home_24);
                    menu.findItem(R.id.navigation_mypage).setIcon(R.drawable.ic_outline_person_24);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new MapFragment()).commit();
                    break;
                case R.id.navigation_service:
                    item.setIcon(R.drawable.home);
                    menu.findItem(R.id.navigation_community).setIcon(R.drawable.ic_outline_library_books_24);
                    menu.findItem(R.id.navigation_item).setIcon(R.drawable.ic_outline_shopping_cart_24);
                    menu.findItem(R.id.navigation_map).setIcon(R.drawable.ic_outline_location_on_24);
                    menu.findItem(R.id.navigation_mypage).setIcon(R.drawable.ic_outline_person_24);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new ServiceFragment()).commit();
                    break;
                case R.id.navigation_mypage:
                    item.setIcon(R.drawable.person);
                    menu.findItem(R.id.navigation_community).setIcon(R.drawable.ic_outline_library_books_24);
                    menu.findItem(R.id.navigation_item).setIcon(R.drawable.ic_outline_shopping_cart_24);
                    menu.findItem(R.id.navigation_map).setIcon(R.drawable.ic_outline_location_on_24);
                    menu.findItem(R.id.navigation_service).setIcon(R.drawable.ic_outline_home_24);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new MypageFragment()).commit();
                    break;
            }
            return true;
        }

    }

    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                //If task is failed then
                if (!task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Failed to get the Token");
                }

                //Token
                String token = task.getResult();
                Log.d(TAG, "onComplete: " + token);
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "firebaseNotifChannel";
            String description = "Receve Firebase notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

//    private void send_FCM(){
//
//        Log.d(TAG+ " send_FCM", "실행");
//        Response.Listener<String> responseListener = new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG+ " send_FCM",response );
//
//
//            }//  public void onResponse(String response)
//        };
//        fcm_request getUserInformation = new fcm_request(token,responseListener);
//        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
//        queue.add(getUserInformation);
//    }
//
//    public class fcm_request extends StringRequest {
//        // 서버 URL 설정 ( PHP 파일 연동 )
//        final static private String URL = "https://android-con.run.goorm.io/notify.php"; //내 서버 명
//        private Map<String, String> map; // Key, Value로 저장 됨
//
//
//        public fcm_request(String Token, Response.Listener<String> listener) {
//            super(Method.POST, URL, listener, null);
//
//            map = new HashMap<>();
//            map.put("Token",Token);
//
//
//
//        }
//        @Override
//        protected Map<String, String> getParams() throws AuthFailureError {
//            return map;
//        }
//    }
}


