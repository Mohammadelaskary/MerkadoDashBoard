package com.example.elmohammadymarket.Views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elmohammadymarket.Adapters.FullOrderAdapter;
import com.example.elmohammadymarket.Model.FullOrder;
import com.example.elmohammadymarket.OnCallClickListener;
import com.example.elmohammadymarket.databinding.ActivityOrdersBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class OrdersActivity extends AppCompatActivity implements OnCallClickListener {
    private static final int REQUEST_CODE_CALL = 100;
    private static final int REQUEST_CODE_SHARE = 200;
    ActivityOrdersBinding binding;
    List<FullOrder> list = new ArrayList<>();
    FullOrderAdapter adapter;
    String mobileNumber;
    LinearLayout layout;
    String mobileNumberGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle("الطلبـــــات");
        binding.fullOrderRecycler.setVisibility(View.GONE);
        binding.ordersTitle.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        if (isConnected()) {
            getData();
            adapter = new FullOrderAdapter(this, list, true, this);
            binding.fullOrderRecycler.setAdapter(adapter);
            binding.fullOrderRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, true));
            binding.fullOrderRecycler.setHasFixedSize(true);
        } else {
            binding.progressBar.hide();
            binding.noOrdersText.setVisibility(View.VISIBLE);
            binding.noOrdersText.setText("تأكد من الاتصال بالانترنت...");
        }

    }


    void getData() {
        DatabaseReference getRef = FirebaseDatabase.getInstance().getReference("Orders");
        getRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if (!dataSnapshot.exists()) {
                    binding.progressBar.hide();
                    binding.noOrdersText.setVisibility(View.VISIBLE);
                    binding.noOrdersText.setText("لا يوجد طلبات حاليا");
                } else {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FullOrder fullOrder = snapshot.getValue(FullOrder.class);
                        list.add(fullOrder);
                        Log.d(TAG, "onDataChange: orders" + fullOrder.getUsername());
                        adapter.notifyDataSetChanged();
                        binding.progressBar.hide();
                        binding.noOrders.setVisibility(View.GONE);
                        binding.fullOrderRecycler.setVisibility(View.VISIBLE);
                        binding.ordersTitle.setVisibility(View.VISIBLE);
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

    @Override
    public void onCallClickListener(String mobileNumber) {
        this.mobileNumber = mobileNumber;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {

                makeCall(mobileNumber);


            } else {

                ActivityCompat.requestPermissions(OrdersActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_CALL);
            }

        }
    }

    @Override
    public void onSendClickListener(LinearLayout layout, String mobileNumber) {
        this.layout = layout;
        mobileNumberGlobal = mobileNumber;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                shareData(layout,mobileNumber);

            } else {

                ActivityCompat.requestPermissions(OrdersActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_SHARE);
            }

        }
    }

    private void shareData(LinearLayout layout,  String mobileNumber) {
        File file = saveBitMap(OrdersActivity.this, layout);    //which view you want to pass that view as parameter
        assert file != null;
        Uri imgUri = Uri.parse(file.getAbsolutePath());
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT,    "رقم تليفون العميل " + mobileNumber);
        whatsappIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
        whatsappIntent.setType("image/png");
        whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {

        }
    }

    private File saveBitMap(Context context, View drawView) {
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Handcare");
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if (!isDirectoryCreated)
                Log.i("ATG", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() + File.separator + System.currentTimeMillis() + ".png";
        File pictureFile = new File(filename);
        Bitmap bitmap = getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery(context, pictureFile.getAbsolutePath());
        return pictureFile;
    }

    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeCall(String mobileNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
        if (requestCode == REQUEST_CODE_CALL) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall(mobileNumber);
            }
        }

        if (requestCode == REQUEST_CODE_SHARE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                shareData(layout,mobileNumber);
            }
        }

    }

}