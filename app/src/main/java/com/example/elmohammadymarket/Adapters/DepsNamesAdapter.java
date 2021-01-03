package com.example.elmohammadymarket.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elmohammadymarket.Database.DepartmentNames;
import com.example.elmohammadymarket.R;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class DepsNamesAdapter extends RecyclerView.Adapter<DepsNamesAdapter.DepsNamesViewHolder> {
    private List<DepartmentNames> names;
    private Context context;
    DatabaseReference ref;


    public DepsNamesAdapter(List<DepartmentNames> names, Context context) {
        this.names = names;
        this.context = context;
    }

    @NonNull
    @Override
    public DepsNamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.department_name_item, parent, false);
        return new DepsNamesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DepsNamesViewHolder holder, final int position) {
        String depName = names.get(position).getDepName();
        holder.departmentName.setText(depName);
        Log.d("-------mido", names.get(position).getDepName());


    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    static class DepsNamesViewHolder extends RecyclerView.ViewHolder {
        TextView departmentName;

        public DepsNamesViewHolder(@NonNull View itemView) {
            super(itemView);
            departmentName = itemView.findViewById(R.id.dep_name);


        }
    }

}
