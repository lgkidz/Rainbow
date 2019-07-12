package com.OdiousPanda.thefweather.Adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class locationListAdapter extends RecyclerView.Adapter<locationListAdapter.locationItemViewHolder> {

    @NonNull
    @Override
    public locationItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull locationItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class locationItemViewHolder extends RecyclerView.ViewHolder{

        public locationItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
