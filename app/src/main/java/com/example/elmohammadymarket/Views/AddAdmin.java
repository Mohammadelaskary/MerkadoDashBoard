package com.example.elmohammadymarket.Views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elmohammadymarket.Model.Admin;
import com.example.elmohammadymarket.databinding.ActivityAddAdminBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

public class AddAdmin extends AppCompatActivity {
    ActivityAddAdminBinding binding;
    private FirebaseAuth mAuth;
    Admin admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        binding.username.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.username.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.username.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String email = binding.username.getEditText().getText().toString().trim();
                if (email.isEmpty()) {
                    binding.username.setError("ادخل الايميل");
                } else {
                    binding.username.setError(null);
                }
            }
        });

        binding.email.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.email.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.email.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String email = binding.email.getEditText().getText().toString().trim();
                if (email.isEmpty()) {
                    binding.email.setError("ادخل الايميل");
                } else {
                    binding.email.setError(null);
                }
            }
        });
        binding.password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.password.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String password = binding.password.getEditText().getText().toString().trim();
                if (password.isEmpty()) {
                    binding.password.setError("ادخل كلمة المرور");
                } else {
                    binding.password.setError(null);
                }
            }
        });

        binding.passwordConfirm.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.passwordConfirm.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.passwordConfirm.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String password = binding.password.getEditText().getText().toString().trim();
                String passwordConfirm = binding.passwordConfirm.getEditText().getText().toString().trim();
                if (passwordConfirm.isEmpty()) {
                    binding.passwordConfirm.setError("ادخل كلمة المرور مرة أخري للتأكيد");
                } else {
                    if (!passwordConfirm.equals(password))
                        binding.passwordConfirm.setError("كلمتي المرور غير متماثلتين");
                    else
                        binding.passwordConfirm.setError(null);
                }
            }
        });
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.email.getEditText().getText().toString().trim();
                String password = binding.password.getEditText().getText().toString().trim();
                String passwordConfirm = binding.passwordConfirm.getEditText().getText().toString().trim();
                String username = binding.username.getEditText().getText().toString().trim();
                if (email.isEmpty())
                    binding.email.setError("ادخل الايميل");
                else if (password.isEmpty())
                    binding.password.setError("ادخل كلمة المرور");
                else if (passwordConfirm.isEmpty())
                    binding.passwordConfirm.setError("ادخل كلمة المرور مرة أخري");

                if (!email.isEmpty() && !password.isEmpty() && !passwordConfirm.isEmpty() && !username.isEmpty()) {
                    signup(email, password);

                }
            }
        });
    }

    private void signup(final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(AddAdmin.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FancyToast.makeText(getApplicationContext(), "تم إضافة الأدمن"
                                    , FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                            String username = binding.username.getEditText().getText().toString().trim();
                            admin = new Admin(username, email, " ", " ");
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Admins");
                            reference.child(FirebaseAuth.getInstance().getUid()).setValue(admin);
                        } else
                            FancyToast.makeText(getApplicationContext(), task.getException().getMessage()
                                    , FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    }
                });
    }
}