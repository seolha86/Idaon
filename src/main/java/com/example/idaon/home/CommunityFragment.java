package com.example.idaon.home;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.idaon.BitmapConverter;
import com.example.idaon.MainActivity;
import com.example.idaon.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityFragment extends Fragment implements RecyclerViewInterface {

    private List<Home> itemList;

    private MyRecyclerAdapter adapter;

    private CommunityViewModel mViewModel;

    RequestQueue requestQueue;

    public static CommunityFragment newInstance() {
        return new CommunityFragment();
    }

    public CommunityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("커뮤니티");
        actionBar.setDisplayHomeAsUpEnabled(false);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.community_fragment, container, false);

        SearchView searchView = view.findViewById(R.id.searchview2);
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

        SwipeRefreshLayout mSwipeRefreshLayout = view.findViewById(R.id.home_swipe);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                makeRequest();

                adapter.notifyDataSetChanged();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 500);
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview2);
        itemList = new ArrayList<>();
        adapter = new MyRecyclerAdapter(getActivity(), itemList, this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL); //밑줄
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(dividerItemDecoration);

//        Divider 구분선
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
        String url = "https://android-con.run.goorm.io/select_home.php";

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
        HomeListResult itemList = gson.fromJson(response, HomeListResult.class);
        adapter.setItems(itemList.result);
        adapter.notifyDataSetChanged();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.community_menu,menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_pencil:
                Intent intent1 = new Intent(getActivity(), PencilActivity.class);
                startActivity(intent1);
                break;

            default:
                break;
        }
        return  super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CommunityViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), ShareActivity.class);

        intent.putExtra("NICKNAME", itemList.get(position).getUnickname());
        intent.putExtra("TITLE", itemList.get(position).getTitle());
        intent.putExtra("CONTENT", itemList.get(position).getTime());
        intent.putExtra("TIME", itemList.get(position).getTime());

        getActivity().startActivity(intent);
    }
}

class Home{
    private String uid;
    private String unickname;
    private String title;
    private String content;
    private String time;
    private String img;

    public Home() {
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

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getImg() {
        if (img != null) {
            return BitmapConverter.RemoveSpace(img);
        } else {
            return null;
        }
    }
}

class HomeListResult {
    ArrayList<Home> result = new ArrayList<>();
}