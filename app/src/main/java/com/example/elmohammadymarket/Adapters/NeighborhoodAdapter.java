package com.example.elmohammadymarket.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elmohammadymarket.Model.Neighborhood;
import com.example.elmohammadymarket.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NeighborhoodAdapter extends RecyclerView.Adapter<NeighborhoodAdapter.NeighborhoodViewHolder> {
    Context context;
    List<Neighborhood> neighborhoods;

    public NeighborhoodAdapter(Context context, List<Neighborhood> neighborhoods) {
        this.context = context;
        this.neighborhoods = neighborhoods;
    }

    @NonNull
    @Override
    public NeighborhoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.neighborhood_item, parent, false);
        return new NeighborhoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NeighborhoodViewHolder holder, int position) {
        final String neighborhoodName = neighborhoods.get(position).getNeighborhood();
        holder.neighborhoodText.setText(neighborhoodName);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Neighborhood");
                Query query = reference.orderByChild("neighborhood").equalTo(neighborhoodName);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                            dataSnapshot.getRef().removeValue();
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
        return neighborhoods.size();
    }

    static class NeighborhoodViewHolder extends RecyclerView.ViewHolder {
        TextView neighborhoodText;
        ImageButton delete;

        public NeighborhoodViewHolder(@NonNull View itemView) {
            super(itemView);
            neighborhoodText = itemView.findViewById(R.id.neighborhood_name);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
