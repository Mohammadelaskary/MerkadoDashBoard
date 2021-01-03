package com.example.elmohammadymarket.Views;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.elmohammadymarket.Adapters.AdsAdapter;
import com.example.elmohammadymarket.Model.AdImages;
import com.example.elmohammadymarket.R;
import com.example.elmohammadymarket.databinding.ActivityAddNewAdBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddNewAd extends AppCompatActivity {
    private static final int IMAGE_REQUEST = 0;
    ActivityAddNewAdBinding binding;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    StorageReference storage;


    Uri imageUri;
    String mUri = "";
    String fileName;
    String adText;
    List<AdImages> adImages;
    AdsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewAdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("الاعلانات");
        storage = FirebaseStorage.getInstance().getReference("Ads");
        adImages = new ArrayList<>();
        binding.addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.noAdsText.setVisibility(View.GONE);
                adText = binding.textAd.getEditText().getText().toString();
                adImages.add(new AdImages(mUri, fileName, adText));
                uploadImage();

                binding.addImage.setImageResource(R.drawable.add_icon);
                binding.textAd.getEditText().setText("");
            }
        });
        if (isConnected()) {
            getAllImages();
            adapter = new AdsAdapter(this, adImages);
            binding.adImages.setAdapter(adapter);
            binding.adImages.setLayoutManager(new LinearLayoutManager(this));
            binding.adImages.setHasFixedSize(true);
        } else {
            binding.addImage.setVisibility(View.GONE);
            binding.add.setVisibility(View.GONE);
            binding.adImages.setVisibility(View.GONE);
            binding.noAdsText.setVisibility(View.VISIBLE);
            binding.textAd.setVisibility(View.GONE);
            binding.noAdsText.setText("تأكد من الاتصال بالانترنت...");
        }

    }

    private void openImage() {
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

            if (uploadTask != null && uploadTask.isInProgress()) {
                FancyToast.makeText(getApplicationContext(), "Upload in progress! "
                        , FancyToast.LENGTH_LONG, FancyToast.DEFAULT, true).show();
            } else {

            }

        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading....");
        progressDialog.show();
        progressDialog.setCancelable(false);
        if (imageUri != null) {
            fileName = System.currentTimeMillis()
                    + "." + getFileExtension(imageUri);
            final StorageReference fileReference = storage.child(fileName);
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
                        mUri = downloadUri.toString();
                        uploadImageUrl(mUri);
                        progressDialog.dismiss();
                        FancyToast.makeText(getApplicationContext(), "Image uploaded "
                                , FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();

                    } else {
                        FancyToast.makeText(getApplicationContext(), "Failed "
                                , FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    FancyToast.makeText(getApplicationContext(), e.getMessage()
                            , FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            progressDialog.dismiss();
            FancyToast.makeText(getApplicationContext(), "No image selected! "
                    , FancyToast.LENGTH_SHORT, FancyToast.WARNING, true).show();
            return;
        }
    }

    private void uploadImageUrl(String mUri) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AdImagesUrl");
        AdImages adImages = new AdImages(mUri, fileName, adText);
        reference.push().setValue(adImages);
    }


    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = Objects.requireNonNull(getApplicationContext()).getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void getAllImages() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("AdImagesUrl");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    binding.noAdsText.setVisibility(View.VISIBLE);
                    binding.noAdsText.setText("لا يوجد صور");
                } else {
                    adImages.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AdImages adImages1 = snapshot.getValue(AdImages.class);
                        adImages.add(adImages1);
                        adapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
}