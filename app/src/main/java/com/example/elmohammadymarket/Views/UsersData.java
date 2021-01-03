package com.example.elmohammadymarket.Views;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elmohammadymarket.Adapters.UsersAdapter;
import com.example.elmohammadymarket.Model.User;
import com.example.elmohammadymarket.databinding.ActivityUsersDataBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UsersData extends AppCompatActivity {
    ActivityUsersDataBinding binding;
    List<User> list = new ArrayList<>();
    UsersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setTitle("بيانات العملاء");
        if (isConnected()) {
            getData();
            adapter = new UsersAdapter(this, list);
            binding.usersRecycler.setAdapter(adapter);
            binding.usersRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            binding.usersRecycler.setHasFixedSize(true);
        } else {
            binding.progressBar.hide();
            binding.noUsersText.setVisibility(View.VISIBLE);
            binding.noUsersText.setText("تأكد من الاتصال بالانترنت...");
        }
    }

    private void getData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.progressBar.hide();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    list.add(user);
                    adapter.notifyDataSetChanged();
                }
                if (list.isEmpty()) {
                    binding.noUsersText.setVisibility(View.VISIBLE);
                    binding.noUsersText.setText("لا يوجد عملاء");
                }
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
}