package com.example.elmohammadymarket.Views;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.elmohammadymarket.Adapters.SubDepAdapter;
import com.example.elmohammadymarket.Database.DepartmentNames;
import com.example.elmohammadymarket.Model.SubDeparment;
import com.example.elmohammadymarket.R;
import com.example.elmohammadymarket.databinding.ActivityAddSubDepartmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class AddSubDepartment extends AppCompatActivity {
    ActivityAddSubDepartmentBinding binding;
    SubDepAdapter adapter;
    List<DepartmentNames> depsNames;
    List<SubDeparment> list;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSubDepartmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.subdepName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.subdepName.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.subdepName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String depName = binding.depName.getSelectedItem().toString();
                String subDep = binding.subdepName.getEditText().getText().toString().trim();

                if (binding.subdepName.getEditText().getText().toString().isEmpty()) {
                    binding.subdepName.setError("ادخل اسم القسم الداخلي");

                } else
                    binding.subdepName.setError(null);
            }
        });
        list = new ArrayList<>();
        depsNames = new ArrayList<>();
        adapter = new SubDepAdapter(AddSubDepartment.this, list);
        binding.subdeps.setAdapter(adapter);
        binding.subdeps.setLayoutManager(new LinearLayoutManager(this));
        new GetDepsAndProducts().execute();
        adapter = new SubDepAdapter(AddSubDepartment.this, list);
        binding.subdeps.setAdapter(adapter);
        binding.subdeps.setLayoutManager(new LinearLayoutManager(AddSubDepartment.this));
        binding.depName.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String depName = binding.depName.getSelectedItem().toString();
                        binding.depNameText.setText(depName);
                        new GetAllsubDeps().execute(depName);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        String depName = binding.depName.getSelectedItem().toString();
                        binding.depNameText.setText(depName);
                        new GetAllsubDeps().execute(depName);

                    }
                });


        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String depName = binding.depName.getSelectedItem().toString();
                String subDep = binding.subdepName.getEditText().getText().toString().trim();
                if (depName.isEmpty()) {
                    FancyToast.makeText(getApplicationContext(), "اختر القسم أولا", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                }
                if (subDep.isEmpty()) {
                    binding.subdepName.setError("ادخل اسم القسم الداخلي");
                }
                List<String> subList = new ArrayList<>();
                for (SubDeparment subDeparment1 : list) {
                    subList.add(subDeparment1.getSubdepName());
                }
                if (subList.contains(subDep))
                    binding.subdepName.setError("اسم القسم الداخلي مستخدم من قبل");
                if (!depName.isEmpty() && !subDep.isEmpty() && !subList.contains(subDep)) {
                    progressDialog = new ProgressDialog(AddSubDepartment.this);
                    progressDialog.setMessage("Loading....");
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SubDeps");
                    final SubDeparment subDeparment = new SubDeparment(depName, subDep);
                    reference.push().setValue(subDeparment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.hide();
                            FancyToast.makeText(getApplicationContext(), "تم إضافة القسم الفرعي", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                        }
                    });
                }

            }
        });

    }

    public class GetDepsAndProducts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.addSubdepLayout.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getdepartmentNames();
            return null;
        }
    }

    public void getdepartmentNames() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DepartmentsNames");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    if (depsNames.isEmpty()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddSubDepartment.this);
                        builder.setMessage("من فضلك أدخل اسماء الأقسام أولا.....")
                                .setCancelable(false)
                                .setPositiveButton("حسنا", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        startActivity(new Intent(AddSubDepartment.this, AddDepartment.class));
                                        finish();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        binding.progressBarDep.hide();
                    }
                } else {
                    depsNames.clear();
                    binding.progressBarDep.hide();
                    binding.addSubdepLayout.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DepartmentNames depName = snapshot.getValue(DepartmentNames.class);
                        depsNames.add(depName);
                    }

                    List<String> depNames = new ArrayList<>();
                    for (int i = 0; i < depsNames.size(); i++) {
                        depNames.add(depsNames.get(i).getDepName());
                    }

                    ArrayAdapter<String> depsAdapter = new ArrayAdapter<>(AddSubDepartment.this, R.layout.dep_spinner_style, depNames);
                    depsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.depName.setAdapter(depsAdapter);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                FancyToast.makeText(getApplicationContext(), databaseError.getMessage()
                        , FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();

            }
        });

    }

    public class GetAllsubDeps extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            getSubDeps(strings[0]);

            return null;
        }


    }

    private void getSubDeps(String depName) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("SubDeps").orderByChild("depName").equalTo(depName);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if (!snapshot.exists())
                    binding.noSubdepsFound.setVisibility(View.VISIBLE);
                else {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        SubDeparment subDeparment = snapshot1.getValue(SubDeparment.class);
                        list.add(subDeparment);
                        adapter.notifyDataSetChanged();
                        binding.noSubdepsFound.setVisibility(View.GONE);
                    }
                    binding.progressBarDep.hide();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}