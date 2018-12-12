package com.movieapp.konwo.movieap.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.movieapp.konwo.movieap.R;
import com.movieapp.konwo.movieap.model.ReviewResults;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {
    private Context mContext;
    private List<ReviewResults> reviewResults;

    public ReviewAdapter(Context mContext, List<ReviewResults> reviewResults) {
        this.mContext = mContext;
        this.reviewResults = reviewResults;
    }

    @NonNull
    @Override
    public ReviewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_card, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.MyViewHolder myViewHolder, int i) {
        myViewHolder.title.setText(reviewResults.get(i).getAuthor());
        String url = reviewResults.get(i).getUrl();
        myViewHolder.url.setText(url);
        Linkify.addLinks(myViewHolder.url, Linkify.WEB_URLS);
    }

    @Override
    public int getItemCount() {
        return reviewResults.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, url;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.review_author);
            url = itemView.findViewById(R.id.review_link);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                }
            });
        }
    }

}
