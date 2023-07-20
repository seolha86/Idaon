package com.example.idaon.map;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import com.example.idaon.MainActivity;
import com.example.idaon.R;
import com.example.idaon.map.MapViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

public class MapFragment extends Fragment {
    //        implements OnMapReadyCallback
    private MapViewModel mViewModel;
    private static final int MY_PERMISSION_REQUEST_LOCATION = 0;
    private WebView mwebView;
    private String myUrl = "file://android_assets/www/map.html";
    Boolean isPermission = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("장소");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)
                inflater.inflate(R.layout.map_fragment, container, false);
        mwebView = (WebView) rootView.findViewById(R.id.webView); //xml 연결
        mwebView.getSettings().setJavaScriptEnabled(true); //자바스크립트 허용
        mwebView.getSettings().setGeolocationEnabled(true);
        mwebView.setWebViewClient(new WebViewClient()); //새창없이 웹뷰내에서 다시 열기
        mwebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                callback.invoke(origin, true, false);
            }
        }); //웹뷰에서 크롬 사용 허용
        mwebView.loadUrl("https://android-con.run.goorm.io/map/index.html"); //연결원하는 URL 실행
        TedPermission.with(getActivity())
                .setPermissionListener(permissionlistener)
                .setDeniedTitle("권한이 거부됨")
                .setRationaleMessage("현재위치 사용을 위해 위치 접근 권한이 필요합니다.")
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setGotoSettingButtonText("권한 허용")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();
        return rootView;
    }

    //TedPermission 라이브러리 -> 현재위치 권한 획득
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getActivity(), "현재위치 권한이 허용됨",
                    Toast.LENGTH_SHORT).show();
            isPermission = true;
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(getActivity(), "현재위치 권한이 거부됨", Toast.LENGTH_SHORT).show();
            isPermission = false;
        }
    };

    private void initWebView() {
        mwebView.getSettings().setJavaScriptEnabled(true); //자바스크립트 허용
// mwebView.getSettings().setGeolocationEnabled(true);
        mwebView.setWebViewClient(new WebViewClient()); //새창없이 웹뷰내에서 다시 열기
        mwebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                callback.invoke(origin, true, false);
            }
        }); //웹뷰에서 크롬 사용 허용
        mwebView.loadUrl("https://idaon.run.goorm.io/map/index.html"); //연결원하는 URL 실행
    }

    private void permissionCheck() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            //Manifest.permission.ACCESS_FIME_LOCATION 접근 승낙 상태 일때
            initWebView();
        } else {
            //Manifest.permission.ACCESS_FINE_LOCATION 접근 거절 상태 일때
            //사용자에게 접근권한 설정을 요구하는 다이얼로그를 띄운다.
            ActivityCompat.requestPermissions(getActivity(), new
                            String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_REQUEST_LOCATION) {
            initWebView();
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}





