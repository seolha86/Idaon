package com.example.idaon.home;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idaon.BitmapConverter;
import com.example.idaon.HangulUtils;
import com.example.idaon.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> implements Filterable {
    private final RecyclerViewInterface recyclerViewInterface;

    static List<Home> homes = new ArrayList<>();


    public MyRecyclerAdapter(Context context, List<Home> homes, RecyclerViewInterface recyclerViewInterface) {
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tv_title, tv_content;

        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            imageView = itemView.findViewById(R.id.cimage);
            tv_title = itemView.findViewById(R.id.ctitle);
            tv_content = itemView.findViewById(R.id.ccontent);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }

                    }
                }
            });
        }

        public void setItem(Home home) {
            if (home.getImg() != null) {
                imageView.setImageBitmap(BitmapConverter.stringToBitmap(home.getImg()));
            } else {
                imageView.setVisibility(View.GONE);
            }

            tv_title.setText(home.getTitle());
            tv_content.setText(home.getContent());
        }
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.communityview, parent, false);

        return new ViewHolder(view, recyclerViewInterface);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Home home = homes.get(position);
        holder.setItem(home);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mPosition = holder.getAdapterPosition();

                Context context = v.getContext();

                Intent intent = new Intent(context, ShareActivity.class);

                intent.putExtra("USER_ID", homes.get(mPosition).getUid());
                intent.putExtra("NICKNAME", homes.get(mPosition).getUnickname());
                intent.putExtra("TITLE", homes.get(mPosition).getTitle());
                intent.putExtra("CONTENT", homes.get(mPosition).getContent());
                intent.putExtra("TIME", homes.get(mPosition).getTime());
                intent.putExtra("IMG", homes.get(mPosition).getImg());

                context.startActivity(intent);

            }
        });
    }

    public int getItemCount() {
        return (homes != null ? homes.size() : 0);
    }

    public void setItems(ArrayList<Home> homes) {
        this.homes = homes;
    }

    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {

        //Automatic on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Home> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(homes);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Home home : homes) {
                    //TODO filter 대상 setting
                    String inName = HangulUtils.getHangulInitialSound(home.getTitle(), filterPattern); // 추가 (초성검색)
                    if (inName.indexOf(filterPattern) >= 0) {
                        filteredList.add(home);
                        Log.d("constraint", String.valueOf(constraint));
                        Log.d("title", home.getTitle());
                    } else if (home.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(home);
//                    if (home.getTitle().toLowerCase().contains(filterPattern)) {
//                        filteredList.add(home);
//                        Log.d("constraint", String.valueOf(constraint));
//                        Log.d("title", home.getTitle());
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        //Automatic on UI thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            homes.clear();
            homes.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}



