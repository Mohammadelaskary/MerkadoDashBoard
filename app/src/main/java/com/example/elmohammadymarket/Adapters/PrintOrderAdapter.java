package com.example.elmohammadymarket.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elmohammadymarket.Model.OrderProduct;
import com.example.elmohammadymarket.R;
import com.example.elmohammadymarket.databinding.OrderItemBinding;
import com.example.elmohammadymarket.databinding.PrintOrderItemBinding;
import com.nineoldandroids.view.ViewHelper;

import java.util.List;

public class PrintOrderAdapter extends RecyclerView.Adapter<PrintOrderAdapter.PrintViewHolder> {
    private List<OrderProduct> orderProducts;

    public PrintOrderAdapter(List<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    @NonNull
    @Override
    public PrintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PrintViewHolder(PrintOrderItemBinding.inflate(LayoutInflater.from(parent.getContext())));
    }

    @Override
    public void onBindViewHolder(@NonNull PrintViewHolder holder, int position) {

            String productName   = orderProducts.get(position).getProductName();
            String originalPrice = orderProducts.get(position).getOriginalPrice();
            String finalPrice    = orderProducts.get(position).getFinalPrice();
            String orderedAmount = "x"+orderProducts.get(position).getOrdered();
            String total         = orderProducts.get(position).getTotalCost();

            holder.binding.productName  .setText(productName);
            holder.binding.originalPrice.setText(originalPrice);
            holder.binding.finalPrice   .setText(finalPrice);
            holder.binding.orderAmount  .setText(orderedAmount);
            holder.binding.total        .setText(total);
    }

    @Override
    public int getItemCount() {
        return orderProducts.size();
    }

    static class PrintViewHolder extends RecyclerView.ViewHolder{
        PrintOrderItemBinding binding;
        public PrintViewHolder(@NonNull PrintOrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
