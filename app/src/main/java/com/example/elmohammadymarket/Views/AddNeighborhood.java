package com.example.elmohammadymarket.Views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.elmohammadymarket.Model.City;
import com.example.elmohammadymarket.Model.Governorate;
import com.example.elmohammadymarket.Model.Neighborhood;
import com.example.elmohammadymarket.R;
import com.example.elmohammadymarket.databinding.ActivityAddNeighborhoodBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class AddNeighborhood extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    ActivityAddNeighborhoodBinding binding;
    FirebaseDatabase database;
    List<String> governoratesNames = new ArrayList<>();
    List<String> citiesNames = new ArrayList<>();
    ArrayAdapter<String> governorateAdapter1;
    ArrayAdapter<String> governorateAdapter2;
    ArrayAdapter<String> citiesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNeighborhoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        database = FirebaseDatabase.getInstance();


        attachSpinnersToAdapters();
        connectViewsToOnClick();
        getGovernoratesNames();
        addTextWatcherToEditTexts();

    }

    private void getDefaultValues() {
        String governorateName = governoratesNames.get(0);
        getCityList(governorateName);
    }

    private void getCityList(String governorateName) {
        binding.getCitiesProgress.show();
        DatabaseReference reference = database.getReference("City");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.getCitiesProgress.hide();
                citiesNames.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    City city = dataSnapshot.getValue(City.class);
                    if (city.getGovernorate().equals(governorateName))
                        citiesNames.add(city.getCity());
                    citiesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FancyToast.makeText(AddNeighborhood.this,"حدث خطأ أثناء إيجاد اسماء المدن!!",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false);
                Log.d("gettingcitiesNames", error.getMessage());
            }
        });
    }

    private void addTextWatcherToEditTexts() {
        binding.governorateName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.governorateName.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.governorateName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.cityName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.cityName.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.cityName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.neighborhoodName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.neighborhoodName.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.neighborhoodName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getGovernoratesNames() {
        binding.governorateNamesProgress.show();
        binding.governorateNamesProgress1.show();
        DatabaseReference reference = database.getReference("Governorate");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                governoratesNames.clear();
                binding.governorateNamesProgress.hide();
                binding.governorateNamesProgress1.hide();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Governorate governorate = dataSnapshot.getValue(Governorate.class);
                    governoratesNames.add(governorate.getGovernorate());
                }
                if (!governoratesNames.isEmpty())
                    getDefaultValues();
                governorateAdapter1.notifyDataSetChanged();
                governorateAdapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FancyToast.makeText(AddNeighborhood.this,"حدث خطأ أثناء إيجاد اسماء المحافظات!!",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false);
                Log.d("gettingGovernorateNames", error.getMessage());
            }
        });
    }



    private void attachSpinnersToAdapters() {
        governorateAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1,governoratesNames);
        governorateAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1,governoratesNames);
        citiesAdapter       = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1,citiesNames);
        binding.governorateNameSpinner.setAdapter(governorateAdapter1);
        binding.governorateNameSpinner2.setAdapter(governorateAdapter2);
        binding.cityNameSpinner.setAdapter(citiesAdapter);
    }

    private void connectViewsToOnClick() {
        binding.addGovernorate.setOnClickListener(this);
        binding.addCity.setOnClickListener(this);
        binding.addNeighborhood.setOnClickListener(this);
        binding.governorateNameSpinner2.setOnItemSelectedListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_governorate:{
                String governorateName = binding.governorateName.getEditText().getText().toString().trim();
                if (governorateName.isEmpty()){
                    binding.governorateName.setError("من فضلك أدخل اسم المحافظة..");
                } else {
                    if (!governoratesNames.contains(governorateName)){
                        Governorate governorate = new Governorate(governorateName);
                        binding.addGovernorateProgress.show();
                        addGovernorate(governorate);
                    } else {
                        binding.governorateName.setError("اسم المحافظة مضاف مسبقا..");
                    }
                }
            } break;
            case R.id.add_city:{
                String governorateName = binding.governorateNameSpinner.getSelectedItem().toString();
                String cityName        = binding.cityName.getEditText().getText().toString().trim();
                if (cityName.isEmpty()){
                    binding.cityName.setError("من فضلك أدخل اسم المدينة..");
                } else {
                    City city = new City(governorateName,cityName);
                    binding.addCityProgress.show();
                    addCity(city);
                }
            } break;
            case R.id.add_neighborhood:{
                String governorateName = binding.governorateNameSpinner2.getSelectedItem().toString();
                String cityName        = binding.cityNameSpinner.getSelectedItem().toString();
                String neighborhoodName = binding.neighborhoodName.getEditText().getText().toString().trim();
                if (neighborhoodName.isEmpty()){
                    binding.neighborhoodName.setError("من فضلك أدخل اسم الحي..");
                } else {
                    Neighborhood neighborhood = new Neighborhood(governorateName,cityName,neighborhoodName);
                    binding.addNeighborhoodProgress.show();
                    addNeighborhood(neighborhood);
                }
            } break;
        }
    }

    private void addNeighborhood(Neighborhood neighborhood) {
        DatabaseReference reference = database.getReference("Neighborhood");
        reference.push().setValue(neighborhood).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                binding.addNeighborhoodProgress.hide();
                if (task.isSuccessful())
                    FancyToast.makeText(AddNeighborhood.this,"تم إضافة الحي",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
                else {
                    FancyToast.makeText(AddNeighborhood.this, "حدث خطأ في إضافة الحي", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    Log.d("addingCityError",task.getException().getMessage());
                }
            }
        });
    }

    private void addCity(City city) {
        DatabaseReference reference = database.getReference("City");
        reference.push().setValue(city).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                binding.addCityProgress.hide();
                if (task.isSuccessful())
                    FancyToast.makeText(AddNeighborhood.this,"تم إضافة المدينة",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
                else {
                    FancyToast.makeText(AddNeighborhood.this, "حدث خطأ في إضافة المدينة", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    Log.d("addingCityError",task.getException().getMessage());
                }
            }
        });
    }

    private void addGovernorate(Governorate governorateName) {
        DatabaseReference reference = database.getReference("Governorate");
        reference.push().setValue(governorateName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                binding.addGovernorateProgress.hide();
                if (task.isSuccessful())
                    FancyToast.makeText(AddNeighborhood.this,"تم إضافة المحافظة",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
                else {
                    FancyToast.makeText(AddNeighborhood.this, "حدث خطأ في إضافة المحافظة", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    Log.d("addingGovernorateError",task.getException().getMessage());
                }
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String governorate = binding.governorateNameSpinner2.getSelectedItem().toString();
        getCityList(governorate);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}