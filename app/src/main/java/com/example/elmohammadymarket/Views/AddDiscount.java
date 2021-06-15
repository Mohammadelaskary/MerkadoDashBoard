package com.example.elmohammadymarket.Views;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.elmohammadymarket.Adapters.DiscountAdapter;
import com.example.elmohammadymarket.Model.OverTotalMoneyDiscount;
import com.example.elmohammadymarket.databinding.ActivityAddDiscountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddDiscount extends AppCompatActivity {
    ActivityAddDiscountBinding binding;
    List<OverTotalMoneyDiscount> discountList;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDiscountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        discountList = new ArrayList<>();

        final DiscountAdapter adapter = new DiscountAdapter(this, discountList);
        binding.currentDiscounts.setAdapter(adapter);
        binding.currentDiscounts.setLayoutManager(new LinearLayoutManager(this));



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("OverTotalMoneyDiscount");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    discountList.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        OverTotalMoneyDiscount moneyDiscount = snapshot1.getValue(OverTotalMoneyDiscount.class);
                        discountList.add(moneyDiscount);
                        adapter.notifyDataSetChanged();
                        binding.getDiscountsProgress.hide();
                        if (discountList.isEmpty())
                            binding.getDiscountsProgress.hide();

                    }
                } else {
                    binding.getDiscountsProgress.hide();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.addDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String minimum = binding.minimum.getEditText().getText().toString().trim();
                String discount = binding.discount.getEditText().getText().toString().trim();
                String discountType = binding.discountType.getSelectedItem().toString();
                progressDialog = new ProgressDialog(AddDiscount.this);
                progressDialog.setMessage("Loading....");
                progressDialog.show();
                progressDialog.setCancelable(false);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("OverTotalMoneyDiscount");
                String key = reference.push().getKey();
                OverTotalMoneyDiscount moneyDiscount = new OverTotalMoneyDiscount(minimum, discount, discountType, key);
                reference.push().setValue(moneyDiscount).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.hide();
                        binding.discount.getEditText().setText("");
                        binding.discount.getEditText().setText("");
                    }
                });
                discountList.add(moneyDiscount);
                adapter.notifyDataSetChanged();
            }
        });


    }
}