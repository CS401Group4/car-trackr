package com.example.cartrackr.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cartrackr.CarDetailPage;
import com.example.cartrackr.R;
import com.example.cartrackr.model.Vehicle;

import java.util.ArrayList;

/**
 * Main Adapter class used for RecyclerView in AddCarPage class
 * @since April 2021
 * @author Group 4
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    /**
     * Array list of vehicles
     */
    ArrayList<Vehicle> mVehicles;

    /**
     * Custom constructor
     * @param vehicles ArrayList type containing list of vehicles of single user
     */
    public MainAdapter(ArrayList<Vehicle> vehicles) {
        mVehicles = vehicles;
    }

    /**
     * Override method of onCreateViewHolder
     * @param parent Instance of ViewGroup
     * @param viewType integer variable of object position
     * @return ViewHolder instance
     */
    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Override method of onBindViewHolder
     * @param holder Instance of ViewHolder
     * @param position integer variable of object position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Vehicle vehicle = mVehicles.get(position);
        Log.i("HELLO", vehicle.toString());
        holder.mFullCarModel.setText(vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel());
    }

    /**
     * Override method of getItemCount
     * @return size of the vehicles ArrayList
     */
    @Override
    public int getItemCount() {
        return mVehicles.size();
    }

    /**
     * ViewHolder class for the car list row
     */
    public class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {
        /**
         * TextView UI component
         */
        public TextView mFullCarModel;

        /**
         * Custom constructor
         * @param itemView Instance of View class
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mFullCarModel = itemView.findViewById(R.id.car_name);
        }

        /**
         * Click listener for a single row of RecyclerView
         * @param v Instance of View class
         */
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(itemView.getContext(), CarDetailPage.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("DATA", mVehicles.get(getAdapterPosition()));
            intent.putExtras(bundle);
            itemView.getContext().startActivity(intent);
        }
    }
}
