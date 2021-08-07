package com.example.elmohammadymarket.Views;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.elmohammadymarket.Adapters.DepsNamesAdapter;
import com.example.elmohammadymarket.Database.DepartmentNames;
import com.example.elmohammadymarket.databinding.ActivityAddDepartmentBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddDepartment extends AppCompatActivity {
    ActivityAddDepartmentBinding binding;
    List<DepartmentNames> names;
    DatabaseReference ref;
    DepsNamesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDepartmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("أسماء الأقسام");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding.departmentName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.departmentName.setError(null);
                binding.departmentName.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.departmentName.setErrorEnabled(true);

            }
        });


        if (isConnected()) {
            binding.noInternetText.setVisibility(View.GONE);
            ref = FirebaseDatabase.getInstance().getReference("DepartmentsNames");
            names = new ArrayList<>();
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        binding.progressBar.hide();
                        binding.noDepsText.setVisibility(View.VISIBLE);
                        binding.depsRecycler.setVisibility(View.GONE);
                    } else {
                        names.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DepartmentNames depName = snapshot.getValue(DepartmentNames.class);
                            names.add(depName);
                            binding.progressBar.hide();
                            binding.addDepLayout.setVisibility(View.VISIBLE);
                            binding.noDeps.setVisibility(View.GONE);
                            binding.depsRecycler.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            binding.noInternetText.setVisibility(View.VISIBLE);
            binding.addDepLayout.setVisibility(View.GONE);
            binding.progressBar.hide();
        }


        adapter = new DepsNamesAdapter(names, this);
        binding.depsRecycler.setAdapter(adapter);
        binding.depsRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.depsRecycler.setHasFixedSize(true);

        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String depName = binding.departmentName.getEditText().getText().toString().trim();
                if (depName.isEmpty()) {
                    binding.departmentName.setError("اسم القسم لا يمكن أن يكون فارغا!");
                } else {

                    DepartmentNames departmentName = new DepartmentNames();
                    departmentName.setDepName(depName);
                    if (isFound(departmentName.getDepName())) {
                        binding.departmentName.setError("هذا القسم موجود بالفعل");
                    } else {
                        ref.push().setValue(departmentName);
                        names.add(departmentName);
                        adapter.notifyDataSetChanged();
                        binding.departmentName.getEditText().setText("");

                    }


                }
            }
        });


    }

    public boolean isConnected() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        return connected;
    }

    public boolean isFound(String departmentName) {
        for (DepartmentNames name : names) {
            if (name.getDepName().equals(departmentName)) {
                return true;
            }
        }
        return false;
    }


//    public boolean departmentNamefound (String depName){
//
//        boolean isfound = true;
//
//        if(names.isEmpty()){
//            isfound = false;
//        }else {
//            names = MainActivity.db.myDao().getAll();
//            for (DepartmentNames name : names) {
//                isfound = name.getDepName().equals(depName);
//            }
//        }
//        return isfound;
//    }


}