package com.example.idaon.home;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idaon.R;

import java.util.ArrayList;
import java.util.List;

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder>{
    static List<Comment> comments = new ArrayList<>();
    Context context;

    public ShareAdapter(Context context, List<Comment> comments) {
        this.comments = comments;
        this.context = context;
    }

    public void setItems(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cmt_comment, cmt_time, cmt_nickname;
        View cmt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cmt = itemView.findViewById(R.id.comment);

            cmt_nickname = itemView.findViewById(R.id.cmt_userid_tv);
            cmt_comment = itemView.findViewById(R.id.cmt_content_tv);
            cmt_time = itemView.findViewById(R.id.cmt_date_tv);
        }
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.commentview, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bundle bundle = ((Activity) context).getIntent().getExtras();
        final String title1 = bundle.getString("TITLE");

        if (title1.equals(comments.get(position).getTitle())) {
            holder.cmt_nickname.setText(comments.get(position).getNickname());
            holder.cmt_comment.setText(comments.get(position).getContent());
            holder.cmt_time.setText(comments.get(position).getTime());
        } else {
            holder.itemView.setVisibility(View.GONE);
        }
    }

    public int getItemCount() {
        return (comments != null? comments.size() : 0);
    }
}

