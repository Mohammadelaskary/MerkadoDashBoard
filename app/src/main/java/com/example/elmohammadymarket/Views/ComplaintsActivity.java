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

import com.example.elmohammadymarket.Adapters.ComplaintsAdapter;
import com.example.elmohammadymarket.Model.Complaint;
import com.example.elmohammadymarket.databinding.ActivityComplaintsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ComplaintsActivity extends AppCompatActivity {
    ActivityComplaintsBinding binding;
    List<Complaint> list = new ArrayList<>();
    ComplaintsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComplaintsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (isConnected()) {
            getData();
            adapter = new ComplaintsAdapter(this, list);
            binding.complaintsRecycler.setAdapter(adapter);
            binding.complaintsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        } else {
            binding.progressBar.hide();
            binding.noComplaintsText.setVisibility(View.VISIBLE);
            binding.noComplaintsText.setText("تأكد من الاتصال بالانترنت...");
        }
    }

    private void getData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Complaints");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.progressBar.hide();
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Complaint complaint = dataSnapshot.getValue(Complaint.class);
                    list.add(complaint);
                    adapter.notifyDataSetChanged();
                }
                if (list.isEmpty()) {
                    binding.complaintsRecycler.setVisibility(View.GONE);
                    binding.noComplaintsText.setVisibility(View.VISIBLE);
                    binding.noComplaintsText.setText("لا يوجد شكاوي");
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