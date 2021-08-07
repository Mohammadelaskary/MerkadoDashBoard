package com.example.elmohammadymarket.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.elmohammadymarket.Model.Admin;
import com.example.elmohammadymarket.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Objects.requireNonNull(getSupportActionBar()).setTitle("تسجيل دخول");

        mAuth = FirebaseAuth.getInstance();


        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = binding.email.getEditText().getText().toString().trim();
                String password = binding.password.getEditText().getText().toString().trim();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    FancyToast.makeText(getApplicationContext(), "من فضلك ادخل الايميل وكلمة المرور! ", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();

                } else {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Loading....");
                    progressDialog.show();
                    progressDialog.setCancelable(false);

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        getUsername(email);
//                                        storeToken(getAdminToken(),email);
                                        progressDialog.hide();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.putExtra("email", email);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {

                                        Log.d("loginError", Objects.requireNonNull(task.getException().getMessage()));
                                        FancyToast.makeText(getApplicationContext(), "الايميل او كلمة المرور غير صحيح!", FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                                        progressDialog.hide();
                                    }
                                }
                            });
                }
            }
        });

        binding.forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailAddress = binding.email.getEditText().getText().toString().trim();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (emailAddress.isEmpty())
                    binding.email.setError("ادخل بريدك الالكتروني");
                else {
                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FancyToast.makeText(getApplicationContext(), "ستصلك رسالة علي بريدك الالكتروني لتغيير كلمة المرور", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();

                                    }
                                }
                            });

                }
            }
        });
    }

    private boolean emailIsAdmin(final String email) {
        final List<String> adminsEmails = new ArrayList<>();
        final boolean[] isFound = {false};
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Admins");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adminsEmails.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Admin admin = dataSnapshot.getValue(Admin.class);
                    adminsEmails.add(admin.getEmail());
                }
                if (adminsEmails.contains(email))
                    isFound[0] = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return isFound[0];
    }

    private void getUsername(final String email) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Admins");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Admin admin = snapshot1.getValue(Admin.class);
                    if (admin.getEmail().equals(email))
                        FancyToast.makeText(getApplicationContext(), "مرحبا " + admin.getUsername(), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        Log.d("username",username[0]);
    }

}