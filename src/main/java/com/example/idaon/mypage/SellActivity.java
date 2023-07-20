package com.example.idaon.mypage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
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
import com.example.idaon.item.DetailActivity;
import com.example.idaon.item.RecyclerViewInterface;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellActivity extends AppCompatActivity implements RecyclerViewInterface2 {

    private List<MySell> itemList;
    ActionBar actionBar;
    private MyRecyclerAdapter2 adapter;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sell_activity);

        Toolbar toolbar = findViewById(R.id.toolbar7);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("판매 내역");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24);

        SwipeRefreshLayout mSwipeRefreshLayout = findViewById(R.id.sell_swipe);
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
        RecyclerView recyclerView = findViewById(R.id.sell_recycler);
        itemList = new ArrayList<>();
        adapter = new MyRecyclerAdapter2(this, itemList, this);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
    }

    public void makeRequest() {
        String url = "https://android-con.run.goorm.io/select_mysell.php";

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
        SellResultList itemList = gson.fromJson(response, SellResultList.class);
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
        Intent intent = new Intent(this, DetailActivity.class);

        intent.putExtra("UID", itemList.get(position).getUid());
        intent.putExtra("NICKNAME", itemList.get(position).getUnickname());
        intent.putExtra("NAME", itemList.get(position).getIname());
        intent.putExtra("PRICE", itemList.get(position).getPrice());
        intent.putExtra("CONTENT", itemList.get(position).getContent());
        intent.putExtra("TIME", itemList.get(position).getTime());
        intent.putExtra("IMG", itemList.get(position).getImg());

        this.startActivity(intent);
    }
}

class MySell {
    private String uid;
    private String unickname;
    private String iname;
    private String price;
    private String content;
    private String time;
    private String img;

    public MySell(String uid, String unickname, String iname, String price, String content, String time, String img) {
        this.uid = uid;
        this.unickname = unickname;
        this.iname = iname;
        this.price = price;
        this.content = content;
        this.time = time;
        this.img = img;
    }

    public String getUid() {
        return uid;
    }

    public String getUnickname() {
        return unickname;
    }

    public String getIname() {
        return iname;
    }

    public String getPrice() {
        return price;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public String getImg() {
        return BitmapConverter.RemoveSpace(img);
    }
}

class SellResultList {
    ArrayList<MySell> result = new ArrayList<>();
}