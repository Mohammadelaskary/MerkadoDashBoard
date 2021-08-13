package com.example.elmohammadymarket.Adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.elmohammadymarket.Model.PharmacyOrder;
import com.example.elmohammadymarket.R;
import com.example.elmohammadymarket.databinding.PharmacyOrderLayoutBinding;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;


public class PharmacyAdapter extends RecyclerView.Adapter<PharmacyAdapter.PharmacyViewHolder> {
    List<PharmacyOrder> orders;
    Context context;

    public PharmacyAdapter(List<PharmacyOrder> orders,Context context) {
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public PharmacyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PharmacyViewHolder(PharmacyOrderLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PharmacyViewHolder holder, int position) {
        String imageUrl = orders.get(position).getImageUrl();
        String therapyName = orders.get(position).getTherapyName();
        String numberOfItems = orders.get(position).getNumberOfItems();
        String typeOfItem    = orders.get(position).getTypeOfItem();
        String acceptAlternative = orders.get(position).getAcceptAlternative();
        String decription        = orders.get(position).getDescribtion();
        String pharmacyOrderId = orders.get(position).getPharmacyCartOrderId();
        String fileName        = orders.get(position).getImageFileName();
        String image64         = "";
        image64 = orders.get(position).getImage64();
        if (!imageUrl.isEmpty()){
            Glide.with(context).load(imageUrl).into(holder.binding.pharmacyImage);
            Log.d("ordersPharmacySize",imageUrl);
        } else {
            if(image64!=null){
                if (!image64.equals(""))
                    decodeBase64AndSetImage(image64,holder.binding.pharmacyImage);
                else
                    holder.binding.pharmacyImage.setVisibility(View.GONE);
            }
        }




        if (therapyName.isEmpty()){
            holder.binding.productNameContainer.setVisibility(View.GONE);
        } else {
            holder.binding.therapyName.setText(therapyName);
        }

        if (numberOfItems.isEmpty()){
            holder.binding.therapyNumberContainer.setVisibility(View.GONE);
        } else {
            holder.binding.therapyNum.setText(numberOfItems);
            holder.binding.therapyItemType.setText(typeOfItem);
        }

        if (acceptAlternative.isEmpty()){
            holder.binding.acceptAlternativeContainer.setVisibility(View.GONE);
        } else {
            if (acceptAlternative.equals("true"))
                holder.binding.acceptAlternative.setText("نعم");
            else
                holder.binding.acceptAlternative.setText("لا");
        }

        if (decription.isEmpty()){
            holder.binding.descriptionContainer.setVisibility(View.GONE);
        } else {
            holder.binding.description.setText(decription);
        }
    }
    private void decodeBase64AndSetImage(String completeImageData, ImageView imageView) {

        // Incase you're storing into aws or other places where we have extension stored in the starting.
        String imageDataBytes = completeImageData.substring(completeImageData.indexOf(",")+1);

        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));

        Bitmap bitmap = BitmapFactory.decodeStream(stream);

        imageView.setImageBitmap(bitmap);
    }
    public String resizeUpBase64Image(String base64image){
        byte [] encodeByte=Base64.decode(base64image.getBytes(),Base64.DEFAULT);
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPurgeable = true;
        Bitmap image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length,options);


        if(image.getHeight() >= 1000 && image.getWidth() >= 1000){
            return base64image;
        }

        image = Bitmap.createScaledBitmap(image, (int)(image.getWidth()*10), (int)(image.getHeight()*10), false);

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG,100, baos);

        byte [] b=baos.toByteArray();
        System.gc();
        return Base64.encodeToString(b, Base64.NO_WRAP);

    }

    @Override
    public int getItemCount() {
        return orders==null?0:orders.size();
    }

    static class PharmacyViewHolder extends RecyclerView.ViewHolder{
        PharmacyOrderLayoutBinding binding;
        public PharmacyViewHolder(PharmacyOrderLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
