package com.example.elmohammadymarket.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elmohammadymarket.Model.OrderProduct;
import com.example.elmohammadymarket.databinding.PrintOrderItemBinding;
import com.nineoldandroids.view.ViewHelper;

import java.util.List;

public class PrintOrderAdapter extends RecyclerView.Adapter<PrintOrderAdapter.PrintOrderViewHolder> {
    private List<OrderProduct> orderProducts;

    public PrintOrderAdapter(List<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    @NonNull
    @Override
    public PrintOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PrintOrderViewHolder(PrintOrderItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PrintOrderViewHolder holder, int position) {
        OrderProduct orderProduct = orderProducts.get(position);
        String productName   = orderProduct.getProductName();
        String originalPrice = orderProduct.getOriginalPrice();
        String finalPrice    = orderProduct.getFinalPrice();
        String orderedAmount = String.valueOf(orderProduct.getOrdered())+"x";
        String total         = orderProduct.getTotalCost();
        holder.binding.productName.setText(productName);
        holder.binding.originalPrice.setText(originalPrice);
        holder.binding.finalPrice.setText(finalPrice);
        holder.binding.orderAmount.setText(orderedAmount);
        holder.binding.total.setText(total);
    }

    @Override
    public int getItemCount() {
        return orderProducts==null?0:orderProducts.size();
    }

    static class PrintOrderViewHolder extends RecyclerView.ViewHolder {
        PrintOrderItemBinding binding;
        public PrintOrderViewHolder(@NonNull PrintOrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
