package com.example.elmohammadymarket.Adapters;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.example.elmohammadymarket.Model.FullOrder;
import com.example.elmohammadymarket.Model.OrderProduct;
import com.example.elmohammadymarket.OnCallClickListener;
import com.example.elmohammadymarket.OnPrintClickListener;
import com.example.elmohammadymarket.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.core.content.ContextCompat.*;

public class FullOrderAdapter extends RecyclerView.Adapter<FullOrderAdapter.FullOrderViewHolder>  {
    private static final int PRINT_REQUEST_CODE = 300;
    Context context;
    List<FullOrder> ordersList;
    boolean newOrder;
    private OnCallClickListener onCallClickListener;
    private OnPrintClickListener onPrintClickListener;

    String id;

    public FullOrderAdapter(Context context,List<FullOrder> ordersList, boolean newOrder, OnCallClickListener onCallClickListener,OnPrintClickListener onPrintClickListener) {
        this.context = context;
        this.newOrder = newOrder;
        this.ordersList = ordersList;
        this.onCallClickListener = onCallClickListener;
        this.onPrintClickListener = onPrintClickListener;
    }




    public Context getContext() {
        return context;
    }

    @NonNull
    @Override
    public FullOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.full_order_item, parent, false);
        return new FullOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FullOrderViewHolder holder, final int position) {
        final String customerName = ordersList.get(position).getUsername();
        final String time = ordersList.get(position).getTime();
        final String date = ordersList.get(position).getDate();
        final String address = ordersList.get(position).getAddress();
        final String mobileNumber = ordersList.get(position).getMobilePhone();
        final String sum = ordersList.get(position).getSum();
        final String discount = ordersList.get(position).getDiscount();
        final String overAllDiscount = ordersList.get(position).getOverAllDiscount();
        final String phoneNumber = ordersList.get(position).getPhoneNumber();
        final String netCost = ordersList.get(position).getTotalCost();
        final List<OrderProduct> list = ordersList.get(position).getOrders();
        final boolean isDone = ordersList.get(position).isDone();
        final boolean shiped = ordersList.get(position).isShiped();
        final boolean seen = ordersList.get(position).isSeen();
        final String shipping = ordersList.get(position).getShipping();
        id = ordersList.get(position).getId();
        final String userId = ordersList.get(position).getUserId();
        final boolean isStillAvailable = ordersList.get(position).isStillAvailable();
        holder.customerName.setText(customerName);
        holder.time.setText(time);
        holder.date.setText(date);
        holder.address.setText(address);
        holder.phoneNumber.setText(phoneNumber);
        holder.totalCost.setText(netCost);
        OrdersAdapter adapter = new OrdersAdapter(context, list);
        holder.orders.setAdapter(adapter);
        holder.orders.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.shipping.setText(shipping);
        holder.sumText.setText(sum);
        holder.discountText.setText(discount);
        holder.overallDiscount.setText(overAllDiscount);
        holder.mobileNumber.setText(mobileNumber);
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCallClickListener.onCallClickListener(mobileNumber);

            }
        });

        markAsSeen(id);

        if (isDone) {
            holder.done.setChecked(true);
            holder.done.setEnabled(false);
            holder.sendOrder.setVisibility(View.GONE);
        } else {
            holder.done.setChecked(false);

            holder.done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    FullOrder order = new FullOrder(date, time, true, shiped, seen, customerName, mobileNumber, phoneNumber, address, list, String.valueOf(sum), String.valueOf(discount), String.valueOf(overAllDiscount), String.valueOf(shipping), String.valueOf(netCost),  String.valueOf(userId),isStillAvailable);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Done orders");
                    reference.push().setValue(order);
                    Query query = ref.child("Orders").orderByChild("id").equalTo(id);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                snapshot.getRef().removeValue();
                                removeAt(position);
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            });
        }

        holder.sendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu = new PopupMenu(context,view);
                menu.getMenuInflater().inflate(R.menu.orders_menu,menu.getMenu());
                menu.show();
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.send_order:{
                                onCallClickListener.onSendClickListener(holder.orderLayout,  mobileNumber);
                                Map<String, Object> shiped = new HashMap<>();
                                shiped.put("shiped", true);
                                updateOrder(shiped,id);
                            } break;
                            case R.id.print:{
                                try {
                                    onPrintClickListener.onPrintClickListener(ordersList.get(position));
                                } catch (EscPosConnectionException | EscPosEncodingException | EscPosBarcodeException | EscPosParserException e) {
                                    e.printStackTrace();
                                }
                            } break;
                            case R.id.delete_order:{
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                Query query = ref.child("Orders").orderByChild("id").equalTo(id);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            snapshot.getRef().removeValue();
                                            removeAt(position);
                                        }
                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } break;
                        }
                        return false;
                    }
                });
            }
        });

        if (isStillAvailable){
            holder.orderCanceled.setVisibility(View.GONE);
            holder.orderLayout.setEnabled(true);
        } else {
            holder.orderCanceled.setVisibility(View.VISIBLE);
            holder.orderLayout.setEnabled(false);
        }



    }


    private void markAsSeen(String id) {
        final Map<String, Object> map = new HashMap<>();
        map.put("seen", true);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        Query query = reference.orderByChild("id").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    dataSnapshot.getRef().updateChildren(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateOrder(final Map<String, Object> map,String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        Query query = reference.orderByChild("id").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    dataSnapshot.getRef().updateChildren(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }



    static class FullOrderViewHolder extends RecyclerView.ViewHolder {
        TextView time, mobileNumber, date, customerName, address, phoneNumber, totalCost, sumText, discountText, overallDiscount, shipping;
        RecyclerView orders;
        CheckBox done;
        ImageButton  call, sendOrder;
        LinearLayout orderLayout,orderCanceled;
        ImageButton deleteOrder;
        CardView cardView;

        public FullOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.order_time);
            date = itemView.findViewById(R.id.order_date);
            customerName = itemView.findViewById(R.id.customer_name);
            address = itemView.findViewById(R.id.customer_address);
            phoneNumber = itemView.findViewById(R.id.phone_number);
            orders = itemView.findViewById(R.id.orders);
            done = itemView.findViewById(R.id.done);
            totalCost = itemView.findViewById(R.id.net_cost);
            mobileNumber = itemView.findViewById(R.id.mobile_phone);
            sumText = itemView.findViewById(R.id.sum);
            discountText = itemView.findViewById(R.id.discount);
            overallDiscount = itemView.findViewById(R.id.over_all_discount);
            shipping = itemView.findViewById(R.id.shipping_fee);
            call = itemView.findViewById(R.id.call);
            sendOrder = itemView.findViewById(R.id.send_order);
            orderLayout = itemView.findViewById(R.id.order_layout);
            orderCanceled = itemView.findViewById(R.id.order_canceled);
            deleteOrder = itemView.findViewById(R.id.delete_order);
            cardView = itemView.findViewById(R.id.card);
        }
    }

    public void removeAt(int position) {
        ordersList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, ordersList.size());
    }
}
