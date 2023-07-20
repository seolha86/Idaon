package com.example.idaon.item;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.idaon.BitmapConverter;
import com.example.idaon.MainActivity;
import com.example.idaon.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemFragment extends Fragment implements RecyclerViewInterface {

    private List<Item> itemList;
    MyRecyclerAdapter adapter;
    RequestQueue requestQueue;

    public static ItemFragment newInstance() {
        return new ItemFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("중고 거래");
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_fragment, container, false);

        SearchView searchView = view.findViewById(R.id.searchview1);
//        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.clearFocus();
        searchView.setOnClickListener(view1 -> searchView.setIconified(false));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();

        SwipeRefreshLayout mSwipeRefreshLayout = view.findViewById(R.id.item_swipe);
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

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        itemList = new ArrayList<>();
        adapter = new MyRecyclerAdapter(getActivity(), itemList, this);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floatingButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            @Override
            public void onClick(View view) {
                intent.setData(Uri.parse("https://pf.kakao.com/_xdUxdExj/chat"));
                startActivity(intent);
            }
        });

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        }

        return view;
    }

    public void makeRequest() {
        String url = "https://android-con.run.goorm.io/select_item.php";

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
        ItemResultList itemList = gson.fromJson(response, ItemResultList.class);
        adapter.setItems(itemList.result);
        adapter.notifyDataSetChanged();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.item_menu,menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_chat2:
                Intent intent1 = new Intent(getActivity(), ChatActivity.class);
                startActivity(intent1);
                break;

            case R.id.menu_add:
                Intent intent = new Intent(getActivity(), AddActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
        return  super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);

        intent.putExtra("UID", itemList.get(position).getUid());
        intent.putExtra("NICKNAME", itemList.get(position).getUnickname());
        intent.putExtra("NAME", itemList.get(position).getIname());
        intent.putExtra("PRICE", itemList.get(position).getPrice());
        intent.putExtra("CONTENT", itemList.get(position).getContent());
        intent.putExtra("TIME", itemList.get(position).getTimestamp());
        intent.putExtra("IMG", itemList.get(position).getImg());

        getActivity().startActivity(intent);
    }
}

class ItemResultList {
    ArrayList<Item> result = new ArrayList<>();
}

class Item{
    private String uid;
    private String unickname;
    private String iname;
    private String price;
    private String content;
    private String time;
    private String img;

    public Item() {
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

    public String getTimestamp() {
        return time;
    }

    public String getImg() {
        return BitmapConverter.RemoveSpace(img);
    }
}