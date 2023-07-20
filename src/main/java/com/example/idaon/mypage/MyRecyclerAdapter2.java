package com.example.idaon.mypage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idaon.BitmapConverter;
import com.example.idaon.R;
import com.example.idaon.SaveSharedPreference;
import com.example.idaon.item.DetailActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MyRecyclerAdapter2 extends RecyclerView.Adapter<MyRecyclerAdapter2.ViewHolder> {
    private final RecyclerViewInterface2 recyclerViewInterface;

    private Context context;

    static List<MySell> mySells = new ArrayList<>();

    public MyRecyclerAdapter2(Context context, List<MySell> mySells, RecyclerViewInterface2 recyclerViewInterface) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.context = context;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tv_name, tv_price;
        View mysell;

        public ViewHolder(@NonNull View itemView, RecyclerViewInterface2 recyclerViewInterface) {
            super(itemView);

            mysell = itemView.findViewById(R.id.linearItem);
            imageView = itemView.findViewById(R.id.itemimage);
            tv_name = itemView.findViewById(R.id.itemname);
            tv_price = itemView.findViewById(R.id.itemprice);

            itemView.setOnClickListener(view -> {
                if (recyclerViewInterface != null) {
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(pos);
                    }

                }
            });
        }
        DecimalFormat decFormat = new DecimalFormat("###,###");

        public void setItem(MySell mySell) {
            imageView.setImageBitmap(BitmapConverter.stringToBitmap(mySell.getImg()));
            tv_name.setText(mySell.getIname());
            tv_price.setText(decFormat.format(Integer.parseInt(mySell.getPrice())) + "원");
        }

    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.itemview, parent, false);
        return new ViewHolder(view, recyclerViewInterface);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if ((mySells.get(position).getUid()).equals(SaveSharedPreference.getUserID(holder.itemView.getContext()))) {
            MySell mySell = mySells.get(position);
            holder.setItem(mySell);
        } else {
            holder.itemView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mPosition = holder.getAdapterPosition();

                Context context = v.getContext();

                Intent intent = new Intent(context, DetailActivity.class);

                intent.putExtra("UID", mySells.get(mPosition).getUid());
                intent.putExtra("NICKNAME", mySells.get(mPosition).getUnickname());
                intent.putExtra("NAME", mySells.get(mPosition).getIname());
                intent.putExtra("PRICE", mySells.get(mPosition).getPrice());
                intent.putExtra("CONTENT", mySells.get(mPosition).getContent());
                intent.putExtra("TIME", mySells.get(mPosition).getTime());
                intent.putExtra("IMG", mySells.get(mPosition).getImg());

                context.startActivity(intent);

            }
        });
    }

    public int getItemCount() {
        return (mySells != null? mySells.size() : 0);
    }

    public void setItems(ArrayList<MySell> mySells) {
        this.mySells = mySells;
    }

};



