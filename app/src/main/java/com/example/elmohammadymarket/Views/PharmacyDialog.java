package com.example.elmohammadymarket.Views;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.elmohammadymarket.Adapters.PharmacyAdapter;
import com.example.elmohammadymarket.Model.PharmacyOrder;
import com.example.elmohammadymarket.R;
import com.example.elmohammadymarket.databinding.PharmacyLayoutBinding;
import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PharmacyDialog extends Dialog  {
    PharmacyLayoutBinding binding;
    List<PharmacyOrder> orders;
    public PharmacyDialog(@NonNull Context context,List<PharmacyOrder> orders) {
        super(context);
        this.orders=orders;
    }

    public PharmacyDialog(@NonNull Context context, int themeResId, List<PharmacyOrder> orders) {
        super(context, themeResId);
        this.orders = orders;
    }

    PharmacyAdapter adapter;
    String mobileNumber;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = PharmacyLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("جار التحميل..");
        adapter = new PharmacyAdapter(orders,getContext());
        binding.pharmacyOrders.setAdapter(adapter);
        mobileNumber = orders.get(0).getShippingData().getMobileNumber();
        binding.sendPharmacy.setOnClickListener(v ->{
            converted.clear();
            convertImageUrlToImage64();
            progressDialog.show();
        });
    }
    int i = 0 ;
    List<PharmacyOrder> converted = new ArrayList<>();
    private void convertImageUrlToImage64() {
        if (!orders.isEmpty()){
            PharmacyOrder order = orders.get(i);
            String url = order.getImageUrl();
            if (!url.equals("")){
                Glide.with(getContext())
                        .asBitmap()
                        .load(url)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                order.setImage64(encodeImage(resource));
                                order.setImageUrl("");
                                converted.add(order);
                                if (i>=orders.size()-1){
                                    progressDialog.dismiss();
                                    shareData(converted,mobileNumber);
                                    i=0;
                                } else {
                                    i++;
                                    convertImageUrlToImage64();
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            } else {
                converted.add(order);
                if (i>=orders.size()-1){
                    progressDialog.dismiss();
                    shareData(converted,mobileNumber);
                    i = 0;
                } else {
                    i++;
                    convertImageUrlToImage64();
                }
            }
            }
    }

    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    private Bitmap createPharmacyClusterBitmap(List<PharmacyOrder> orders){
        View cluster = LayoutInflater.from(getContext()).inflate(R.layout.pharmacy_layout,
                null);
        RecyclerView pharmacyOrdersRec = cluster.findViewById(R.id.pharmacy_orders);
        MaterialButton sendPharmacy = cluster.findViewById(R.id.send_pharmacy);
        PharmacyAdapter adapter = new PharmacyAdapter(orders,getContext());
        Log.d("ordersPrint",orders.size()+"");
        pharmacyOrdersRec.setAdapter(adapter);
        sendPharmacy.setVisibility(View.GONE);
        cluster.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        cluster.layout(0, 0, cluster.getMeasuredWidth(), cluster.getMeasuredHeight());


        final Bitmap clusterBitmap = Bitmap.createBitmap(cluster.getMeasuredWidth(),
                cluster.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(clusterBitmap);
        canvas.drawColor(Color.WHITE);
        cluster.draw(canvas);

        return scaleBitmap(clusterBitmap);
    }
    private Bitmap createPharmacyClusterBitmap(PharmacyOrder order){
        View cluster = LayoutInflater.from(getContext()).inflate(R.layout.pharmacy_order_send_layout,
                null);
        ImageView pharmacyImage = cluster.findViewById(R.id.pharmacy_image);
        LinearLayout productNameContainer = cluster.findViewById(R.id.product_name_container);
        TextView therapyNameTv = cluster.findViewById(R.id.therapy_name);
        LinearLayout therapyNumberContainer = cluster.findViewById(R.id.therapy_number_container);
        TextView therapyNum = cluster.findViewById(R.id.therapy_num);
        TextView therapyItemType = cluster.findViewById(R.id.therapy_item_type);
        LinearLayout acceptAlternativeContainer = cluster.findViewById(R.id.accept_alternative_container);
        TextView acceptAlternativeTv = cluster.findViewById(R.id.accept_alternative);
        LinearLayout descriptionContainer = cluster.findViewById(R.id.description_container);
        TextView description = cluster.findViewById(R.id.description);

        String imageUrl = order.getImageUrl();
        String therapyName = order.getTherapyName();
        String numberOfItems = order.getNumberOfItems();
        String typeOfItem    = order.getTypeOfItem();
        String acceptAlternative = order.getAcceptAlternative();
        String decription        = order.getDescribtion();
        String pharmacyOrderId = order.getPharmacyCartOrderId();
        String fileName        = order.getImageFileName();
        String image64         = "";
        image64 = order.getImage64();

            if(image64!=null){
                if (!image64.equals(""))
                    decodeBase64AndSetImage(image64,pharmacyImage);
                else
                    pharmacyImage.setVisibility(View.GONE);

        }

        if (therapyName.isEmpty()){
            productNameContainer.setVisibility(View.GONE);
        } else {
            therapyNameTv.setText(therapyName);
        }

        if (numberOfItems.isEmpty()){
            therapyNumberContainer.setVisibility(View.GONE);
        } else {
           therapyNum.setText(numberOfItems);
           therapyItemType.setText(typeOfItem);
        }

        if (acceptAlternative.isEmpty()){
            acceptAlternativeContainer.setVisibility(View.GONE);
        } else {
            if (acceptAlternative.equals("true"))
                acceptAlternativeTv.setText("نعم");
            else
                acceptAlternativeTv.setText("لا");
        }

        if (decription.isEmpty()){
            descriptionContainer.setVisibility(View.GONE);
        } else {
            description.setText(decription);
        }
        cluster.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        cluster.layout(0, 0, cluster.getMeasuredWidth(), cluster.getMeasuredHeight());


        final Bitmap clusterBitmap = Bitmap.createBitmap(cluster.getMeasuredWidth(),
                cluster.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(clusterBitmap);
        canvas.drawColor(Color.WHITE);
        cluster.draw(canvas);

        return clusterBitmap;
    }

    private void decodeBase64AndSetImage(String completeImageData, ImageView imageView) {

        // Incase you're storing into aws or other places where we have extension stored in the starting.
        String imageDataBytes = completeImageData.substring(completeImageData.indexOf(",")+1);

        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));

        Bitmap bitmap = BitmapFactory.decodeStream(stream);

        imageView.setImageBitmap(bitmap);
    }

    private Bitmap scaleBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        Log.v("Pictures", "Width and height are " + width + "--" + height);

        if (width > height) {
            // landscape
            float ratio = (float) width / 400;
            width = 400;
            height = (int)(height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = (float) height / 700;
            height = 700;
            width = (int)(width / ratio);
        } else {
            // square
            height = 700;
            width = 400;
        }

        Log.v("Pictures", "after scaling Width and height are " + width + "--" + height);

        bm = Bitmap.createScaledBitmap(bm, width, height, true);
        return bm;
    }
    private File saveBitMap(Bitmap bitmap) {
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Handcare");
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if (!isDirectoryCreated)
                Log.i("ATG", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() + File.separator + System.currentTimeMillis() + ".png";
        File pictureFile = new File(filename);
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
        return pictureFile;
    }
    private void shareData(List<PharmacyOrder> orders, String mobileNumber) {
        ArrayList<Uri> files = new ArrayList<>();
        for (PharmacyOrder order:orders){
            Bitmap bitmap = createPharmacyClusterBitmap(order);
            File file = saveBitMap(bitmap);    //which view you want to pass that view as parameter
            assert file != null;
            Uri imgUri = Uri.parse(file.getAbsolutePath());
            files.add(imgUri); // uri of my bitmap image2
        }
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "رقم تليفون العميل " + mobileNumber);
        whatsappIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,files);
        whatsappIntent.setType("image/png");
        whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            getContext().startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {

        }
    }

}
