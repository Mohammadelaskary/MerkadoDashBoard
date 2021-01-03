package com.example.elmohammadymarket.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elmohammadymarket.Model.OverTotalMoneyDiscount;
import com.example.elmohammadymarket.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.DiscountViewHolder> {
    Context context;
    List<OverTotalMoneyDiscount> discountList;
    ProgressDialog progressDialog;


    public DiscountAdapter(Context context, List<OverTotalMoneyDiscount> discountList) {
        this.context = context;
        this.discountList = discountList;
    }

    @NonNull
    @Override
    public DiscountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.discount_item, parent, false);
        return new DiscountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscountViewHolder holder, final int position) {
        holder.minimum.setText(discountList.get(position).getMinimum());
        holder.discount.setText(discountList.get(position).getDiscount());
        holder.discountType.setText(discountList.get(position).getDiscount_unit());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Loading....");
                progressDialog.show();
                progressDialog.setCancelable(false);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query query = ref.child("OverTotalMoneyDiscount").orderByChild("key").equalTo(discountList.get(position).getKey());
                query.getRef().removeValue();
                progressDialog.hide();
                removeAt(position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return discountList.size();
    }

    static class DiscountViewHolder extends RecyclerView.ViewHolder {
        TextView minimum, discount, discountType;
        ImageButton delete;

        public DiscountViewHolder(@NonNull View itemView) {
            super(itemView);
            minimum = itemView.findViewById(R.id.minimum);
            discount = itemView.findViewById(R.id.discount);
            discountType = itemView.findViewById(R.id.discount_type);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    private void removeAt(int position) {
        discountList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, discountList.size());
    }
}
