package com.example.elmohammadymarket.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
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
        getPointsDiscount2();
        binding.freeShipping1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                binding.discount1.setChecked(!b);
                binding.discountValue2.setEnabled(!b);
            }
        });
        binding.discount1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                binding.freeShipping1.setChecked(!b);
                binding.discountValue1.setEnabled(b);
            }
        });
        binding.freeShipping2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                binding.discount2.setChecked(!b);
                binding.discountValue2.setEnabled(!b);
            }
        });
        binding.discount2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                binding.freeShipping2.setChecked(!b);
                binding.discountValue2.setEnabled(b);
            }
        });
        binding.send1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean freeShipping = binding.freeShipping1.isChecked();
                int numberOfPoints;
                float discountValue = 0;

                String numberOfPointsText = Objects.requireNonNull(binding.minPoints1.getEditText()).getText().toString().trim();
                if (numberOfPointsText.isEmpty()) {
                    binding.minPoints1.setError("ادخل عدد النقاط");
                } else {
                    if (binding.discount1.isChecked()) {
                        String discountValueText = binding.discountValue1.getText().toString().trim();
                        if (discountValueText.isEmpty()) {
                            Toast.makeText(RedeemPoints.this, "من فضلك أدخل قيمة الخصم", Toast.LENGTH_SHORT).show();
                        } else {
                            discountValue = Float.parseFloat(discountValueText);
                            numberOfPoints = Integer.parseInt(numberOfPointsText);
                            PointsDiscount pointsDiscount = new PointsDiscount(freeShipping, numberOfPoints, String.valueOf(discountValue));
                            uploadDiscount(pointsDiscount);
                        }
                    } else {
                        discountValue = 0;
                        numberOfPoints = Integer.parseInt(numberOfPointsText);
                        PointsDiscount pointsDiscount = new PointsDiscount(freeShipping, numberOfPoints, String.valueOf(discountValue));
                        uploadDiscount(pointsDiscount);
                    }

                }
            }
        });
        binding.send2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean freeShipping = binding.freeShipping2.isChecked();
                int numberOfPoints;
                float discountValue = 0;

                String numberOfPointsText = Objects.requireNonNull(binding.minPoints2.getEditText()).getText().toString().trim();
                if (numberOfPointsText.isEmpty()) {
                    binding.minPoints2.setError("ادخل عدد النقاط");
                } else {
                    if (binding.discount2.isChecked()) {
                        String discountValueText = binding.discountValue2.getText().toString().trim();
                        if (discountValueText.isEmpty()) {
                            Toast.makeText(RedeemPoints.this, "من فضلك أدخل قيمة الخصم", Toast.LENGTH_SHORT).show();
                        } else {
                            discountValue = Float.parseFloat(discountValueText);
                            numberOfPoints = Integer.parseInt(numberOfPointsText);
                            PointsDiscount pointsDiscount = new PointsDiscount(freeShipping, numberOfPoints, String.valueOf(discountValue));
                            uploadDiscount2(pointsDiscount);
                        }
                    } else {
                        discountValue = 0;
                        numberOfPoints = Integer.parseInt(numberOfPointsText);
                        PointsDiscount pointsDiscount = new PointsDiscount(freeShipping, numberOfPoints, String.valueOf(discountValue));
                        uploadDiscount2(pointsDiscount);
                    }

                }
            }
        });

        binding.remove1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               removeDiscount1();
            }
        });

        binding.remove2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeDiscount2();
            }
        });
    }

    private void removeDiscount1() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PointsDiscount");
        reference.setValue(null);
        binding.minPoints1.getEditText().setText("");
        binding.freeShipping1.setChecked(false);
        binding.discount1.setChecked(false);
        binding.discountValue1.setText("");
    }
    private void removeDiscount2() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PointsDiscount2");
        reference.setValue(null);
        binding.minPoints2.getEditText().setText("");
        binding.freeShipping2.setChecked(false);
        binding.discount2.setChecked(false);

        binding.discountValue2.setText("");
    }

    private void getPointsDiscount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PointsDiscount");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.progressBar.hide();
                if (snapshot.exists()) {
                    PointsDiscount pointsDiscount = snapshot.getValue(PointsDiscount.class);
                    boolean freeShipping = pointsDiscount.isFreeShipping();
                    String discountValue  = pointsDiscount.getDiscountValue();
                    int pointsNumber     = pointsDiscount.getNumberOfPoints();
                    binding.minPoints1.getEditText().setText(String.valueOf(pointsNumber));
                    binding.freeShipping1.setChecked(freeShipping);
                    binding.discount1.setChecked(!freeShipping);
                    binding.discount1.setChecked(!freeShipping);
                    if (Float.parseFloat(discountValue) !=0)
                        binding.discountValue1.setText(String.valueOf(discountValue));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getPointsDiscount2() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PointsDiscount2");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.progressBar.hide();
                if (snapshot.exists()) {
                    PointsDiscount pointsDiscount = snapshot.getValue(PointsDiscount.class);
                    boolean freeShipping = pointsDiscount.isFreeShipping();
                    String discountValue  = pointsDiscount.getDiscountValue();
                    int pointsNumber     = pointsDiscount.getNumberOfPoints();
                    binding.minPoints2.getEditText().setText(String.valueOf(pointsNumber));
                    binding.freeShipping2.setChecked(freeShipping);
                    binding.discount2.setChecked(!freeShipping);
                    binding.discount2.setChecked(!freeShipping);
                    if (Float.parseFloat(discountValue) !=0)
                    binding.discountValue2.setText(String.valueOf(discountValue));
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
    private void uploadDiscount2(PointsDiscount pointsDiscount) {
        binding.progressBar.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PointsDiscount2");
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