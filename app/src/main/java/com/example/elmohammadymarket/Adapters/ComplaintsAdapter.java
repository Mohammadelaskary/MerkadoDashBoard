package com.example.elmohammadymarket.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elmohammadymarket.Model.Complaint;
import com.example.elmohammadymarket.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.ComplaintViewHolder> {
    Context context;
    List<Complaint> list;

    public ComplaintsAdapter(Context context, List<Complaint> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.complaint_item, parent, false);
        return new ComplaintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {
        String customerName = list.get(position).getCustomerName();
        String phoneNumber = list.get(position).getMobileNumber();
        final String complaintBody = list.get(position).getComplaintText();
        holder.complaintBody.setText(complaintBody);
        holder.phoneNumber.setText(phoneNumber);
        holder.customerName.setText(customerName);
        final Map<String, Object> seen = new HashMap<>();
        seen.put("seen", true);
        if (!list.get(position).isSeen()) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Complaints");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        dataSnapshot.getRef().updateChildren(seen);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Complaints");
                Query query = reference.orderByChild("complaintText").equalTo(complaintBody);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            dataSnapshot.getRef().removeValue();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ComplaintViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, phoneNumber, complaintBody;
        ImageButton delete;

        public ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.customer_name);
            phoneNumber = itemView.findViewById(R.id.phone_number);
            complaintBody = itemView.findViewById(R.id.complaints);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
