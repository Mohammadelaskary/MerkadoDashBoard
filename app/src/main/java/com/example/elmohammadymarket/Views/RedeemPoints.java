package com.example.elmohammadymarket.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elmohammadymarket.Model.PointsDiscount;
import com.example.elmohammadymarket.databinding.ActivityRedeemPointsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class RedeemPoints extends AppCompatActivity {
    ActivityRedeemPointsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRedeemPointsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getPointsDiscount();

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean freeShipping = binding.freeShipping.isChecked();
                int numberOfPoints;
                float discountValue = 0;

                String numberOfPointsText = Objects.requireNonNull(binding.minPoints.getEditText()).getText().toString().trim();
                if (numberOfPointsText.isEmpty()) {
                    binding.minPoints.setError("ادخل عدد النقاط");
                } else {
                    if (binding.discount.isChecked()) {
                        String discountValueText = binding.discountValue.getText().toString().trim();
                        if (discountValueText.isEmpty()) {
                            Toast.makeText(RedeemPoints.this, "من فضلك أدخل قيمة الخصم", Toast.LENGTH_SHORT).show();
                        } else {
                            discountValue = Float.parseFloat(discountValueText);
                            numberOfPoints = Integer.parseInt(numberOfPointsText);
                            PointsDiscount pointsDiscount = new PointsDiscount(freeShipping, numberOfPoints, discountValue);
                            uploadDiscount(pointsDiscount);
                        }
                    } else {
                        discountValue = 0;
                        numberOfPoints = Integer.parseInt(numberOfPointsText);
                        PointsDiscount pointsDiscount = new PointsDiscount(freeShipping, numberOfPoints, discountValue);
                        uploadDiscount(pointsDiscount);
                    }

                }
            }
        });
    }

    private void getPointsDiscount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PointsDiscount");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.progressBar.hide();
                if (snapshot.exists()) {
                    PointsDiscount pointsDiscount = snapshot.getValue(PointsDiscount.class);
                    assert pointsDiscount != null;
                    Objects.requireNonNull(binding.minPoints.getEditText()).setText(String.valueOf(pointsDiscount.getNumberOfPoints()));
                    binding.freeShipping.setChecked(pointsDiscount.isFreeShipping());
                    binding.discount.setChecked(pointsDiscount.getNumberOfPoints() != 0);
                    if (pointsDiscount.getNumberOfPoints() == 0)
                        binding.discountValue.setText("");
                    else
                        binding.discountValue.setText(String.valueOf(pointsDiscount.getDiscountValue()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadDiscount(PointsDiscount pointsDiscount) {
        binding.progressBar.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PointsDiscount");
        reference.setValue(pointsDiscount).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    binding.progressBar.hide();
                    Toast.makeText(RedeemPoints.this, "تم إرسال الخصم", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RedeemPoints.this, MainActivity.class));
                    finish();
                }

            }
        });
    }
}