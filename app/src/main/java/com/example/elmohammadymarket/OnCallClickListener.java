package com.example.elmohammadymarket;

import android.widget.LinearLayout;

public interface OnCallClickListener {
    public void onCallClickListener(String mobileNumber);

    void onSendClickListener(LinearLayout layout,  String mobileNumber);
}
