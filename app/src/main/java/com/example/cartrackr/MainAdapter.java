package com.example.cartrackr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    ArrayList<Vehicle> mVehicles;

    public MainAdapter(ArrayList<Vehicle> vehicles) {
        mVehicles = vehicles;
    }

    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mFullCarModel.setText(mVehicles.get(position).getYear() + " " + mVehicles.get(position).getMake() + " " + mVehicles.get(position).getModel());

    }

    @Override
    public int getItemCount() {
        return mVehicles.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {
        public TextView mFullCarModel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mFullCarModel = itemView.findViewById(R.id.car_name);
        }
    }
}
