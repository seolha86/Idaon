package com.example.idaon.service;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.example.idaon.BitmapConverter;
import com.example.idaon.MainActivity;
import com.example.idaon.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ServiceFragment extends Fragment implements RecyclerViewInterface{

    private static RequestQueue requestQueue;

    private Gson gson;
    private String requestUrl;
    private ArrayList<Service> itemList = null;
    Service service = null;

    RecyclerView recyclerView;
    MyRecyclerAdapter adapter;

    private DrawerLayout drawerLayout;

    private ServiceViewModel mViewModel;

    static String life = "";

    BottomSheet bottomSheet;

    public static ServiceFragment newInstance() {
        return new ServiceFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("나만의 혜택");
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.service_fragment, container, false);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();

        SearchView searchView = root.findViewById(R.id.searchView);
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

        recyclerView = (RecyclerView) root.findViewById(R.id.recylcerview3);
        recyclerView.setHasFixedSize(true);

        adapter = new MyRecyclerAdapter(getActivity(), itemList, this);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        }

        FloatingActionButton floatingActionButton = (FloatingActionButton) root.findViewById(R.id.floatingButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            @Override
            public void onClick(View view) {
                intent.setData(Uri.parse("https://pf.kakao.com/_xdUxdExj/chat"));
                startActivity(intent);
            }
        });

        SwipeRefreshLayout mSwipeRefreshLayout = root.findViewById(R.id.service_swipe);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyAsyncTask myAsyncTask = new MyAsyncTask();
                        myAsyncTask.execute();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 500);
            }
        });

        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();

        return root;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), ServiceDetailActivity.class);

        intent.putExtra("SERVICE_INGNUM", itemList.get(position).getInqNum());
        intent.putExtra("SERVICE_JURMNOFNM", itemList.get(position).getJurMnofNm());
        intent.putExtra("SERVICE_JURORGNM", itemList.get(position).getJurOrgNm());
        intent.putExtra("SERVICE_LIFE", itemList.get(position).getLifeArray());
        intent.putExtra("SERVICE_SERVDGST", itemList.get(position).getServDgst());
        intent.putExtra("SERVICE_LINK", itemList.get(position).getServDtlLink());
        intent.putExtra("SERVICE_ID", itemList.get(position).getServId());
        intent.putExtra("SERVICE_NAME", itemList.get(position).getServNm());
        intent.putExtra("SERVICE_SVCFRST", itemList.get(position).getSvcfrstRegTs());

        getActivity().startActivity(intent);
    }

    public class MyAsyncTask extends AsyncTask<String, Void, String> implements RecyclerViewInterface {
        String lifeArray = "&lifeArray=" + life;

        @Override
        protected String doInBackground(String... strings) {
            requestUrl = "https://www.bokjiro.go.kr/ssis-tbu/NationalWelfareInformations/NationalWelfarelist.do?serviceKey=서비스키&callTp=L&pageNo=1&numOfRows=500" + lifeArray + "&srchKeyCode=001&SG_APIM=2ug8Dm9qNBfD32JLZGPN64f3EoTlkpD8kSOHWfXpyrY";

            try {
                boolean b_inqNum = false;
                boolean b_jurMnofNm = false;
                boolean b_jurOrgNm = false;
                boolean b_lifeArray = false;
                boolean b_servDgst = false;
                boolean b_servDtlLink = false;
                boolean b_servId = false;
                boolean b_servNm = false;
                boolean b_svcfrstRegTs = false;
                boolean b_trgterIndvdlArray = false;

                URL url = new URL(requestUrl);
                InputStream is = url.openStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new InputStreamReader(is));

                String tag;
                int eventType = parser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            itemList = new ArrayList<Service>();
                            break;

                        case XmlPullParser.END_TAG:
                            tag = parser.getName();

                            if (tag.equals("servList")) {
                                itemList.add(service);
                            }

                            tag = "";
                            break;

                        case XmlPullParser.START_TAG:
                            tag = parser.getName();

                            if (tag.equals("servList")) {
                                service = new Service();
                            }

                            if (tag.equals("inqNum")) b_inqNum = true;
                            if (tag.equals("jurMnofNm")) b_jurMnofNm = true;
                            if (tag.equals("jurOrgNm")) b_jurOrgNm = true;
                            if (tag.equals("lifeArray")) b_lifeArray = true;
                            if (tag.equals("servDgst")) b_servDgst = true;
                            if (tag.equals("servDtlLink")) b_servDtlLink = true;
                            if (tag.equals("servId")) b_servId = true;
                            if (tag.equals("servNm")) b_servNm = true;
                            if (tag.equals("svcfrstRegTs")) b_svcfrstRegTs = true;
                            if (tag.equals("trgterIndvdlArray")) b_trgterIndvdlArray = true;

                            break;

                        case XmlPullParser.TEXT:
                            if (b_inqNum) {
                                if (parser.getText() != null) {
                                    service.setInqNum(parser.getText());
                                    b_inqNum = false;
                                    Log.d("ingnum", parser.getText());
                                } else {
                                    service.setInqNum(BitmapConverter.nullToSpace(parser.getText()));
                                }
                            }

                            if (b_jurMnofNm) {
                                if (parser.getText() != null) {
                                    service.setJurMnofNm(parser.getText());
                                    b_jurMnofNm = false;
                                    Log.d("jurmnofnm", parser.getText());
                                } else {
                                    service.setJurMnofNm(BitmapConverter.nullToSpace(parser.getText()));
                                }
                            }

                            if (b_jurOrgNm) {
                                if (parser.getText() != null) {
                                    service.setJurOrgNm(parser.getText());
                                    b_jurOrgNm = false;
                                    Log.d("jurorgnm", parser.getText());
                                } else {
                                    service.setJurOrgNm(BitmapConverter.nullToSpace(parser.getText()));
                                }
                            }

                            if (b_lifeArray) {
                                if (parser.getText() != null) {
                                    service.setLifeArray(parser.getText());
                                    b_lifeArray = false;
                                    Log.d("life", parser.getText());
                                } else {
                                    service.setServNm(BitmapConverter.nullToSpace(parser.getText()));
                                }
                            }

                            if (b_servDgst) {
                                if (parser.getText() != null) {
                                    service.setServDgst(parser.getText());
                                    b_servDgst = false;
                                    Log.d("servDgst", parser.getText());
                                } else {
                                    service.setServDgst(BitmapConverter.nullToSpace(parser.getText()));
                                }
                            }

                            if (b_servDtlLink) {
                                if (parser.getText() != null) {
                                    service.setServDtlLink(parser.getText());
                                    b_servDtlLink = false;
                                    Log.d("link", parser.getText());
                                } else {
                                    service.setServDtlLink(parser.getText());
                                }
                            }

                            if (b_servId) {
                                if (parser.getText() != null) {
                                    service.setServId(parser.getText());
                                    b_servId = false;
                                    Log.d("id", parser.getText());
                                } else {
                                    service.setServNm(BitmapConverter.nullToSpace(parser.getText()));
                                }
                            }

                            if (b_servNm) {
                                if (parser.getText() != null) {
                                    service.setServNm(parser.getText());
                                    b_servNm = false;
                                    Log.d("name", parser.getText());
                                } else {
                                    service.setServNm(BitmapConverter.nullToSpace(parser.getText()));
                                }
                            }

                            if (b_svcfrstRegTs) {
                                if (parser.getText() != null) {
                                    service.setSvcfrstRegTs(parser.getText());
                                    b_svcfrstRegTs = false;
                                    Log.d("svcfrst", parser.getText());
                                } else {
                                    service.setSvcfrstRegTs(BitmapConverter.nullToSpace(parser.getText()));
                                }
                            }

                            if (b_trgterIndvdlArray) {
                                if (parser.getText() != null) {
                                    service.setTrgterIndvdlArray(parser.getText());
                                    b_trgterIndvdlArray = false;
                                    Log.d("trgterarray", parser.getText());
                                } else {
                                    service.setTrgterIndvdlArray(BitmapConverter.nullToSpace(parser.getText()));
                                }
                            }

                            break;
                    }
                    eventType = parser.next();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            adapter = new MyRecyclerAdapter(getActivity().getApplicationContext(), itemList, this);
            recyclerView.setAdapter(adapter);
        }

        @Override
        public void onItemClick(int position) {
            Intent intent = new Intent(getActivity(), ServiceDetailActivity.class);

            intent.putExtra("SERVICE_INGNUM", itemList.get(position).getInqNum());
            intent.putExtra("SERVICE_JURMNOFNM", itemList.get(position).getJurMnofNm());
            intent.putExtra("SERVICE_JURORGNM", itemList.get(position).getJurOrgNm());
            intent.putExtra("SERVICE_LIFE", itemList.get(position).getLifeArray());
            intent.putExtra("SERVICE_SERVDGST", itemList.get(position).getServDgst());
            intent.putExtra("SERVICE_LINK", itemList.get(position).getServDtlLink());
            intent.putExtra("SERVICE_ID", itemList.get(position).getServId());
            intent.putExtra("SERVICE_NAME", itemList.get(position).getServNm());
            intent.putExtra("SERVICE_SVCFRST", itemList.get(position).getSvcfrstRegTs());
            intent.putExtra("SERVICE_TRGTERINDVD", itemList.get(position).getTrgterIndvdlArray());

            getActivity().startActivity(intent);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.service_menu,menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.service_menu:
                bottomSheet = new BottomSheet();
                bottomSheet.show(getActivity().getSupportFragmentManager(), bottomSheet.getTag());

                break;
            default:
                break;
        }
        return  super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ServiceViewModel.class);
        // TODO: Use the ViewModel
    }


    public static class BottomSheet extends BottomSheetDialogFragment {
        RadioGroup radioGroup1, radioGroup2, radioGroup3, radioGroup4, radioGroup5;
        RadioButton a1,a2,a3,a4,a5,a6,a7,a8,a9,a10,a11,a12,a13,a14,a15;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.bottom_sheet, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            radioGroup1 = view.findViewById(R.id.radioGroup1);
            radioGroup2 = view.findViewById(R.id.radioGroup2);
            radioGroup3 = view.findViewById(R.id.radioGroup3);
            radioGroup4 = view.findViewById(R.id.radioGroup4);
            radioGroup5 = view.findViewById(R.id.radioGroup5);
            a1 = view.findViewById(R.id.a1);
            a2 = view.findViewById(R.id.a2);
            a3 = view.findViewById(R.id.a3);
            a4 = view.findViewById(R.id.a4);
            a5 = view.findViewById(R.id.a5);
            a6 = view.findViewById(R.id.a6);
            a7 = view.findViewById(R.id.a7);
            a8 = view.findViewById(R.id.a8);
            a9 = view.findViewById(R.id.a9);
            a10 = view.findViewById(R.id.a10);
            a11 = view.findViewById(R.id.a11);
            a12 = view.findViewById(R.id.a12);
            a13 = view.findViewById(R.id.a13);
            a14 = view.findViewById(R.id.a14);
            a15 = view.findViewById(R.id.a15);

            radioGroup1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (a1.isChecked()) {
                        a2.setChecked(false);
                        a3.setChecked(false);
                        a4.setChecked(false);
                        a5.setChecked(false);
                        a6.setChecked(false);
                        a7.setChecked(false);
                        a8.setChecked(false);
                        a9.setChecked(false);
                        a10.setChecked(false);
                        a11.setChecked(false);
                        a12.setChecked(false);
                        a13.setChecked(false);
                        a14.setChecked(false);
                        a15.setChecked(false);
                    }
                    if (a2.isChecked()) {
                        a1.setChecked(false);
                        a3.setChecked(false);
                        a4.setChecked(false);
                        a5.setChecked(false);
                        a6.setChecked(false);
                        a7.setChecked(false);
                        a8.setChecked(false);
                        a9.setChecked(false);
                        a10.setChecked(false);
                        a11.setChecked(false);
                        a12.setChecked(false);
                        a13.setChecked(false);
                        a14.setChecked(false);
                        a15.setChecked(false);
                    }
                    if (a3.isChecked()) {
                        a1.setChecked(false);
                        a2.setChecked(false);
                        a4.setChecked(false);
                        a5.setChecked(false);
                        a6.setChecked(false);
                        a7.setChecked(false);
                        a8.setChecked(false);
                        a9.setChecked(false);
                        a10.setChecked(false);
                        a11.setChecked(false);
                        a12.setChecked(false);
                        a13.setChecked(false);
                        a14.setChecked(false);
                        a15.setChecked(false);
                    }
                }
            });
            radioGroup2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (a4.isChecked()) {
                        a2.setChecked(false);
                        a3.setChecked(false);
                        a1.setChecked(false);
                        a5.setChecked(false);
                        a6.setChecked(false);
                        a7.setChecked(false);
                        a8.setChecked(false);
                        a9.setChecked(false);
                        a10.setChecked(false);
                        a11.setChecked(false);
                        a12.setChecked(false);
                        a13.setChecked(false);
                        a14.setChecked(false);
                        a15.setChecked(false);
                    }
                    if (a5.isChecked()) {
                        a1.setChecked(false);
                        a3.setChecked(false);
                        a4.setChecked(false);
                        a2.setChecked(false);
                        a6.setChecked(false);
                        a7.setChecked(false);
                        a8.setChecked(false);
                        a9.setChecked(false);
                        a10.setChecked(false);
                        a11.setChecked(false);
                        a12.setChecked(false);
                        a13.setChecked(false);
                        a14.setChecked(false);
                        a15.setChecked(false);
                    }
                    if (a6.isChecked()) {
                        a1.setChecked(false);
                        a2.setChecked(false);
                        a4.setChecked(false);
                        a5.setChecked(false);
                        a3.setChecked(false);
                        a7.setChecked(false);
                        a8.setChecked(false);
                        a9.setChecked(false);
                        a10.setChecked(false);
                        a11.setChecked(false);
                        a12.setChecked(false);
                        a13.setChecked(false);
                        a14.setChecked(false);
                        a15.setChecked(false);
                    }
                }
            });
            radioGroup3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (a7.isChecked()) {
                        a2.setChecked(false);
                        a3.setChecked(false);
                        a4.setChecked(false);
                        a5.setChecked(false);
                        a6.setChecked(false);
                        a1.setChecked(false);
                        a8.setChecked(false);
                        a9.setChecked(false);
                        a10.setChecked(false);
                        a11.setChecked(false);
                        a12.setChecked(false);
                        a13.setChecked(false);
                        a14.setChecked(false);
                        a15.setChecked(false);
                    }
                    if (a8.isChecked()) {
                        a1.setChecked(false);
                        a3.setChecked(false);
                        a4.setChecked(false);
                        a5.setChecked(false);
                        a6.setChecked(false);
                        a7.setChecked(false);
                        a2.setChecked(false);
                        a9.setChecked(false);
                        a10.setChecked(false);
                        a11.setChecked(false);
                        a12.setChecked(false);
                        a13.setChecked(false);
                        a14.setChecked(false);
                        a15.setChecked(false);
                    }
                    if (a9.isChecked()) {
                        a1.setChecked(false);
                        a2.setChecked(false);
                        a4.setChecked(false);
                        a5.setChecked(false);
                        a6.setChecked(false);
                        a7.setChecked(false);
                        a8.setChecked(false);
                        a3.setChecked(false);
                        a10.setChecked(false);
                        a11.setChecked(false);
                        a12.setChecked(false);
                        a13.setChecked(false);
                        a14.setChecked(false);
                        a15.setChecked(false);
                    }
                }
            });
            radioGroup4.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (a10.isChecked()) {
                        a2.setChecked(false);
                        a3.setChecked(false);
                        a4.setChecked(false);
                        a5.setChecked(false);
                        a6.setChecked(false);
                        a7.setChecked(false);
                        a8.setChecked(false);
                        a9.setChecked(false);
                        a1.setChecked(false);
                        a11.setChecked(false);
                        a12.setChecked(false);
                        a13.setChecked(false);
                        a14.setChecked(false);
                        a15.setChecked(false);
                    }
                    if (a11.isChecked()) {
                        a1.setChecked(false);
                        a3.setChecked(false);
                        a4.setChecked(false);
                        a5.setChecked(false);
                        a6.setChecked(false);
                        a7.setChecked(false);
                        a8.setChecked(false);
                        a9.setChecked(false);
                        a10.setChecked(false);
                        a2.setChecked(false);
                        a12.setChecked(false);
                        a13.setChecked(false);
                        a14.setChecked(false);
                        a15.setChecked(false);
                    }
                    if (a12.isChecked()) {
                        a1.setChecked(false);
                        a2.setChecked(false);
                        a4.setChecked(false);
                        a5.setChecked(false);
                        a6.setChecked(false);
                        a7.setChecked(false);
                        a8.setChecked(false);
                        a9.setChecked(false);
                        a10.setChecked(false);
                        a11.setChecked(false);
                        a3.setChecked(false);
                        a13.setChecked(false);
                        a14.setChecked(false);
                        a15.setChecked(false);
                    }
                }
            });
            radioGroup5.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (a13.isChecked()) {
                        a2.setChecked(false);
                        a3.setChecked(false);
                        a4.setChecked(false);
                        a5.setChecked(false);
                        a6.setChecked(false);
                        a7.setChecked(false);
                        a8.setChecked(false);
                        a9.setChecked(false);
                        a10.setChecked(false);
                        a11.setChecked(false);
                        a12.setChecked(false);
                        a1.setChecked(false);
                        a14.setChecked(false);
                        a15.setChecked(false);
                    }
                    if (a14.isChecked()) {
                        a1.setChecked(false);
                        a3.setChecked(false);
                        a4.setChecked(false);
                        a5.setChecked(false);
                        a6.setChecked(false);
                        a7.setChecked(false);
                        a8.setChecked(false);
                        a9.setChecked(false);
                        a10.setChecked(false);
                        a11.setChecked(false);
                        a12.setChecked(false);
                        a13.setChecked(false);
                        a2.setChecked(false);
                        a15.setChecked(false);
                    }
                    if (a15.isChecked()) {
                        a1.setChecked(false);
                        a2.setChecked(false);
                        a4.setChecked(false);
                        a5.setChecked(false);
                        a6.setChecked(false);
                        a7.setChecked(false);
                        a8.setChecked(false);
                        a9.setChecked(false);
                        a10.setChecked(false);
                        a11.setChecked(false);
                        a12.setChecked(false);
                        a13.setChecked(false);
                        a14.setChecked(false);
                        a3.setChecked(false);
                    }
                }
            });


            view.findViewById(R.id.btn_hide_bt_sheet).setOnClickListener(view1 -> dismiss());

            view.findViewById(R.id.btn_apply_bt_sheet).setOnClickListener(view12 -> {
//                RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
                RadioButton lifeArray007 = view.findViewById(R.id.a1);
                RadioButton lifeArray001 = view.findViewById(R.id.a10);
                RadioButton lifeArray002 = view.findViewById(R.id.a15);
                RadioButton lifeArray003 = view.findViewById(R.id.a11);
                RadioButton lifeArray004 = view.findViewById(R.id.a12);
                RadioButton lifeArray005 = view.findViewById(R.id.a13);
                RadioButton lifeArray006 = view.findViewById(R.id.a6);
                RadioButton lifeArray03 = view.findViewById(R.id.a5);
                RadioButton lifeArray06 = view.findViewById(R.id.a3);
                RadioButton lifeArray6 = view.findViewById(R.id.a2);
                RadioButton lifeArray3 = view.findViewById(R.id.a8);
                RadioButton lifeArray01 = view.findViewById(R.id.a9);
                RadioButton lifeArray05 = view.findViewById(R.id.a14);
                RadioButton lifeArray02 = view.findViewById(R.id.a4);
                RadioButton lifeArray1 = view.findViewById(R.id.a7);

                if (lifeArray007.isChecked()) {
                    life = "007";

                } else if (lifeArray001.isChecked()){
                    life = "001";
                } else if (lifeArray002.isChecked()){
                    life = "002";
                } else if (lifeArray003.isChecked()){
                    life = "003";
                } else if (lifeArray004.isChecked()){
                    life = "004";
                } else if (lifeArray005.isChecked()){
                    life = "005";
                } else if (lifeArray006.isChecked()){
                    life = "006";
                } else if (lifeArray03.isChecked()){
                    life = "003";
                } else if (lifeArray06.isChecked()){
                    life = "006";
                } else if (lifeArray6.isChecked()){
                    life = "006";
                } else if (lifeArray3.isChecked()){
                    life = "003";
                } else if (lifeArray01.isChecked()){
                    life = "001";
                } else if (lifeArray05.isChecked()){
                    life = "005";
                } else if (lifeArray02.isChecked()){
                    life = "002";
                } else if (lifeArray1.isChecked()){
                    life = "001";
                }


                dismiss();
            });
        }

    }

    private void refresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}

class Service {
    private String inqNum;
    private String jurMnofNm;
    private String jurOrgNm;
    private String lifeArray;
    private String servDgst;
    private String servDtlLink;
    private String servId;
    private String servNm;
    private String svcfrstRegTs;
    private String trgterIndvdlArray;

    public Service() {
        this.lifeArray = lifeArray;
        this.servId = servId;
        this.servNm = servNm;
    }

    public String getInqNum() {
        return inqNum;
    }

    public void setInqNum(String inqNum) {
        this.inqNum = inqNum;
    }

    public String getJurMnofNm() {
        return jurMnofNm;
    }

    public void setJurMnofNm(String jurMnofNm) {
        this.jurMnofNm = jurMnofNm;
    }

    public String getJurOrgNm() {
        return jurOrgNm;
    }

    public void setJurOrgNm(String jurOrgNm) {
        this.jurOrgNm = jurOrgNm;
    }

    public String getLifeArray() {
        return lifeArray;
    }

    public void setLifeArray(String lifeArray) {
        this.lifeArray = lifeArray;
    }

    public String getServDgst() {
        return servDgst;
    }

    public void setServDgst(String servDgst) {
        this.servDgst = servDgst;
    }

    public String getServDtlLink() {
        return servDtlLink;
    }

    public void setServDtlLink(String servDtlLink) {
        this.servDtlLink = servDtlLink;
    }

    public String getServId() {
        return servId;
    }

    public void setServId(String servId) {
        this.servId = servId;
    }

    public String getServNm() {
        return servNm;
    }

    public void setServNm(String servNm) {
        this.servNm = servNm;
    }

    public String getSvcfrstRegTs() {
        return svcfrstRegTs;
    }

    public void setSvcfrstRegTs(String svcfrstRegTs) {
        this.svcfrstRegTs = svcfrstRegTs;
    }

    public String getTrgterIndvdlArray() {
        return trgterIndvdlArray;
    }

    public void setTrgterIndvdlArray(String trgterIndvdlArray) {
        this.trgterIndvdlArray = trgterIndvdlArray;
    }
}