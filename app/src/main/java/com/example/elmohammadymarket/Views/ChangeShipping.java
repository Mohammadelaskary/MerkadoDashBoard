package com.example.elmohammadymarket.Views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elmohammadymarket.Model.Shipping;
import com.example.elmohammadymarket.databinding.ActivityChangeShippingBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ChangeShipping extends AppCompatActivity {
    ActivityChangeShippingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeShippingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getCurrentShippingFee();
        Objects.requireNonNull(binding.shippingFee.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.shippingFee.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.shippingFee.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String shippingFee = binding.shippingFee.getEditText().getText().toString().trim();
                if (shippingFee.isEmpty())
                    binding.shippingFee.setError("ادخل مصاريف الشحن");

            }
        });
        binding.changeShippingFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shippingFee = Objects.requireNonNull(binding.shippingFee.getEditText()).getText().toString();
                if (shippingFee.isEmpty())
                    binding.shippingFee.setError("ادخل مصاريف الشحن");
                else {
                    Shipping shipping = new Shipping();
                    shipping.setShippingFee(Float.parseFloat(shippingFee));
                    uploadShippingFee(shipping);
                }
            }
        });

    }

    private void uploadShippingFee(Shipping shipping) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Shipping");
        reference.setValue(shipping);
        String shippingText = shipping.getShippingFee() + " جنيه";
        binding.currentShippingFee.setText(shippingText);
    }

    private void getCurrentShippingFee() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Shipping");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Shipping shipping = snapshot.getValue(Shipping.class);
                    assert shipping != null;
                    binding.currentShippingFee.setText(String.valueOf(shipping.getShippingFee()));
                    binding.currentShippingFee.setVisibility(View.VISIBLE);
                } else {
                    binding.currentShippingFee.setText("10 جنيه");
                    binding.currentShippingFee.setVisibility(View.VISIBLE);
                }
                binding.loadingShippingFee.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}