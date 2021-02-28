package com.example.elmohammadymarket.Views;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.elmohammadymarket.Database.DepartmentNames;
import com.example.elmohammadymarket.Database.Product;
import com.example.elmohammadymarket.Model.MostSoldName;
import com.example.elmohammadymarket.Model.SubDeparment;
import com.example.elmohammadymarket.Model.TodaysOfferName;
import com.example.elmohammadymarket.R;
import com.example.elmohammadymarket.databinding.ActivityAddNewProductBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class AddNewProduct extends AppCompatActivity {
    private static final int IMAGE_REQUEST = 0;
    boolean add;
    ActivityAddNewProductBinding binding;
    StorageReference storage;
    Uri imageUri;
    String mUri = "";
    String imageFileName;
    String currentImageFileName;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    Product product;
    List<String> depNames;
    List<String> productsNames;
    List<String> optionsList;
    List<Product> productsList;
    List<String> mostSoldList = new ArrayList<>();
    List<String> todaysOfferList = new ArrayList<>();
    ArrayAdapter<String> depsAdapter;
    ArrayAdapter<String> optionsAdapter;
    ArrayAdapter<String> subDepsAdapter;
    String depName, subdep, imageUrl, productName, price, unitWeight, discount, discountUnit, count;
    boolean todaysOffer, mostSold;
    float availableAmount,minimumOrderAmount;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("إضافة منتج");
        storage = FirebaseStorage.getInstance().getReference("Images");

        if (isConnected()) {
            binding.noInternetText.setVisibility(View.GONE);
            new GetDepsAndProducts().execute();
        } else {
            binding.noInternetText.setVisibility(View.VISIBLE);
            binding.addProductLayout.setVisibility(View.GONE);
            binding.progressBarProductName.setVisibility(View.GONE);
        }
        getMostSoldProducts();
        getTodaysOfferProducts();

        optionsList = new ArrayList<>();
        optionsList.add("جنيه");
        optionsList.add("%");
        optionsAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_style, optionsList);
        optionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.discountType.setAdapter(optionsAdapter);


        getIntentData();
        Log.d("subdep", subdep);

        assert discountUnit != null;
        if (!discountUnit.isEmpty()) {
            final int spinnerPosition = optionsAdapter.getPosition(discountUnit);
            binding.discountType.setSelection(spinnerPosition);

        }
        if (imageUrl.isEmpty())
            binding.addImage.setImageResource(R.drawable.add_icon);
        else
            Glide.with(this).load(imageUrl).into(binding.addImage);

        binding.todaysOffer.setChecked(todaysOffer);
        binding.mostSold.setChecked(mostSold);
        binding.productName.getEditText().setText(productName);
        binding.addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewProduct();
            }
        });
        binding.productPrice.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.productPrice.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.productPrice.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String productName = binding.productPrice.getEditText().getText().toString().trim();
                if (productName.isEmpty()) {
                    binding.productPrice.setError("ادخل سعر المنتج");
                } else {
                    binding.productPrice.setError(null);
                }


            }
        });
        binding.unitWeight.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.unitWeight.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.unitWeight.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String productName = binding.unitWeight.getEditText().getText().toString().trim();
                if (productName.isEmpty()) {
                    binding.unitWeight.setError("ادخل وزن الوحدة");
                } else {
                    binding.unitWeight.setError(null);
                }


            }
        });
        binding.productName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.productName.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.productName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String productName = binding.productName.getEditText().getText().toString().trim();
                if (productName.isEmpty()) {
                    binding.productName.setError("ادخل اسم المنتج");
                } else {
                    binding.productName.setError(null);
                }


            }
        });

        binding.depName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                new GetAllsubDeps()
                        .execute(binding.depName.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


    }

    public void addNewProduct() {
        if (add) {
            String productName, subDep, productPrice, department, unitWeight, discount, discountUnit;
            float availableAmount,minimumOrderAmount;
            productName = Objects.requireNonNull(binding.productName.getEditText()).getText().toString().trim();
            subDep = binding.subdepName.getSelectedItem().toString();
            productPrice = Objects.requireNonNull(binding.productPrice.getEditText()).getText().toString().trim();
            department = binding.depName.getSelectedItem().toString();
            unitWeight = Objects.requireNonNull(binding.unitWeight.getEditText()).getText().toString().trim();
            discount = Objects.requireNonNull(binding.discount.getEditText()).getText().toString().trim();
            discountUnit = binding.discountType.getSelectedItem().toString();
            availableAmount = Float.parseFloat(binding.availableAmount.getEditText().getText().toString().trim());
            minimumOrderAmount = Float.parseFloat(binding.minimumOrderAmount.getEditText().getText().toString().trim());
            todaysOffer = binding.todaysOffer.isChecked();
            mostSold = binding.mostSold.isChecked();

            if (binding.availableAmount.getEditText().getText().toString().trim().isEmpty())
                availableAmount = 0;
            product = new Product(department, subDep, productName, productPrice
                    , unitWeight, discount, discountUnit, availableAmount, todaysOffer, mostSold,minimumOrderAmount);
            product.setCount("0");
            boolean validateDiscount = true;

            if (discountUnit.equals("جنيه") && !binding.discount.getEditText().getText().toString().isEmpty() && !binding.productPrice.getEditText().getText().toString().isEmpty()) {
                if (Float.parseFloat(binding.discount.getEditText().getText().toString()) > Float.parseFloat(binding.productPrice.getEditText().getText().toString())) {
                    binding.discount.setError("مبلغ الخصم يجب أن يكون أقل من السعر");
                    validateDiscount = false;
                } else {
                    binding.discount.setError(null);
                }

            } else if (discountUnit.equals("%") && !binding.discount.getEditText().getText().toString().isEmpty() && !binding.productPrice.getEditText().getText().toString().isEmpty()) {
                if (Float.parseFloat(binding.discount.getEditText().getText().toString()) > 100) {
                    binding.discount.setError("نسبة الخصم يجب أن تكون أقل من أو تساوي 100");
                    validateDiscount = false;
                } else binding.discount.setError(null);
            }


            if (productName.isEmpty())
                binding.productName.setError("ادخل اسم المنتج");
            if (productPrice.isEmpty())
                binding.productPrice.setError("ادخل سعر المنتج");
            if (unitWeight.isEmpty())
                binding.unitWeight.setError("ادخل وزن الوحدة");
            if (!productName.isEmpty() && !productPrice.isEmpty() && !unitWeight.isEmpty() && validateDiscount) {

                if (!productsNames.contains(productName)) {

                    if (uploadTask != null && uploadTask.isInProgress()) {
                        FancyToast.makeText(getApplicationContext(), "Upload in progress! "
                                , FancyToast.LENGTH_LONG, FancyToast.DEFAULT, false).show();
                    } else {
                        uploadImageAndProduct();
                    }


                } else {
                    binding.productName.setError("هذا الاسم مستخدم من قبل");
                }

            }


        } else {

            String productName, productPrice, department, subdep, unitWeight, discount, discountUnit;
            float availableAmount,minimumOrderAmount;
            productName = binding.productName.getEditText().getText().toString().trim();
            productPrice = binding.productPrice.getEditText().getText().toString().trim();
            department = binding.depName.getSelectedItem().toString();
            subdep = binding.subdepName.getSelectedItem().toString();
            unitWeight = binding.unitWeight.getEditText().getText().toString().trim();
            discount = binding.discount.getEditText().getText().toString().trim();
            discountUnit = binding.discountType.getSelectedItem().toString();
            availableAmount = Float.parseFloat(binding.availableAmount.getEditText().getText().toString().trim());
            minimumOrderAmount = Float.parseFloat(binding.minimumOrderAmount.getEditText().getText().toString().trim());
            boolean mostSold = binding.mostSold.isChecked();
            boolean todaysOffer = binding.todaysOffer.isChecked();
            if (binding.availableAmount.getEditText().getText().toString().isEmpty())
                availableAmount = 0;


            final HashMap<String, Object> map = new HashMap<>();

            map.put("dep", department);
            map.put("productName", productName);
            map.put("price", productPrice);
            map.put("unitWeight", unitWeight);
            map.put("discount", discount);
            map.put("discountUnit", discountUnit);
            map.put("availableAmount", availableAmount);
            map.put("todaysOffer", todaysOffer);
            map.put("mostSold", mostSold);
            map.put("subDep", subdep);
            map.put("count", count);
            map.put("minimumOrderAmount",minimumOrderAmount);
            boolean validateDiscount = true;

            if (discountUnit.equals("جنيه") && !binding.discount.getEditText().getText().toString().isEmpty() && !binding.productPrice.getEditText().getText().toString().isEmpty()) {
                if (Float.parseFloat(binding.discount.getEditText().getText().toString()) > Float.parseFloat(binding.productPrice.getEditText().getText().toString())) {
                    binding.discount.setError("مبلغ الخصم يجب أن يكون أقل من السعر");
                    validateDiscount = false;
                } else {
                    binding.discount.setError(null);
                }

            } else if (discountUnit.equals("%") && !binding.discount.getEditText().getText().toString().isEmpty() && !binding.productPrice.getEditText().getText().toString().isEmpty()) {
                if (Float.parseFloat(binding.discount.getEditText().getText().toString()) > 100) {
                    binding.discount.setError("نسبة الخصم يجب أن تكون أقل من أو تساوي 100");
                    validateDiscount = false;
                } else binding.discount.setError(null);
            }


            if (productName.isEmpty())
                binding.productName.setError("ادخل اسم المنتج");
            if (productPrice.isEmpty())
                binding.productPrice.setError("ادخل سعر المنتج");
            if (unitWeight.isEmpty())
                binding.unitWeight.setError("ادخل وزن الوحدة");
            if (!productName.isEmpty() && !productPrice.isEmpty() && !unitWeight.isEmpty() && validateDiscount) {

                if (!productsNames.contains(productName)) {

                    if (uploadTask != null && uploadTask.isInProgress()) {
                        FancyToast.makeText(getApplicationContext(), "Upload in progress! "
                                , FancyToast.LENGTH_LONG, FancyToast.DEFAULT, false).show();
                    } else {
                        uploadImageAndUpdateProduct(map);
                    }

                }


            }
        }
    }

    private void uploadImageAndProduct() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading....");
        progressDialog.show();
        progressDialog.setCancelable(false);
        if (imageUri != null) {
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
                        mUri = downloadUri.toString();
                        product.setImageFileName(imageFileName);
                        product.setImageUrl(mUri);
                        DatabaseReference ref = database.getReference("Products");
                        ref.push().setValue(product);
                        FancyToast.makeText(getApplicationContext(), "تم إضافة المنتج"
                                , FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                        if (todaysOffer) {
                            TodaysOfferName todaysOfferName = new TodaysOfferName(productName);
                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("TodaysOffer");
                            reference1.push().setValue(todaysOfferName);
                        }
                        if (mostSold) {
                            MostSoldName mostSoldName = new MostSoldName(productName);
                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("MostSoldName");
                            reference1.push().setValue(mostSoldName);
                        }

                        progressDialog.dismiss();
                        binding.addImage.setImageResource(R.drawable.add_icon);
                        binding.productName.getEditText().setText("");
                        binding.productName.setError(null);
                        binding.productPrice.getEditText().setText("");
                        binding.productPrice.setError(null);
                        binding.discount.getEditText().setText("");
                        binding.unitWeight.getEditText().setText("");
                        binding.unitWeight.setError(null);
                        binding.availableAmount.getEditText().setText("");
                        binding.minimumOrderAmount.getEditText().setText("");
                        binding.productName.requestFocus();
                        imageUri = Uri.EMPTY;
                        mUri = "";

                    } else {
                        FancyToast.makeText(getApplicationContext(), "Failed "
                                , FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    FancyToast.makeText(getApplicationContext(), e.getMessage()
                            , FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    progressDialog.dismiss();

                }
            });
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("لم يتم اختيار صورة")
                    .setMessage("هل تريد إضافة المنتج بدون صورة؟")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            product.setImageFileName("");
                            product.setImageUrl("");
                            DatabaseReference ref = database.getReference("Products");
                            ref.push().setValue(product);
                            if (todaysOffer) {
                                TodaysOfferName todaysOfferName = new TodaysOfferName(productName);
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("TodaysOffer");
                                reference1.push().setValue(todaysOfferName);
                            }
                            if (mostSold) {
                                MostSoldName mostSoldName = new MostSoldName(productName);
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("MostSoldName");
                                reference1.push().setValue(mostSoldName);
                            }
                            FancyToast.makeText(getApplicationContext(), "تم إضافة المنتج"
                                    , FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                            binding.addImage.setImageResource(R.drawable.add_icon);
                            binding.productName.getEditText().setText("");
                            binding.productName.setError(null);
                            binding.productPrice.getEditText().setText("");
                            binding.productPrice.setError(null);
                            binding.discount.getEditText().setText("");
                            binding.unitWeight.getEditText().setText("");
                            binding.unitWeight.setError(null);
                            binding.availableAmount.getEditText().setText("");
                            binding.minimumOrderAmount.getEditText().setText("");
                            binding.productName.requestFocus();
                            imageUri = Uri.EMPTY;
                            mUri = "";

                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
            progressDialog.hide();
        }
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

    private void uploadImageAndUpdateProduct(final HashMap<String, Object> map) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading....");
        progressDialog.show();
        progressDialog.setCancelable(false);
        if (imageUri != null) {
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
                        mUri = downloadUri.toString();
                        map.put("imageUrl", mUri);
                        map.put("imageFileName", imageFileName);

                        DatabaseReference ref = database.getReference();
                        Query query = ref.child("Products").orderByChild("productName").equalTo(productName);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    snapshot.getRef().updateChildren(map);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Log.d("todaysOffer", binding.todaysOffer.isChecked() + "");

                        FancyToast.makeText(getApplicationContext(), "تم تعديل المنتج"
                                , FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                        deleteImage(currentImageFileName);
                        Intent intent = new Intent(AddNewProduct.this, ManageProducts.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        progressDialog.dismiss();


                    } else {
                        FancyToast.makeText(getApplicationContext(), "Failed "
                                , FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    FancyToast.makeText(getApplicationContext(), e.getMessage()
                            , FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    progressDialog.dismiss();

                }
            });
        } else {

            DatabaseReference ref = database.getReference();
            Query query = ref.child("Products").orderByChild("productName").equalTo(productName);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().updateChildren(map);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            FancyToast.makeText(getApplicationContext(), "تم تعديل المنتج"
                    , FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
        }
        Intent intent = new Intent(AddNewProduct.this, ManageProducts.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        progressDialog.dismiss();

    }

    private void deleteImage(String imageFileName) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference storageReference = mStorageRef.child("Images/" + imageFileName);
        storageReference.delete();

    }


    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = Objects.requireNonNull(getApplicationContext()).getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    public void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        add = bundle.getBoolean("add");
        depName = bundle.getString("dep");
        subdep = bundle.getString("subDep");
        imageFileName = bundle.getString("imageFileName");
        currentImageFileName = imageFileName;
        imageUrl = bundle.getString("imageUrl");
        productName = bundle.getString("productName");
        price = bundle.getString("price");
        unitWeight = bundle.getString("unitWeight");
        discount = bundle.getString("discount");
        discountUnit = bundle.getString("discountUnit");
        availableAmount = bundle.getFloat("availableAmount");
        count = bundle.getString("count");
        todaysOffer = bundle.getBoolean("todaysOffer");
        mostSold = bundle.getBoolean("mostSold");
        minimumOrderAmount = bundle.getFloat("minimumOrderAmount");
        binding.productName.getEditText().setText(productName);
        binding.productPrice.getEditText().setText(price);
        binding.unitWeight.getEditText().setText(unitWeight);
        if (availableAmount == 0) {
            binding.availableAmount.getEditText().setText("");
        } else
            binding.availableAmount.getEditText().setText(String.valueOf(availableAmount));

        binding.discount.getEditText().setText(discount);

        if (add)
            binding.add.setText("إضافة");
        else {
            binding.productName.setEnabled(false);
            binding.depName.setEnabled(false);
            binding.add.setText("تعديل");
        }

        if (imageUrl.isEmpty())
            binding.addImage.setImageResource(R.drawable.add_icon);
        else {
            Glide.with(AddNewProduct.this).load(imageUrl).into(binding.addImage);
        }

        binding.minimumOrderAmount.getEditText().setText(String.valueOf(minimumOrderAmount));

    }


    public void getdepartmentNames() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DepartmentsNames");
        final List<DepartmentNames> departmentNamesList = new ArrayList<>();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    if (departmentNamesList.isEmpty()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewProduct.this);
                        builder.setMessage("من فضلك أدخل اسماء الأقسام أولا.....")
                                .setCancelable(false)
                                .setPositiveButton("حسنا", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        startActivity(new Intent(AddNewProduct.this, AddDepartment.class));
                                        finish();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        binding.progressBarDep.hide();
                    }
                } else {
                    departmentNamesList.clear();
                    binding.progressBarDep.hide();
                    binding.addProductLayout.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DepartmentNames depName = snapshot.getValue(DepartmentNames.class);
                        departmentNamesList.add(depName);
                    }

                    depNames = new ArrayList<>();
                    for (int i = 0; i < departmentNamesList.size(); i++) {
                        depNames.add(departmentNamesList.get(i).getDepName());
                    }

                    depsAdapter = new ArrayAdapter<>(AddNewProduct.this, R.layout.dep_spinner_style, depNames);
                    depsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.depName.setAdapter(depsAdapter);
                    try {
                        productsList = new GetProducts().execute(depName).get();
                        productsNames = new ArrayList<>();
                        for (int i = 0; i < productsList.size(); i++) {
                            productsNames.add(productsList.get(i).getProductName());
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }


                    assert depName != null;
                    if (!depName.isEmpty()) {
                        final int spinnerPosition = depsAdapter.getPosition(depName);
                        binding.depName.setSelection(spinnerPosition);

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                FancyToast.makeText(getApplicationContext(), databaseError.getMessage()
                        , FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();

            }
        });

    }

    public List<Product> getAllProductsNames(String depName) {
        final List<Product> productList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Products").child(depName);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binding.progressBarProductName.hide();
                binding.productName.setEnabled(true);
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    productList.add(product);
                }
                for (Product product : productList) {
                    productsNames.add(product.getProductName());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                FancyToast.makeText(getApplicationContext(), databaseError.getMessage()
                        , FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();

            }
        });


        return productList;
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

    private List<String> getSubDeps(final String depName) {
        final List<String> subDeparments = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("SubDeps").orderByChild("depName").equalTo(depName);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subDeparments.clear();
                if (!snapshot.exists()) {
                    subDeparments.add("عام");
                    subDepsAdapter = new ArrayAdapter<>(AddNewProduct.this, R.layout.dep_spinner_style, subDeparments);
                    subDepsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.subdepName.setAdapter(subDepsAdapter);
                    binding.subdepProgress.hide();
                } else {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        SubDeparment subDeparment = snapshot1.getValue(SubDeparment.class);
                        subDeparments.add(subDeparment.getSubdepName());
                        binding.subdepProgress.hide();
                        if (subDeparments.isEmpty())
                            subDeparments.add("عام");
                        subDepsAdapter = new ArrayAdapter<>(AddNewProduct.this, R.layout.dep_spinner_style, subDeparments);
                        subDepsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        binding.subdepName.setAdapter(subDepsAdapter);
                        if (!subdep.isEmpty()) {
                            final int spinnerPosition = subDepsAdapter.getPosition(subdep);
                            binding.subdepName.setSelection(spinnerPosition);

                        }
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return subDeparments;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public class GetDepsAndProducts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.addProductLayout.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getdepartmentNames();
            return null;
        }
    }

    public class GetAllsubDeps extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.subdepProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            getSubDeps(strings[0]);
            return null;
        }
    }

    public class GetProducts extends AsyncTask<String, Void, List<Product>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.progressBarProductName.show();
        }

        @Override
        protected List<Product> doInBackground(String... strings) {
            List<Product> products = getAllProductsNames(strings[0]);
            return products;
        }
    }

    public void getMostSoldProducts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MostSold");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mostSoldList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Product product = snapshot1.getValue(Product.class);
                    mostSoldList.add(product.getProductName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getTodaysOfferProducts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TodaysOffer");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                todaysOfferList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Product product = snapshot1.getValue(Product.class);
                    todaysOfferList.add(product.getProductName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}