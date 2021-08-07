package com.example.elmohammadymarket.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.elmohammadymarket.Database.Product;
import com.example.elmohammadymarket.Model.Cart;
import com.example.elmohammadymarket.Model.User;
import com.example.elmohammadymarket.OnDeleteProductClicked;
import com.example.elmohammadymarket.R;
import com.example.elmohammadymarket.Views.AddNewProduct;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsViewholder> {
    Context context;
    List<Product> productList;
    DatabaseReference  ref = FirebaseDatabase.getInstance().getReference();
    List<Product> deletedItems;
    List<String> mostSoldList = new ArrayList();
    List<User> users = new ArrayList<>();
    List<String> todaysOfferList = new ArrayList<>();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    public ProductsAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        users = getAllUsers();
        return new ProductsViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewholder holder, final int position) {

        final String imageFileName = productList.get(position).getImageFileName();
        final String imageUrl = productList.get(position).getImageUrl();
        final String dep = productList.get(position).getDep();
        final String subDep = productList.get(position).getSubDep();
        final String productName = productList.get(position).getProductName();
        final String price = productList.get(position).getPrice();
        final String unitWeight = productList.get(position).getUnitWeight();
        final String discount = productList.get(position).getDiscount();
        final String discountType = productList.get(position).getDiscountUnit();
        final String availableAmount = productList.get(position).getAvailableAmount();
        final String minimumOrderAmount = productList.get(position).getMinimumOrderAmount();
        final String count = productList.get(position).getCount();
        final boolean mostSold = productList.get(position).isMostSold();
        final boolean todaysOffer = productList.get(position).isTodaysOffer();
        final boolean isVisible = productList.get(position).isVisible();
        final boolean isAvailable = productList.get(position).isAvailable();
        int productPosition = productList.get(position).getPosition();
        Log.d("isAvailable",isAvailable+"");
        if (productPosition == -1){
            updatePosition(position,productName);
        }
        deletedItems = new ArrayList<>();

        if (isAvailable){
            holder.isAvailable.setText("متاح");
            holder.isAvailable.setTextColor(Color.GREEN);
        } else {
            holder.isAvailable.setText("غير متاح");
            holder.isAvailable.setTextColor(Color.GRAY);
        }
        if (isVisible){
            holder.isVisible.setText("مرئي");
            holder.isVisible.setTextColor(Color.GREEN);
        } else {
            holder.isVisible.setText("غير مرئي");
            holder.isVisible.setTextColor(Color.GRAY);
        }
        if (Float.parseFloat(availableAmount) == 0) {
            holder.unavailable.setVisibility(View.VISIBLE);
            holder.unavailableImage.setVisibility(View.VISIBLE);
        } else {
            holder.unavailable.setVisibility(View.GONE);
            holder.unavailableImage.setVisibility(View.GONE);
        }
        if (discount.isEmpty()) {
            holder.discount.setVisibility(View.GONE);
            holder.productFinal.setText(price + " " + "جنيه/" + unitWeight);
            holder.discountPrice.setVisibility(View.GONE);
        } else {
            holder.discount.setVisibility(View.VISIBLE);
            holder.discount.setText("خصم " + discount + " " + discountType);
            holder.discountPrice.setVisibility(View.VISIBLE);
            holder.discountPrice.setText("بدلا من " + price + " جنيه");
            BigDecimal priceValue = new BigDecimal(price);
            BigDecimal discountValue = new BigDecimal(discount);
            String finalPrice = "";
            if (discountType.equals("جنيه")) {
                BigDecimal finalPriceValue = priceValue.subtract(discountValue);
                finalPrice = String.valueOf(finalPriceValue);
            } else if (discountType.equals("%")) {
                BigDecimal finalPriceValue = priceValue.multiply(BigDecimal.ONE .subtract(discountValue.divide(new BigDecimal(100))));
                finalPrice = String.valueOf(finalPriceValue);
            }
            holder.productFinal.setText(finalPrice + " " + "جنيه/" + unitWeight);
        }

        if (imageUrl.isEmpty()) {
            if (dep.equals("خضروات")) {
                holder.productImage.setImageResource(R.drawable.vegetables);
            } else if (dep.equals("فاكهة")) {
                holder.productImage.setImageResource(R.drawable.fruites);
            } else {
                holder.productImage.setImageResource(R.drawable.no_image);
            }
        } else {
            Glide.with(context).load(imageUrl).into(holder.productImage);
        }

        holder.productName.setText(productName);


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context).setTitle("تحذير!")
                        .setMessage("هل أنت متأكدة من حذف " +productName + " ?")
                        .setIcon(R.drawable.ic_warning)
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                                StorageReference storageReference = mStorageRef.child("Images/" + productList.get(position).getImageFileName());
                                storageReference.delete();
                                if (productList.get(position).isMostSold()) {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MostSold");
                                    Query query1 = reference.orderByChild("productName").equalTo(productName);
                                    query1.getRef().removeValue();
                                }
                                if (productList.get(position).isTodaysOffer()) {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TodaysOffer");
                                    Query query1 = reference.orderByChild("productName").equalTo(productName);
                                    query1.getRef().removeValue();
                                }

                                final Query query = ref.child("Products").orderByChild("productName").equalTo(productName);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                            snapshot1.getRef().setValue(null);
                                        }
                                        getMostSoldProducts();
                                        getTodaysOfferProducts();
                                        if (mostSoldList.contains(productName)) {
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MostSold");
                                            Query query1 = reference.orderByChild("productName").equalTo(productName);
                                            query1.getRef().removeValue();
                                        }
                                        if (todaysOfferList.contains(productName)) {
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TodaysOffer");
                                            Query query1 = reference.orderByChild("productName").equalTo(productName);
                                            query1.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                deleteFromCart(productName);

                            }
                        })
                .setNegativeButton("لا",null)
                .show();
                            }
        });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddNewProduct.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("add", false);
                bundle.putString("imageFileName", imageFileName);
                bundle.putString("imageUrl", imageUrl);
                Log.d("imageUrl prodct adapter", imageUrl);
                bundle.putString("dep", dep);
                bundle.putString("subDep", subDep);
                bundle.putString("productName", productName);
                bundle.putString("price", price);
                bundle.putString("unitWeight", unitWeight);
                bundle.putString("discount", discount);
                bundle.putString("discountUnit", discountType);
                bundle.putString("availableAmount", availableAmount);
                bundle.putString("count", count);
                bundle.putBoolean("todaysOffer", todaysOffer);
                bundle.putBoolean("mostSold", mostSold);
                bundle.putBoolean("isAvailable", isAvailable);
                bundle.putBoolean("isVisible", isVisible);
                bundle.putString("minimumOrderAmount",minimumOrderAmount);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });


    }

    private void updatePosition(int position,String productName) {
        Map<String, Object> newPosition = new HashMap<>();
        newPosition.put("position",position);
        Query query = ref.child("Products").orderByChild("productName").equalTo(productName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                    dataSnapshot.getRef().updateChildren(newPosition);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void deleteFromCart(final String productName) {

        for (final User user:users){
            reference.child("Cart").child(user.getUserId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        Cart cart = dataSnapshot.getValue(Cart.class);
                        assert cart != null;
                        if (cart.getProductName().equals(productName))
                           dataSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
//
//    private void removeFromCart(String productName,String userId) {
//        Query query = reference.child(userId).orderByChild("productName").equalTo(productName);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                snapshot.getRef().removeValue();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private List<User> getAllUsers() {
        final List<User> users = new ArrayList<>();
        reference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    users.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return users;
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductsViewholder extends RecyclerView.ViewHolder {
        TextView discount, productName, productFinal, discountPrice, unavailable,isVisible,isAvailable;
        ImageView productImage, unavailableImage;
        Button delete;

        public ProductsViewholder(@NonNull View itemView) {
            super(itemView);
            discount = itemView.findViewById(R.id.discount);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productFinal = itemView.findViewById(R.id.final_price);
            discountPrice = itemView.findViewById(R.id.price_before_discount);
            unavailable = itemView.findViewById(R.id.unavailable);
            unavailableImage = itemView.findViewById(R.id.unavailable_image);
            delete = itemView.findViewById(R.id.delete);
            isVisible = itemView.findViewById(R.id.isVisible);
            isAvailable = itemView.findViewById(R.id.isAvailable);


        }


    }

    private void removeAt(int position) {

        productList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeRemoved(position, productList.size());
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
