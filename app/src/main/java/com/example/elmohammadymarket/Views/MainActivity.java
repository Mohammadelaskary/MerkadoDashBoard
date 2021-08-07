package com.example.elmohammadymarket.Views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.elmohammadymarket.Database.MyDatabase;
import com.example.elmohammadymarket.Model.Complaint;
import com.example.elmohammadymarket.NotificationPackage.OrdersNotificationsService;
import com.example.elmohammadymarket.R;
import com.example.elmohammadymarket.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final int PERMISSION_BLUETOOTH = 100;
    ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    public static MyDatabase db;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 0);


        db = MyDatabase.getInstance(this);
        getOrders();
        getComplaints();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            startForegroundService(new Intent(MainActivity.this, OrdersNotificationsService.class));
        }

//        Intent intent = getIntent();
//        final String email = intent.getStringExtra("email");
//        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
////        final String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//        assert mUser != null;
//        mUser.getIdToken(true)
//                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
//                    public void onComplete(@NonNull Task<GetTokenResult> task) {
//                        if (task.isSuccessful()) {
//                            String token = Objects.requireNonNull(task.getResult()).getToken();
//                            final HashMap<String, Object> tokenMap = new HashMap<>();
//                            tokenMap.put("token", token);
//                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//                            Query query = reference.child("Admins").orderByChild("email").equalTo(email);
//                            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                                        snapshot1.getRef().updateChildren(tokenMap);
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//                            Log.d("token", Objects.requireNonNull(task.getResult().getToken()));
//                        } else {
//                            // Handle error -> task.getException();
//                        }
//                    }
//                });

        binding.addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNewProduct.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("add", true);
                bundle.putBoolean("isVisible", true);
                bundle.putBoolean("isAvailable", true);
                bundle.putString("imageFileName", "");
                bundle.putString("imageUrl", "");
                bundle.putString("dep", "");
                bundle.putString("subDep", "");
                bundle.putString("productName", "");
                bundle.putString("price", "");
                bundle.putString("unitWeight", "");
                bundle.putString("discount", "");
                bundle.putString("discountUnit", "");
                bundle.putString("availableAmount", "0");
                bundle.putString("count", "0");
                bundle.putString("todaysOffer", "false");
                bundle.putString("mostSold", "false");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        binding.addAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddNewAd.class));
            }
        });

        binding.manageProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ManageProducts.class));

            }
        });

        binding.myRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OrdersActivity.class));

            }
        });
        binding.addDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddDepartment.class));
            }
        });
        binding.lastRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PreOrdersActivity.class));
            }
        });

        binding.addAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddAdmin.class));
            }
        });

        binding.addDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddDiscount.class));
            }
        });

        binding.addSubdep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddSubDepartment.class));
            }
        });
        binding.changeShippingFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ChangeShipping.class));
            }
        });
        binding.addNeighborhood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddNeighborhood.class));
            }
        });

        binding.redeemPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RedeemPoints.class));
            }
        });
        binding.complaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ComplaintsActivity.class));
            }
        });
        binding.users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UsersData.class));
            }
        });
        binding.contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ContactActivity.class));
            }
        });
    }

    private void getComplaints() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Complaints");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Complaint complaint = snapshot.getValue(Complaint.class);
                        assert complaint != null;
                        if (!complaint.isSeen()) {
                            binding.complaintsExist.setVisibility(View.VISIBLE);
                            binding.complaints.setBackgroundResource(R.drawable.requests_exist_background);
                        }
                    }
                } else {
                    binding.complaintsExist.setVisibility(View.GONE);
                    binding.complaints.setBackgroundResource(R.drawable.button_background);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout: {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();

            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getOrders() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Orders");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    binding.ordersExist.setVisibility(View.VISIBLE);
                    binding.myRequests.setBackgroundResource(R.drawable.requests_exist_background);
                } else {
                    binding.ordersExist.setVisibility(View.GONE);
                    binding.myRequests.setBackgroundResource(R.drawable.button_background);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkPermission(String permission, int requestCode) {

        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(),
                permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(
                            MainActivity.this,
                            new String[]{permission},
                            requestCode);


        } else {


        }

    }
}