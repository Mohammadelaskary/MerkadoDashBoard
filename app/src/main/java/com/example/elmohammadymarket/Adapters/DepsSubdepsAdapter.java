package com.example.elmohammadymarket.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elmohammadymarket.Database.DepartmentNames;
import com.example.elmohammadymarket.Model.SubDeparment;
import com.example.elmohammadymarket.R;

import java.util.List;

public class DepsSubdepsAdapter extends RecyclerView.Adapter<DepsSubdepsAdapter.DepsSubdepsViewHolder> {
    private List<DepartmentNames> departmentNames;
    private List<SubDeparment> subDeparments;
    int selected_position = -1;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public DepsSubdepsAdapter(List<DepartmentNames> departmentNames, List<SubDeparment> subDeparments) {
        this.departmentNames = departmentNames;
        this.subDeparments = subDeparments;
    }

    @NonNull
    @Override
    public DepsSubdepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dep_subdep, parent, false);
        return new DepsSubdepsViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DepsSubdepsViewHolder holder, int position) {
        String depName = departmentNames.get(position).getDepName();
        holder.depName.setText(depName);
        if (selected_position == position) {
            holder.depName.setBackgroundResource(R.drawable.selected_depname_background);
            holder.subDeps.setVisibility(View.VISIBLE);
        } else {
            holder.depName.setBackgroundResource(R.drawable.unselected_depname);
            holder.subDeps.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return departmentNames.size();
    }

    class DepsSubdepsViewHolder extends RecyclerView.ViewHolder {
        Button depName;
        RecyclerView subDeps;

        public DepsSubdepsViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            depName = itemView.findViewById(R.id.dep_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                            selected_position = position;
                            notifyDataSetChanged();
                        }
                    }


                }
            });

        }
    }
}
