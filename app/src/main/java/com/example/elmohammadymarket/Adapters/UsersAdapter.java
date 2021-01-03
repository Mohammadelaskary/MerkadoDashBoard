package com.example.elmohammadymarket.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elmohammadymarket.Model.User;
import com.example.elmohammadymarket.R;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    Context context;
    List<User> list;

    public UsersAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public UsersAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UsersAdapter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String customerName = list.get(position).getCustomerName();
        String phoneNumber = list.get(position).getMobileNumber();
        String governorate = list.get(position).getGovernorate();
        String city = list.get(position).getCity();
        String neighborhood = list.get(position).getNeighborhood();
        String streetName = list.get(position).getStreetName();
        String buildingNo = list.get(position).getBuildingNo();
        String appartmentNo = list.get(position).getAppartmentNo();
        String famousMark = list.get(position).getFamousMark();
        String address;
        if (famousMark==null)
            address = buildingNo + " " + streetName +" ، "+ neighborhood +" ، "+city+" ، "+governorate+" شقة رقم "+ appartmentNo;
        else
            address = buildingNo + " " + streetName +" ، "+ neighborhood +" ، "+city+" ، "+governorate+" بالقرب من "+famousMark+" شقة رقم "+ appartmentNo;
        int pointsNum = list.get(position).getCount();
        holder.neighborhoodName.setText(neighborhood);
        holder.address.setText(address);
        holder.phoneNumber.setText(phoneNumber);
        holder.customerName.setText(customerName);
        holder.points.setText(String.valueOf(pointsNum));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, phoneNumber, address, neighborhoodName, points;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.customer_name);
            phoneNumber = itemView.findViewById(R.id.phone_number);
            address = itemView.findViewById(R.id.address);
            neighborhoodName = itemView.findViewById(R.id.neighborhood_name);
            points = itemView.findViewById(R.id.points);
        }
    }
}