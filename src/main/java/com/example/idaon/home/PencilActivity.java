package com.example.idaon.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.idaon.BitmapConverter;
import com.example.idaon.MainActivity;
import com.example.idaon.R;
import com.example.idaon.SaveSharedPreference;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PencilActivity extends AppCompatActivity {
    ImageView imageView1;
    ActionBar actionBar;
    int REQUEST_EXTERNAL_STORAGE_PERMISSION = 1001;
    Boolean isPermission = true;

    private EditText et_title, et_content;

    private static final int FROM_CAMERA = 0;
    private static final int FROM_ALBUM = 1;
    private Uri imgUri, photoURI, albumURI;
    private String mCurrentPhotoPath, userId, itemimg;

    String img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pencil_activity);

        Toolbar toolbar = findViewById(R.id.toolbar10);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("글쓰기");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24);

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedTitle("권한이 거부됨")
                .setRationaleMessage("카메라와 갤러리 사용을 위해 접근 권한이 필요합니다.")
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setGotoSettingButtonText("권한 허용")
                .setPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();

        imageView1 = findViewById(R.id.pencilimageview);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeDialog();
            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;

            case R.id.done:
                et_title = findViewById(R.id.itemtitle);
                et_content = findViewById(R.id.itemcontent);
                userId = SaveSharedPreference.getUserID(this);

                final String title = et_title.getText().toString();
                final String content = et_content.getText().toString();
                final String uid = userId;
                img = itemimg;

                Response.Listener<String> responseListener = response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean bool1 = jsonObject.getBoolean("success");

                        if(!title.equals("") && !content.equals("")) {
                            if(bool1) {
                                Toast.makeText(getApplicationContext(), "글쓰기 완료", Toast.LENGTH_SHORT).show();

                                onBackPressed();
                            } else {
                                Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };

                if (img != null) {
                    HomeWriteRequest writeRequest = new HomeWriteRequest(title, uid, content, img, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(writeRequest);
                } else {
                    HomeWriteNonImageRequest writeNonImageRequest = new HomeWriteNonImageRequest(title, uid, content, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(writeNonImageRequest);
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.done_menu, menu);
        return true;
    }

    private void makeDialog(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setTitle("사진 업로드").setCancelable(
                false).setPositiveButton("사진촬영",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.v("알림", "다이얼로그 > 사진촬영 선택");
                        // 사진 촬영 클릭
                        takePhoto();
                    }
                }).setNegativeButton("앨범선택",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {
                        Log.v("알림", "다이얼로그 > 앨범선택 선택");
                        //앨범에서 선택
                        selectAlbum();
                    }
                }).setNeutralButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.v("알림", "다이얼로그 > 취소 선택");
                        // 취소 클릭. dialog 닫기.
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    public void selectAlbum(){
        //앨범 열기
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setType("image/*");
        startActivityForResult(intent, FROM_ALBUM);
    }

    public void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this,"사진이 저장되었습니다",Toast.LENGTH_SHORT).show();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            try { photoFile = createImageFile(); }
            catch (IOException ex) { }
            if(photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.idaon.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, FROM_CAMERA);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Intent intent = new Intent();
        Bitmap bm;

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FROM_CAMERA:
                    File file = new File(mCurrentPhotoPath);
                    try {
                        bm = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
//                        bm = resize(bm);
                        itemimg = BitmapConverter.bitmapToString(bm);
                        imageView1.setImageBitmap(bm);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    setResult(RESULT_OK, intent);
                    break;


                case FROM_ALBUM: {
                    try {
                        File albumFile = null;
                        albumFile = createImageFile();
                        photoURI = data.getData();
                        albumURI = Uri.fromFile(albumFile);
                        galleryAddPic();
//                        bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        bm = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
//                        bm = resize(bm);
                        itemimg = BitmapConverter.bitmapToString(bm);
                        Log.d("bitmap", itemimg);
                        imageView1.setImageBitmap(bm);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    setResult(RESULT_OK, intent);
                    break;
                }
            }
        }
    }

    private Bitmap resize(Bitmap bm) {
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 800)
            bm = Bitmap.createScaledBitmap(bm, 400, 240, true);
        else if (config.smallestScreenWidthDp >= 600)
            bm = Bitmap.createScaledBitmap(bm, 300, 180, true);
        else if (config.smallestScreenWidthDp >= 400)
            bm = Bitmap.createScaledBitmap(bm, 200, 120, true);
        else if (config.smallestScreenWidthDp >= 360)
            bm = Bitmap.createScaledBitmap(bm, 180, 108, true);
        else
            bm = Bitmap.createScaledBitmap(bm, 160, 96, true);
        return bm;
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getApplicationContext(), "카메라 권한이 허용됨",
                    Toast.LENGTH_SHORT).show();
            isPermission = true;
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(),"카메라 권한이 거부됨", Toast.LENGTH_SHORT).show();
            isPermission = false;
        }
    };

    private void permissionCheck() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE_PERMISSION);
            }
        }
    }
}

class HomeWriteRequest extends StringRequest {
    final static private String URL = "https://android-con.run.goorm.io/home_write.php";
    private Map<String, String> map;

    public HomeWriteRequest(String title, String uid, String content, String img, Response.Listener<String> responseListener) {
        super(Method.POST, URL, responseListener, null);

        map = new HashMap<>();
        map.put("title", title);
        map.put("uid", uid);
        map.put("content", content);
        map.put("img", img);
    }

    protected Map<String, String> getParams() {
        return map;
    }
}

class HomeWriteNonImageRequest extends StringRequest {
    final static private String URL = "https://android-con.run.goorm.io/home_write_nonimg.php";
    private Map<String, String> map;

    public HomeWriteNonImageRequest(String title, String uid, String content, Response.Listener<String> responseListener) {
        super(Method.POST, URL, responseListener, null);

        map = new HashMap<>();
        map.put("title", title);
        map.put("uid", uid);
        map.put("content", content);
    }

    protected Map<String, String> getParams() {
        return map;
    }
}