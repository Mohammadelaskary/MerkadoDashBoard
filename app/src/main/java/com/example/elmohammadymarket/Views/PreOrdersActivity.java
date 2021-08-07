package com.example.elmohammadymarket.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.elmohammadymarket.Adapters.DatesAdapter;
import com.example.elmohammadymarket.Model.FullOrder;
import com.example.elmohammadymarket.databinding.ActivityPreOrdersBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PreOrdersActivity extends AppCompatActivity {
    ActivityPreOrdersBinding binding;
    List<String> dates = new ArrayList<>();
    List<String> years = new ArrayList<>();
    DatesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        fillyears();
        getDates();
        initDatesRecycler();

        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String day = String.valueOf(binding.day.getSelectedItemId());
                int month = (int) binding.month.getSelectedItemId();
                NumberFormat format = new DecimalFormat("00");
                String formatedMonth = convertToEnglish(format.format(month));
                String year = String.valueOf(binding.year.getSelectedItem());
                String date = day + "/" + formatedMonth + "/" + year;
                Intent intent = new Intent(PreOrdersActivity.this, PreviousOrders.class);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        });

    }

    private void initDatesRecycler() {
        adapter = new DatesAdapter(dates, this);
        binding.datesRec.setAdapter(adapter);
        binding.datesRec.setLayoutManager(new LinearLayoutManager(this));
    }

    public String convertToEnglish(String value) {
        return (((((((((((value + "")
                .replaceAll("١", "1")).replaceAll("٢", "2"))
                .replaceAll("٣", "3")).replaceAll("٤", "4"))
                .replaceAll("٥", "5")).replaceAll("٦", "6"))
                .replaceAll("٧", "7")).replaceAll("٨", "8"))
                .replaceAll("٩", "9")).replaceAll("٠", "0"));
    }

    private void fillyears() {
        years.add("سنة");
        for (int i = 2020; i < 2500; i++) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years);
        binding.year.setAdapter(adapter);
    }

    private void getDates() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Done orders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dates.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FullOrder order = dataSnapshot.getValue(FullOrder.class);
                    assert order != null;
                    if (!dates.contains(order.getDate()))
                        dates.add(order.getDate());
                    Collections.sort(dates, new StringDateComparator());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    class StringDateComparator implements Comparator<String> {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        public int compare(String lhs, String rhs) {
            int res = 0;
            try {
                res = dateFormat.parse(rhs).compareTo(dateFormat.parse(lhs));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return res;
        }
    }
}