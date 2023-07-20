package com.example.idaon.mypage;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.collection.CircularArray;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idaon.BitmapConverter;
import com.example.idaon.HangulUtils;
import com.example.idaon.R;
import com.example.idaon.SaveSharedPreference;
import com.example.idaon.home.ShareActivity;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>{
    private final RecyclerViewInterface recyclerViewInterface;

    private Context context;

    static List<MyWriting> myWritings = new ArrayList<>();

    public MyRecyclerAdapter(Context context, List<MyWriting> myWritings, RecyclerViewInterface recyclerViewInterface) {
        this.recyclerViewInterface = recyclerViewInterface;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tv_title, tv_content;
        View mywriting;

        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            mywriting = itemView.findViewById(R.id.linearItem2);
            imageView = itemView.findViewById(R.id.cimage);
            tv_title = itemView.findViewById(R.id.ctitle);
            tv_content = itemView.findViewById(R.id.ccontent);

            itemView.setOnClickListener(view -> {
                if (recyclerViewInterface != null) {
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(pos);
                    }

                }
            });
        }

        public void setItem(MyWriting myWriting) {
            imageView.setImageBitmap(BitmapConverter.stringToBitmap(myWriting.getImg()));
            tv_title.setText(myWriting.getTitle());
            tv_content.setText(myWriting.getContent());
        }

    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.communityview, parent, false);
        return new ViewHolder(view, recyclerViewInterface);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if ((myWritings.get(position).getUid()).equals(SaveSharedPreference.getUserID(holder.itemView.getContext()))) {
            MyWriting myWriting = myWritings.get(position);
            holder.setItem(myWriting);
        } else {
            holder.itemView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mPosition = holder.getAdapterPosition();

                Context context = v.getContext();

                Intent intent = new Intent(context, ShareActivity.class);

                intent.putExtra("UID", myWritings.get(mPosition).getUid());
                intent.putExtra("NICKNAME", myWritings.get(mPosition).getUnickname());
                intent.putExtra("TITLE", myWritings.get(mPosition).getTitle());
                intent.putExtra("CONTENT", myWritings.get(mPosition).getContent());
                intent.putExtra("TIME", myWritings.get(mPosition).getTime());
                intent.putExtra("IMG", myWritings.get(mPosition).getImg());

                context.startActivity(intent);

            }
        });
    }

    public int getItemCount() {
        return (myWritings != null? myWritings.size() : 0);
    }

    public void setItems(ArrayList<MyWriting> myWritings) {
        this.myWritings = myWritings;
    }

    };



