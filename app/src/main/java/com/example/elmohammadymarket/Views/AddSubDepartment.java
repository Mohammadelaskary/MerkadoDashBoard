package com.example.elmohammadymarket.Views;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.elmohammadymarket.Adapters.SubDepAdapter;
import com.example.elmohammadymarket.Database.DepartmentNames;
import com.example.elmohammadymarket.Model.SubDeparment;
import com.example.elmohammadymarket.R;
import com.example.elmohammadymarket.databinding.ActivityAddSubDepartmentBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddSubDepartment extends AppCompatActivity {
    private static final int IMAGE_REQUEST = 100;
    ActivityAddSubDepartmentBinding binding;
    SubDepAdapter adapter;
    List<DepartmentNames> depsNames;
    List<SubDeparment> list;
    ProgressDialog progressDialog;
    Uri imageUri;
    String imageFileName,imageUrl;
    StorageReference storage;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSubDepartmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        storage = FirebaseStorage.getInstance().getReference("Images");
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

        binding.addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
        list = new ArrayList<>();
        depsNames = new ArrayList<>();
        adapter = new SubDepAdapter(AddSubDepartment.this, list);
        binding.subdeps.setAdapter(adapter);
        binding.subdeps.setLayoutManager(new GridLayoutManager(this,2));
        new GetDepsAndProducts().execute();
        adapter = new SubDepAdapter(AddSubDepartment.this, list);
        binding.subdeps.setAdapter(adapter);
        binding.subdeps.setLayoutManager(new GridLayoutManager(AddSubDepartment.this,2));
        binding.depName.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String depName = binding.depName.getSelectedItem().toString();
                        new GetAllsubDeps().execute(depName);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        String depName = binding.depName.getSelectedItem().toString();
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

                if (imageUri==null)
                    FancyToast.makeText(getApplicationContext(), "من فضلك اختر صورة للقسم الداخلي", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();

                if (!depName.isEmpty() && !subDep.isEmpty() && !subList.contains(subDep)&&imageUri!= null) {
                    progressDialog = new ProgressDialog(AddSubDepartment.this);
                    progressDialog.setMessage("Loading....");
                    progressDialog.show();
                    progressDialog.setCancelable(false);

                    uploadImage(imageUri);


                }

            }
        });

    }

    private void uploadImage(Uri imageUri) {
        imageFileName = System.currentTimeMillis() + "." + getFileExtension(imageUri);
        final StorageReference fileReference = storage.child(imageFileName);
        uploadTask = fileReference.putFile(imageUri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    assert downloadUri != null;
                    assert imageFileName != null;
                    imageUrl = downloadUri.toString();
                    String depName = binding.depName.getSelectedItem().toString();
                    String subDep  = binding.subdepName.getEditText().getText().toString().trim();

                    final SubDeparment subDeparment = new SubDeparment(depName, subDep,imageFileName,imageUrl);
                    uploadSubDep(subDeparment);
                } else {
                    FancyToast.makeText(getApplicationContext(), "حدث خطأ ما !", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                }
                progressDialog.dismiss();
            }
        });
    }
    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = Objects.requireNonNull(getApplicationContext()).getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void uploadSubDep(SubDeparment subDeparment) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SubDeps");
        reference.push().setValue(subDeparment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                FancyToast.makeText(getApplicationContext(), "تم إضافة القسم الفرعي", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
            }
        });
    }

    private  void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.addImage.setImageURI(imageUri);
        }
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