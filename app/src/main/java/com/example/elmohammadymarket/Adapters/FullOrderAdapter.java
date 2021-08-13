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
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.example.elmohammadymarket.Model.Order;
import com.example.elmohammadymarket.Model.OrderProduct;
import com.example.elmohammadymarket.Model.PharmacyOrder;
import com.example.elmohammadymarket.OnCallClickListener;
import com.example.elmohammadymarket.OnPrintClickListener;
import com.example.elmohammadymarket.R;
import com.example.elmohammadymarket.Views.PharmacyDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.core.content.ContextCompat.*;

public class FullOrderAdapter extends RecyclerView.Adapter<FullOrderAdapter.FullOrderViewHolder>  {
    private static final int PRINT_REQUEST_CODE = 300;
    Context context;
    List<Order> orders;
    boolean newOrder;
    private OnCallClickListener onCallClickListener;
    private OnPrintClickListener onPrintClickListener;


    public FullOrderAdapter(Context context,List<Order> orders, boolean newOrder, OnCallClickListener onCallClickListener,OnPrintClickListener onPrintClickListener) {
        this.context = context;
        this.newOrder = newOrder;
        this.orders = orders;
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
    public void onBindViewHolder(@NonNull final FullOrderViewHolder holder, int position) {
        int currentPosition = holder.getAdapterPosition();
        String customerName, time,date,address,mobileNumber,phoneNumber,sum ,discount,overAllDiscount,netCost,shipping,userId;
        final boolean isDone,isSeen,isShiped;
        Order order = orders.get(currentPosition);
        FullOrder fullOrder = order.getFullOrder();
        List<PharmacyOrder> pharmacyOrders = order.getPharmacyOrders();
        List<OrderProduct> orderProducts = new ArrayList<>();
        final int id;
        id = orders.get(currentPosition).getId();
        int numberOfPharmacyItems = 0;
        if (pharmacyOrders!=null) {
            numberOfPharmacyItems = pharmacyOrders.size();
            holder.pharmacyContainer.setVisibility(View.VISIBLE);
        } else {
            holder.pharmacyContainer.setVisibility(View.GONE);
        }
        holder.numberOfPharmacyItems.setText(String.valueOf(numberOfPharmacyItems));
        if (fullOrder!=null){
            customerName = fullOrder.getUsername();
            time = fullOrder.getTime();
            date = fullOrder.getDate();
            address = fullOrder.getAddress();
            mobileNumber = fullOrder.getMobilePhone();
            sum = fullOrder.getSum();
            discount = fullOrder.getDiscount();
            overAllDiscount = fullOrder.getOverAllDiscount();
            phoneNumber = fullOrder.getPhoneNumber();
            netCost = fullOrder.getTotalCost();
            orderProducts = fullOrder.getOrders();
            userId = fullOrder.getUserId();
            shipping = fullOrder.getShipping();
            isDone = fullOrder.isDone();
            OrdersAdapter adapter = new OrdersAdapter(context, orderProducts);
            holder.orders.setAdapter(adapter);
            holder.orders.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            holder.totalCost.setText(netCost);
            holder.shipping.setText(shipping);
            holder.sumText.setText(sum);
            holder.discountText.setText(discount);
            holder.overallDiscount.setText(overAllDiscount);
            holder.calculations.setVisibility(View.VISIBLE);
            fullOrder.setSeen(true);
            updateOrder(fullOrder,id);
            if (pharmacyOrders!=null) {
                for (PharmacyOrder pharmacyOrder : pharmacyOrders) {
                    pharmacyOrder.setSeen(true);
                }
                updatePharmacyOrder(id, pharmacyOrders);
            }
        } else {
            customerName = pharmacyOrders.get(0).getShippingData().getUsername();
            time = pharmacyOrders.get(0).getTime();
            date = pharmacyOrders.get(0).getDate();
            address = pharmacyOrders.get(0).getShippingData().getAddress();
            mobileNumber = pharmacyOrders.get(0).getShippingData().getMobileNumber();
            phoneNumber = pharmacyOrders.get(0).getShippingData().getPhoneNumber();
            isDone = pharmacyOrders.get(0).isDone();
            String pharmacyCost = pharmacyOrders.get(0).getPharmacyCost();
            userId = pharmacyOrders.get(0).getUserId();
            for (PharmacyOrder pharmacyOrder:pharmacyOrders){
                pharmacyOrder.setSeen(true);
            }
            updatePharmacyOrder(id,pharmacyOrders);
            holder.calculations.setVisibility(View.GONE);
            holder.paymentTitle.setText("التوصيــــــــــــــــــــــــل:");
            holder.totalCost.setText(pharmacyCost);
        }
        holder.customerName.setText(customerName);
        holder.time.setText(time);
        holder.date.setText(date);
        holder.address.setText(address);
        holder.phoneNumber.setText(phoneNumber);
        holder.mobileNumber.setText(mobileNumber);



        OrdersAdapter adapter = new OrdersAdapter(context, orderProducts);
        holder.orders.setAdapter(adapter);
        holder.orders.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.mobileNumber.setText(mobileNumber);
        holder.orderId.setText(String.valueOf(id));
        holder.call.setOnClickListener(view -> onCallClickListener.onCallClickListener(mobileNumber));




        if (!newOrder) {
            holder.done.setVisibility(View.GONE);
            holder.sendOrder.setVisibility(View.GONE);
        } else {
            holder.done.setChecked(false);
            holder.done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    moveOrderToDoneOrders(id, order);
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
                        final int itemId = item.getItemId();
                        switch (itemId){
                            case R.id.send_order:{
                                onCallClickListener.onSendClickListener(holder.orderLayout,  mobileNumber);
                                if (fullOrder!=null) {
                                    fullOrder.setShiped(true);
                                    updateOrder(fullOrder,id);
                                }
                                for (PharmacyOrder pharmacyOrder:pharmacyOrders){
                                    pharmacyOrder.setShiped(true);
                                }
                                updatePharmacyOrder(id,pharmacyOrders);

                            } break;
                            case R.id.print:{
                                try {
                                    onPrintClickListener.onPrintClickListener(fullOrder);
                                } catch (EscPosConnectionException | EscPosEncodingException | EscPosBarcodeException | EscPosParserException e) {
                                    e.printStackTrace();
                                }
                            } break;
                            case R.id.delete_order:{
                                if (pharmacyOrders!=null) {
                                    for (PharmacyOrder order : pharmacyOrders) {
                                        deleteImage(order.getImageFileName(),id);
                                    }
                                }
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                Query query = ref.child("Orders").orderByChild("id").equalTo(id);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            snapshot.getRef().removeValue();
                                            removeAt(currentPosition);
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
        PharmacyDialog pharmacyDialog = new PharmacyDialog(context,android.R.style.Theme_Light,pharmacyOrders);
        pharmacyDialog.setTitle("طلبات الصيدلية");
        holder.openPharmacy.setOnClickListener(v->{
            pharmacyDialog.show();
        });
        if (!userId.equals("")){
            holder.orderCanceled.setVisibility(View.GONE);
            holder.orderLayout.setEnabled(true);
        } else {
            holder.orderCanceled.setVisibility(View.VISIBLE);
            holder.orderLayout.setEnabled(false);
        }


    }

    private void updatePharmacyOrder(int id, List<PharmacyOrder> pharmacyOrders) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        Query query = reference.orderByChild("id").equalTo(id);
        Map<String,Object> pharmacyMap = new HashMap<>();
        pharmacyMap.put("pharmacyOrders",pharmacyOrders);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    dataSnapshot.getRef().updateChildren(pharmacyMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removePharmacy(int id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        Query query = reference.orderByChild("id").equalTo(id);
        Map<String,Object> pharMap = new HashMap<>();
        pharMap.put("pharmacyOrder",null);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    dataSnapshot.getRef().updateChildren(pharMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void moveOrderToDoneOrders(int id,Order order) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference;
        List<PharmacyOrder> pharmacyOrders = order.getPharmacyOrders();
        reference = FirebaseDatabase.getInstance().getReference("Done orders");
        reference.push().setValue(order);
        Query query = ref.child("Orders").orderByChild("id").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
                for (PharmacyOrder pharmacyOrder:pharmacyOrders){
                    String imageFileName = pharmacyOrder.getImageFileName();
                    deleteImage(imageFileName,id);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteImage(String filename,int id) {
        String storageUrl = "Pharmacy/"+filename;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(storageUrl);
        storageReference.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                removePharmacy(id);
            }
        });
    }



    private void updateOrder(FullOrder fullOrder,int id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        Query query = reference.orderByChild("id").equalTo(id);
        Map<String,Object> fullOrderMap = new HashMap<>();
        fullOrderMap.put("fullOrder",fullOrder);
        Log.d("orderId",fullOrder.getId()+"");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    dataSnapshot.getRef().updateChildren(fullOrderMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }



    static class FullOrderViewHolder extends RecyclerView.ViewHolder {
        TextView numberOfPharmacyItems, paymentTitle, orderId, time, mobileNumber, date, customerName, address, phoneNumber, totalCost, sumText, discountText, overallDiscount, shipping;
        RecyclerView orders;
        CheckBox done;
        ImageButton  call, sendOrder;
        LinearLayout orderLayout,orderCanceled,calculations;
        ImageButton deleteOrder;
        CardView cardView;
        ConstraintLayout pharmacyContainer;
        MaterialButton openPharmacy;

        public FullOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
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
            paymentTitle = itemView.findViewById(R.id.payment_title);
            calculations = itemView.findViewById(R.id.calculations);
            pharmacyContainer = itemView.findViewById(R.id.pharmacy_container);
            numberOfPharmacyItems = itemView.findViewById(R.id.number_of_pharmacy_items);
            openPharmacy = itemView.findViewById(R.id.pharmacy);
        }
    }

    public void removeAt(int position) {
        orders.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, orders.size());
    }
}
