package com.example.idaon.mypage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.idaon.BitmapConverter;
import com.example.idaon.R;
import com.example.idaon.home.CommunityViewModel;
import com.example.idaon.home.ShareActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WriteActivity extends AppCompatActivity implements RecyclerViewInterface {

    private List<MyWriting> itemList;
    ActionBar actionBar;
    private MyRecyclerAdapter adapter;
    private CommunityViewModel mViewModel;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_activity);

        Toolbar toolbar = findViewById(R.id.toolbar9);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("내가 작성한 글");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24);

        SwipeRefreshLayout mSwipeRefreshLayout = findViewById(R.id.mywriting_swipe);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                makeRequest();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);

                    }
                }, 500);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.mywriting_recycler);
        itemList = new ArrayList<>();
        adapter = new MyRecyclerAdapter(this, itemList, this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL); //밑줄
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(dividerItemDecoration);

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
    }

    public void makeRequest() {
        String url = "https://android-con.run.goorm.io/select_mywriting.php";

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
        WritingResultList itemList = gson.fromJson(response, WritingResultList.class);
        adapter.setItems(itemList.result);
        adapter.notifyDataSetChanged();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, ShareActivity.class);

        intent.putExtra("UID", itemList.get(position).getUid());
        intent.putExtra("NICKNAME", itemList.get(position).getUnickname());
        intent.putExtra("TITLE", itemList.get(position).getTitle());
        intent.putExtra("CONTENT", itemList.get(position).getTime());
        intent.putExtra("TIME", itemList.get(position).getTime());

        this.startActivity(intent);
    }
}

class MyWriting {
    private String uid;
    private String unickname;
    private String title;
    private String content;
    private String time;
    private String img;

    public MyWriting() {
        this.uid = uid;
        this.unickname = unickname;
        this.title = title;
        this.content = content;
        this.time = time;
        this.img = img;
    }

    public String getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getUnickname() {
        return unickname;
    }

    public String getTime() {
        return time;
    }

    public String getImg() {return BitmapConverter.RemoveSpace(img);}
}

class WritingResultList {
    ArrayList<MyWriting> result = new ArrayList<>();
}