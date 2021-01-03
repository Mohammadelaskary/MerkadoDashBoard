package com.example.elmohammadymarket.Views;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.elmohammadymarket.Adapters.NeighborhoodAdapter;
import com.example.elmohammadymarket.Model.Neighborhood;
import com.example.elmohammadymarket.databinding.ActivityAddNeighborhoodBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddNeighborhood extends AppCompatActivity {
    private static final int REQUEST_CODE = 200;
    ActivityAddNeighborhoodBinding binding;
    List<Neighborhood> neighborhoods = new ArrayList<>();
    List<String> neighborhoodNames = new ArrayList<>();
    NeighborhoodAdapter adapter;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNeighborhoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getNeighborhoodNames();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        adapter = new NeighborhoodAdapter(this, neighborhoods);
        binding.neighborhoodList.setAdapter(adapter);
        binding.neighborhoodList.setLayoutManager(new LinearLayoutManager(this));
        binding.addNeighborhood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String neighbohoodName = Objects.requireNonNull(binding.neighborhoodName.getEditText()).getText().toString().trim();
                if (!neighbohoodName.isEmpty() && !neighborhoodNames.contains(neighbohoodName)) {
                    Neighborhood neighborhood = new Neighborhood(neighbohoodName);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Neighborhood");
                    reference.push().setValue(neighborhood);
                } else {
                    if (neighbohoodName.isEmpty())
                        binding.neighborhoodName.setError("ادخل اسم الحي");
                    if (neighborhoodNames.contains(neighbohoodName))
                        binding.neighborhoodName.setError("هذا الحي مضاف مسبقا");
                }
            }
        });
        binding.myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        getLocation();

                    } else {

                        ActivityCompat.requestPermissions(AddNeighborhood.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                    }
                }
            }
        });
    }

    public void getLocation() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Geocoder geocoder;
                            List<Address> addresses = new ArrayList<>();
                            Locale loc = new Locale("ar");
                            geocoder = new Geocoder(AddNeighborhood.this, loc);
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            String neighborhood = addresses.get(0).getSubAdminArea();

                            Objects.requireNonNull(binding.neighborhoodName.getEditText()).setText(neighborhood);
                        }
                    }
                });

    }

    private void getNeighborhoodNames() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Neighborhood");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.loadingShippingFee.hide();
                neighborhoods.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Neighborhood neighborhood = dataSnapshot.getValue(Neighborhood.class);
                    assert neighborhood != null;
                    neighborhoods.add(neighborhood);
                    adapter.notifyDataSetChanged();
                    binding.neighborhoodList.setVisibility(View.VISIBLE);
                }
                neighborhoodNames.clear();
                for (Neighborhood neighborhood : neighborhoods) {
                    neighborhoodNames.add(neighborhood.getNeighborhood());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }


    }
}