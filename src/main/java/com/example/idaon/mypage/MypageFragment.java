package com.example.idaon.mypage;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.idaon.MainActivity;
import com.example.idaon.R;
import com.example.idaon.SaveSharedPreference;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MypageFragment extends Fragment {

    private MypageViewModel mViewModel;
    AlertDialog.Builder builder;

    public static MypageFragment newInstance() {
        return new MypageFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("나의 다온");
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mypage_fragment, container, false);

        TextView uid = (TextView) view.findViewById(R.id.userid);
        uid.setText(SaveSharedPreference.getUserID(getActivity()));

        Button edit = (Button)view.findViewById(R.id.edit);
        edit.setOnClickListener(view13 -> {
            Intent intent = new Intent(getActivity(), EdictActivity.class);
            startActivity(intent);
        });

        Button ibtn1 = (Button) view.findViewById(R.id.ibtn1);
        ibtn1.setOnClickListener(view12 -> {
            Intent intent = new Intent(getActivity(), SellActivity.class);
            startActivity(intent);
        });


        Button ibtn3 = (Button) view.findViewById(R.id.ibtn3);
        ibtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LikeActivity.class);
                startActivity(intent);
            }
        });
        Button region = (Button)view.findViewById(R.id.myregion);
        region.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyregionActivity.class);
                startActivity(intent);
            }
        });

        Button write = (Button)view.findViewById(R.id.write);
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WriteActivity.class);
                startActivity(intent);
            }
        });

        Button settings = (Button)view.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CustomPreferenceActivity.class);
                startActivity(intent);
            }
        });

        Button btnlogout = (Button)view.findViewById(R.id.btnlogout);
        builder = new AlertDialog.Builder(getActivity());
        btnlogout.setOnClickListener(view1 -> {
            builder.setMessage("로그아웃 하시겠습니까?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", (dialogInterface, which) -> getActivity().finish())
                    .setNegativeButton("No", (dialogInterface, which) -> dialogInterface.cancel());
            AlertDialog alert = builder.create();
            alert.setTitle("로그아웃");
            alert.show();
            SaveSharedPreference.clearShared(getActivity());
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MypageViewModel.class);
        // TODO: Use the ViewModel
    }

}