package com.example.idaon.item;

import android.content.Context;
import android.content.Intent;
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
import com.example.idaon.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> implements Filterable {
    private final RecyclerViewInterface recyclerViewInterface;

    static List<Item> items;

    public MyRecyclerAdapter(Context context, List<Item> items, RecyclerViewInterface recyclerViewInterface) {
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tv_title, tv_price;

        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            imageView = itemView.findViewById(R.id.itemimage);
            tv_title = itemView.findViewById(R.id.itemname);
            tv_price = itemView.findViewById(R.id.itemprice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }

                    }
                }
            });
        }

        DecimalFormat decFormat = new DecimalFormat("###,###");

        public void setItem(Item item) {
            imageView.setImageBitmap(BitmapConverter.stringToBitmap(item.getImg()));
            tv_title.setText(item.getIname());
            tv_price.setText(decFormat.format(Integer.parseInt(item.getPrice())) + "원");
        }
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.itemview, parent, false);
        return new ViewHolder(view, recyclerViewInterface);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.setItem(item);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mPosition = holder.getAdapterPosition();

                Context context = v.getContext();

                Intent intent = new Intent(context, DetailActivity.class);

                intent.putExtra("UID", items.get(mPosition).getUid());
                intent.putExtra("NICKNAME", items.get(mPosition).getUnickname());
                intent.putExtra("NAME", items.get(mPosition).getIname());
                intent.putExtra("PRICE", items.get(mPosition).getPrice());
                intent.putExtra("CONTENT", items.get(mPosition).getContent());
                intent.putExtra("TIME", items.get(mPosition).getTimestamp());
                intent.putExtra("IMG", items.get(mPosition).getImg());

                context.startActivity(intent);

            }
        });
    }

    public int getItemCount() {
        return (items != null? items.size() : 0);
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        //Automatic on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Item> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(items);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Item item : items) {
                    //TODO filter 대상 setting
                    if (item.getIname().toLowerCase().contains(filterPattern)) {
                        filteredList.add(0, item);
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
            items.clear();
            items.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}