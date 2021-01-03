package com.example.elmohammadymarket.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elmohammadymarket.Model.SubDeparment;
import com.example.elmohammadymarket.R;

import java.util.List;

public class SubDepAdapter extends RecyclerView.Adapter<SubDepAdapter.SubDepViewholder> {
    private Context context;
    private List<SubDeparment> list;

    public SubDepAdapter(Context context, List<SubDeparment> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SubDepViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subdep_item, parent, false);
        return new SubDepViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubDepViewholder holder, int position) {
        holder.subDepName.setText(list.get(position).getSubdepName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class SubDepViewholder extends RecyclerView.ViewHolder {
        TextView subDepName;

        public SubDepViewholder(@NonNull View itemView) {
            super(itemView);
            subDepName = itemView.findViewById(R.id.subdep_name);
        }
    }
}
