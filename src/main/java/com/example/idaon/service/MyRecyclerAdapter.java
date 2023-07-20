package com.example.idaon.service;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idaon.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> implements Filterable {
    private final RecyclerViewInterface recyclerViewInterface;

    static ArrayList<Service> items;

    public MyRecyclerAdapter(Context context, ArrayList<Service> items, RecyclerViewInterface recyclerViewInterface) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.items = items;
    }

    @NonNull
    public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.serviceview, parent, false);
        return new ViewHolder(view, recyclerViewInterface);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_servicename;

        public ViewHolder(@NonNull View serviceView, RecyclerViewInterface recyclerViewInterface) {
            super(serviceView);

            tv_servicename = serviceView.findViewById(R.id.servicename);

            serviceView.setOnClickListener(new View.OnClickListener() {
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

        public void setItem(Service service) {
            tv_servicename.setText(service.getServNm());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Service service = items.get(position);
        holder.setItem(service);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int mPosition = holder.getAdapterPosition();

                Context context = view.getContext();

                Intent intent = new Intent(context, ServiceDetailActivity.class);

                intent.putExtra("SERVICE_NAME", items.get(mPosition).getServNm());
                intent.putExtra("SERVICE_INGNUM", items.get(mPosition).getInqNum());
                intent.putExtra("SERVICE_JURMNOFNM", items.get(mPosition).getJurMnofNm());
                intent.putExtra("SERVICE_JURORGNM", items.get(mPosition).getJurOrgNm());
                intent.putExtra("SERVICE_LIFE", items.get(mPosition).getLifeArray());
                intent.putExtra("SERVICE_SERVDGST", items.get(mPosition).getServDgst());
                intent.putExtra("SERVICE_LINK", items.get(mPosition).getServDtlLink());
                intent.putExtra("SERVICE_ID", items.get(mPosition).getServId());
                intent.putExtra("SERVICE_NAME", items.get(mPosition).getServNm());
                intent.putExtra("SERVICE_SVCFRST", items.get(mPosition).getSvcfrstRegTs());
                intent.putExtra("SERVICE_TRGTERINDVD", items.get(mPosition).getTrgterIndvdlArray());

                context.startActivity(intent);
            }
        });
    }

    public int getItemCount() {
        return (items != null ? items.size() : 0);
    }

//    public void setItems(ArrayList<Service> services) {
//        this.items = services;
//    }

    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        //Automatic on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Service> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(items);
            } else {
                ArrayList<Service> filteringList = new ArrayList<>();
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Service item : items) {
                    //TODO filter 대상 setting
                    if (item.getServNm().toLowerCase().contains(filterPattern)) {
                        filteringList.add(item);
                    }
                }
                filteredList = filteringList;
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