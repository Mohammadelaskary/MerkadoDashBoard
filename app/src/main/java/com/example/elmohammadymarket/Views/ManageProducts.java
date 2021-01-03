package com.example.elmohammadymarket.Views;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elmohammadymarket.Adapters.DepsAndSubdepsAdapter;
import com.example.elmohammadymarket.Database.DepartmentNames;
import com.example.elmohammadymarket.Model.SubDeparment;
import com.example.elmohammadymarket.databinding.ActivityManageProductsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManageProducts extends AppCompatActivity {
    ActivityManageProductsBinding binding;
    List<String> depList;
    HashMap<String, List<String>> depsSubdeps;
    DepsAndSubdepsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageProductsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().setTitle("التعديل على المنتجات");


        depList = new ArrayList<>();
        depsSubdeps = new HashMap<>();
        adapter = new DepsAndSubdepsAdapter(depList, depsSubdeps, this);
        binding.depsSubdeps.setAdapter(adapter);
        binding.depsSubdeps.setVisibility(View.GONE);
        binding.depsSubdeps.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Intent intent = new Intent(ManageProducts.this, Products.class);
                intent.putExtra("depName", depList.get(i));
                intent.putExtra("subdep", depsSubdeps.get(depList.get(i)).get(i1));
                startActivity(intent);
                return true;
            }
        });


        if (isConnected()) {
            new GetDepsAndSubdeps().execute();
            binding.progressBar.show();
        } else {
            binding.progressBar.hide();
            binding.noProductsText.setVisibility(View.VISIBLE);
            binding.noProducts.setVisibility(View.VISIBLE);
            binding.noProductsText.setText("تأكد من الاتصال بالانترنت...");
            binding.depsSubdeps.setVisibility(View.GONE);
            binding.noProducts.setVisibility(View.GONE);
        }


    }


    public class GetDepsAndSubdeps extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            getDepsAndSubdeps();
            return null;
        }
    }

    private void getDepsAndSubdeps() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DepartmentsNames");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    depList.clear();
                    binding.noProductsText.setVisibility(View.GONE);
                    binding.depsSubdeps.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        DepartmentNames names = snapshot1.getValue(DepartmentNames.class);
                        depList.add(names.getDepName());
                        adapter.notifyDataSetChanged();
                        getSubdeps(depList);
                    }
                } else {
                    binding.noProductsText.setVisibility(View.VISIBLE);
                    binding.depsSubdeps.setVisibility(View.GONE);
                    binding.noProductsText.setText("لا يوجد أقسام");
                }
                binding.progressBar.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSubdeps(final List<String> depList) {
        final List<SubDeparment> subDeparments = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SubDeps");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subDeparments.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    SubDeparment subDeparment = snapshot1.getValue(SubDeparment.class);
                    subDeparments.add(subDeparment);
                }
                for (String dep : depList) {
                    List<String> subdeps = new ArrayList<>();
                    for (SubDeparment subDeparment : subDeparments) {
                        if (subDeparment.getDepName().equals(dep)) {
                            subdeps.add(subDeparment.getSubdepName());
                        }
                    }
                    depsSubdeps.put(dep, subdeps);
                    adapter.notifyDataSetChanged();
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
