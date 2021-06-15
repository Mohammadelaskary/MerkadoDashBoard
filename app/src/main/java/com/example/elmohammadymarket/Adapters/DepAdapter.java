package com.example.elmohammadymarket.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elmohammadymarket.Model.Department;
import com.example.elmohammadymarket.R;

import java.util.List;

public class DepAdapter extends RecyclerView.Adapter<DepAdapter.DepsViewholder> {
    Context context;
    List<Department> depList;
    RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();


    public DepAdapter(Context context, List<Department> depList) {
        this.context = context;
        this.depList = depList;
    }

    @NonNull
    @Override
    public DepsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_products_with_deps_names, parent, false);
        return new DepsViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DepsViewholder holder, int position) {
        String departName = depList.get(position).getDepName();
        Department dep = depList.get(position);
        holder.depName.setText(departName);
        ProductsAdapter productsAdapter = new ProductsAdapter(context, depList.get(position).getProductList());
        holder.main_recycler_view.setAdapter(productsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.setInitialPrefetchItemCount(dep.getProductList().size());
        holder.main_recycler_view.setRecycledViewPool(viewPool);
        holder.main_recycler_view.setLayoutManager(linearLayoutManager);


    }

    @Override
    public int getItemCount() {
        return depList.size();
    }


    static class DepsViewholder extends RecyclerView.ViewHolder {
        TextView depName;
        RecyclerView main_recycler_view;

        public DepsViewholder(@NonNull View itemView) {
            super(itemView);
            depName = itemView.findViewById(R.id.dep_name);
            main_recycler_view = itemView.findViewById(R.id.dep_recycler_view);
        }


    }
}
