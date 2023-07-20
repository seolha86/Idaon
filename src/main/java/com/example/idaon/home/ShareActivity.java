package com.example.idaon.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.idaon.BitmapConverter;
import com.example.idaon.R;
import com.example.idaon.SaveSharedPreference;
import com.example.idaon.mypage.EdictActivity;
import com.example.idaon.mypage.WriteActivity;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShareActivity extends AppCompatActivity {
    ActionBar actionBar;
    Uri imgUri;
    RequestQueue requestQueue;
    private EditText comment;
    private String userId;
    private List<Comment> commentList;
    ShareAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_activity);

        Toolbar toolbar = findViewById(R.id.toolbar11);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24);

        String nickname = getIntent().getStringExtra("NICKNAME");
        String title = getIntent().getStringExtra("TITLE");
        String content = getIntent().getStringExtra("CONTENT");
        String image = getIntent().getStringExtra("IMG");
        String time = getIntent().getStringExtra("TIME");

        TextView NicknameTextView = findViewById(R.id.txt_id);
        TextView TitleTextView = findViewById(R.id.sharetitle);
        TextView ContentTextView = findViewById(R.id.sharecontent);
        ImageView imageView = findViewById(R.id.shareimage);
        TextView TimeTextView = findViewById(R.id.txt_date);

        NicknameTextView.setText(nickname);
        TitleTextView.setText(title);
        ContentTextView.setText(content);
        imageView.setImageBitmap(BitmapConverter.stringToBitmap(image));
        TimeTextView.setText(time);

        RecyclerView recyclerView = findViewById(R.id.comment_recylcer);
        commentList = new ArrayList<>();
        adapter = new ShareAdapter(this, commentList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (image == null) {
            imageView.setVisibility(View.GONE);
        }

        ImageButton btn_write = findViewById(R.id.btn_r_write);
        btn_write.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    hidekeyboard(btn_write);
                }
                return false;
            }

            private void hidekeyboard(ImageButton btn_write) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btn_write.getWindowToken(),0);
            }
        });

        ImageButton btn_r_write = findViewById(R.id.btn_r_write);
        btn_r_write.setOnClickListener(view -> {
            comment = findViewById(R.id.input_r_content);
            userId = SaveSharedPreference.getUserID(this);

            final String content1 = comment.getText().toString();
            final String uid = userId;

            Response.Listener<String> responseListener = response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean bool1 = jsonObject.getBoolean("success");

                    if (!content1.equals("")) {
                        if (bool1) {
                            comment.setText(null);
                            Toast.makeText(getApplicationContext(), "댓글 쓰기 완료", Toast.LENGTH_SHORT).show();

                            Log.d("userid", uid);
                        } else {
                            Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            CommentRequest writeRequest = new CommentRequest(uid, content1, title, responseListener);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(writeRequest);
        });

        Button comment_btn = findViewById(R.id.comment_button);
        comment_btn.setOnClickListener(view -> makeRequest());

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        }
    }


    private void makeRequest() {
        String url = "https://android-con.run.goorm.io/select_comment.php";

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Log.d("GSON", "응답 -> " + response);

            processResponse(response);
        }, error -> Log.d("GSON", "에러 -> " + error.getMessage())) {
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                return map;
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);
        Log.d("GSON", "요청 보냄");
    }

    @SuppressLint("NotifyDataSetChanged")
    public void processResponse(String response) {
        Gson gson = new Gson();
        CommentListResult commentList = gson.fromJson(response, CommentListResult.class);
        adapter.setItems(commentList.result);
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;

            case R.id.share:
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                String text = "원하는 텍스트를 입력하세요";
                intent.putExtra(Intent.EXTRA_TEXT, text);
                Intent chooser = Intent.createChooser(intent, "공유하기");
                startActivity(chooser);
                break;

            case R.id.delete:

                String share_user = getIntent().getStringExtra("USER_ID");

                String uid = SaveSharedPreference.getUserID(this);
                String title = getIntent().getStringExtra("TITLE");

                Log.d("uid", uid);
                Log.d("title", title);

                Response.Listener<String> responselistener = response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");

                        if (share_user.equals(uid)) {
                            if (success) {
                                Toast.makeText(getApplicationContext(), "삭제완료", Toast.LENGTH_SHORT).show();

                                onBackPressed();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "내 글이 아닙니다.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };

                DeleteRequest deleteRequest = new DeleteRequest(uid, title, responselistener);
                RequestQueue queue = Volley.newRequestQueue(this);
                queue.add(deleteRequest);

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();          // 현재 터치 위치

        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP
                || ev.getAction() == MotionEvent.ACTION_MOVE)
                && view instanceof EditText
                && !view.getClass().getName().startsWith("android.webkit.")) {
            // view 의 id 가 명시되어있지 않은 다른 부분을 터치 시
            int[] scrcoords = new int[2];
            view.getLocationOnScreen(scrcoords);        // 0 은 x 마지막 터치 위치에서 x 값
            // 1은 y 마지막 터치 위치에서 y 값

            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];

            if (x < view.getLeft() || x > view.getRight()
                    || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }

        return super.dispatchTouchEvent(ev);
    }
}

class DeleteRequest extends StringRequest {
    final static private String URL = "https://android-con.run.goorm.io/delete_home.php";
    private Map<String, String> map;

    public DeleteRequest(String uid, String title, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("uid", uid);
        map.put("title", title);
    }

    protected Map<String, String> getParams() {
        return map;
    }
}

class CommentRequest extends StringRequest {
    final static private String URL = "https://android-con.run.goorm.io/comment_home.php";
    private Map<String, String> map;

    public CommentRequest(String uid, String content, String title, Response.Listener<String> responseListener) {
        super(Method.POST, URL, responseListener, null);

        map = new HashMap<>();
        map.put("uid", uid);
        map.put("content", content);
        map.put("title", title);
    }

    protected Map<String, String> getParams() {
        return map;
    }
}

class Comment{
    private String unickname;
    private String content;
    private String time;
    private String title;

    public Comment(String unickname, String content, String time, String title) {
        this.unickname = unickname;
        this.content = content;
        this.time = time;
        this.title = title;
    }

    public String getNickname() {
        return unickname;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {return title;}
}

class CommentListResult {
    ArrayList<Comment> result = new ArrayList<>();
}
