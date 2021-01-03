package com.example.elmohammadymarket.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elmohammadymarket.R;
import com.example.elmohammadymarket.Views.PreviousOrders;

import java.util.List;

public class DatesAdapter extends RecyclerView.Adapter<DatesAdapter.DatesViewHolder> {
    List<String> dates;
    Context context;

    public DatesAdapter(List<String> dates, Context context) {
        this.dates = dates;
        this.context = context;
    }

    @NonNull
    @Override
    public DatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.date_item, parent, false);
        return new DatesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DatesViewHolder holder, int position) {
        final String date = dates.get(position);
        holder.dateText.setText(date);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PreviousOrders.class);
                intent.putExtra("date", date);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public class DatesViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;

        public DatesViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.date_text);
        }
    }
}
