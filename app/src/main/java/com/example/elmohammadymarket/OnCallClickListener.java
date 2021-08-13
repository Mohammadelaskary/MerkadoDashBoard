package com.example.elmohammadymarket;

import android.widget.LinearLayout;

import com.example.elmohammadymarket.Model.PharmacyOrder;

import java.util.List;

public interface OnCallClickListener {
    public void onCallClickListener(String mobileNumber);

    void onSendClickListener(LinearLayout layout, String mobileNumber);
}
