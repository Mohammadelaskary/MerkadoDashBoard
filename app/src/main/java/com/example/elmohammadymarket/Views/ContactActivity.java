package com.example.elmohammadymarket.Views;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.elmohammadymarket.Model.Contact;
import com.example.elmohammadymarket.databinding.ActivityContactBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Objects;

public class ContactActivity extends AppCompatActivity {
    ActivityContactBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getContactNumber();
        binding.phoneNumber.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.phoneNumber.setError("");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.phoneNumber.setError("");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String phoneNumber = binding.phoneNumber.getEditText().getText().toString().trim();
                if (phoneNumber.isEmpty())
                    binding.phoneNumber.setError("رقم الهاتف لا يمكن أن يكون فارغ");
                else
                    binding.phoneNumber.setError("");
            }
        });
        binding.changeContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = binding.phoneNumber.getEditText().getText().toString().trim();
                if (phoneNumber.isEmpty())
                    binding.phoneNumber.setError("رقم الهاتف لا يمكن أن يكون فارغ");
                else
                    binding.phoneNumber.setError("");

                if (!phoneNumber.isEmpty())
                    uploadContact(phoneNumber);
            }
        });
    }

    private void uploadContact(String phoneNumber) {
        Contact contact = new Contact(phoneNumber);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Contact");
        reference.setValue(contact).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FancyToast.makeText(ContactActivity.this, "تم تغيير رقم التليفون", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                    startActivity(new Intent(ContactActivity.this, MainActivity.class));

                }

            }
        });
    }

    private void getContactNumber() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Contact");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.progressBar.hide();
                Contact contact = snapshot.getValue(Contact.class);
                if (contact!=null)
                    Objects.requireNonNull(binding.phoneNumber.getEditText()).setText(contact.getPhoneNumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}