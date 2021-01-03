package com.example.elmohammadymarket.Adapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.elmohammadymarket.Model.AdImages;
import com.example.elmohammadymarket.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.AdViewHolder> {
    Context context;
    List<AdImages> adImages;
    private StorageReference mStorageRef;

    public AdsAdapter(Context context, List<AdImages> adImages) {
        this.context = context;
        this.adImages = adImages;
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ad_images_item, parent, false);
        return new AdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdViewHolder holder, final int position) {
        holder.adText.setText(adImages.get(position).getAdText());
        Glide.with(context).load(adImages.get(position).getAdImageUrl()).into(holder.image);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStorageRef = FirebaseStorage.getInstance().getReference();
                StorageReference ref = mStorageRef.child("Ads/" + adImages.get(position).getImageFileName());
                ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FancyToast.makeText(context, "تم إزالة الإعلان"
                                , FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    }
                });
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                Query query = databaseReference.child("AdImagesUrl").orderByChild("adImageUrl").equalTo(adImages.get(position).getAdImageUrl());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removeAt(position);
                    }
                }, 1000);
            }


        });
    }

    @Override
    public int getItemCount() {
        return adImages.size();
    }

    static class AdViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageButton delete;
        TextView adText;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.ad_image);
            delete = itemView.findViewById(R.id.delete);
            adText = itemView.findViewById(R.id.text_ad);
        }
    }

    private void removeAt(int position) {
        adImages.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, adImages.size());
    }
}
