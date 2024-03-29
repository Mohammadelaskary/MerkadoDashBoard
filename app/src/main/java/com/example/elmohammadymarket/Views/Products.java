package com.example.elmohammadymarket.Views;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elmohammadymarket.Adapters.ProductsAdapter;
import com.example.elmohammadymarket.Database.Product;
import com.example.elmohammadymarket.OnDeleteProductClicked;
import com.example.elmohammadymarket.databinding.ActivityProductsBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Products extends AppCompatActivity {
    ActivityProductsBinding binding;
    ProductsAdapter adapter;
    List<Product> products;
    String depName, subdep;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        products = new ArrayList<>();
        Intent intent = getIntent();
        depName = intent.getStringExtra("depName");
        subdep = intent.getStringExtra("subdep");
        Log.d("depname", depName);
        Log.d("subdep", subdep);
        binding.depName.setText(depName);
        binding.subdep.setText(subdep);

        if (isConnected()) {
            binding.progressBar.setVisibility(View.VISIBLE);
            getProducts(depName);

        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.products.setVisibility(View.GONE);
            binding.noProductsText.setVisibility(View.VISIBLE);
            binding.noProductsText.setText("تأكد من الاتصال بالانترنت");
        }


        adapter = new ProductsAdapter(this, products);
        binding.products.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.products.setLayoutManager(layoutManager);


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


    private void getProducts(final String string) {

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference();
        Query query = reference.child("Products").orderByChild("dep").equalTo(string);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("product", "hi i am here");
                if (snapshot.exists()) {
                    binding.products.setVisibility(View.VISIBLE);
                    binding.noProductsText.setVisibility(View.GONE);
                    products.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Product product = snapshot1.getValue(Product.class);
                        if (product.getSubDep().equals(subdep)) {
                            products.add(product);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    int numberOfProducts = products.size();
                    Objects.requireNonNull(getSupportActionBar()).setTitle("عدد المنتجات : " + numberOfProducts);


                } else {
                    binding.products.setVisibility(View.GONE);
                    binding.noProductsText.setVisibility(View.VISIBLE);
                    binding.noProductsText.setText("لا يوجد منتجات");
                }
                binding.progressBar.hide();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
    protected void onPause() {
        super.onPause();

    }
}