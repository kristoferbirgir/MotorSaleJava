package is.hbv601g.motorsale.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import is.hbv601g.motorsale.DTOs.ListingDTO;
import is.hbv601g.motorsale.R;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private final Context context;
    private final List<ListingDTO> listings;
    private NavController navController;

    private final boolean isUserListings;

    public VehicleAdapter(Context context, List<ListingDTO> listings, NavController navController, boolean isUserListings) {
        this.context = context;
        this.listings = listings;
        this.navController = navController;
        this.isUserListings = isUserListings;
    }

    public void updateListings(List<ListingDTO> newListings) {
        listings.clear();
        listings.addAll(newListings);
        notifyDataSetChanged(); // Refresh RecyclerView
    }
    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_listingitem, parent, false);
        return new VehicleViewHolder(view);
    }

  /*  @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        ListingDTO listing = listings.get(position);

        // Set text values
        holder.vehicleName.setText(listing.getMotorVehicle().getBrand() + " " + listing.getMotorVehicle().getModel());
        holder.vehicleYear.setText(String.valueOf(listing.getMotorVehicle().getModelYear()));
        holder.vehiclePrice.setText("$" + listing.getPrice());
        holder.vehicleLocation.setText(listing.getCity() + ", " + listing.getPostalCode());

        // 🚀 Fix: Handle null imageBase64 safely
        String base64String = listing.getImageBase64();
        if (base64String != null && !base64String.isEmpty()) {
            try {
                byte[] imageBytes = Base64.decode(base64String, Base64.DEFAULT);
                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                holder.vehicleImage.setImageBitmap(decodedImage);
            } catch (IllegalArgumentException e) {
                // 🚨 Handle invalid Base64 format

            }
        } else {
            // 🚀 If there's no image, use a placeholder

        }
    }*/


    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        ListingDTO listing = listings.get(position);

        // Set text values
        holder.vehicleName.setText(listing.getMotorVehicle().getBrand() + " " + listing.getMotorVehicle().getModel());
        holder.vehicleYear.setText(String.valueOf(listing.getMotorVehicle().getModelYear()));
        holder.vehiclePrice.setText("$" + listing.getPrice());
        holder.vehicleLocation.setText(listing.getCity() + ", " + listing.getPostalCode());

        // Get Base64-encoded image string
        String base64String = listing.getImageBase64();
        // Show edit button only in My Listings
        if (isUserListings) {
            holder.editListingButton.setVisibility(View.VISIBLE);

            // Kristofer this sets up your stuff, I added everything that is needed up to the point where u need to start setting up
            // the editing fragments and what not, u will just have to start switch to it from below as I am currently just taking
            // navigating to the login when edit button is clicked, so u have to create a new fragment and send the request to our
            // backend
            holder.editListingButton.setOnClickListener(v -> {
                navController.navigate(R.id.loginFragment);
            });
        } else {
            holder.editListingButton.setVisibility(View.GONE);
        }
        if (base64String != null && !base64String.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                Log.d("VehicleAdapter", "fakaurmama");
                if (decodedByte != null) {
                    holder.vehicleImage.setImageBitmap(decodedByte);
                } else {
               //  holder.vehicleImage.setImageResource(R.drawable.placeholder_car); // Use placeholder if decoding fails
                }
            } catch (IllegalArgumentException e) {
            Log.e("VehicleAdapter", "Invalid Base64 format: " + e.getMessage());
            holder.vehicleImage.setImageResource(R.drawable.placeholder_car);
            }
        } else {
            holder.vehicleImage.setImageResource(R.drawable.placeholder_car); // Use placeholder if no image
        }


        Bundle bundle = new Bundle();
        if (listing.getListingId() != null) {
            bundle.putString("listingId", String.valueOf(listing.getListingId()));
        } else {
            Log.e("VehicleAdapter", "Listing ID is null!");
        }
        // Viktor, this setus up a button on each item in the main listings that when clicked will navigated
        // you to the login page, this naeeds to be changed to navigate you to a newly created fragment should check the diagrams
        // but the fragment will be called fragment_single_listing i think, then you will navigate to it and make it display all
        // the info about this listing, you can access the listing id but calling listing.getlistingId() and then make new method in
        // the service in order to call our API to fetch the info and then u populate the fragment with all the info I guess.
        holder.viewListingButton.setOnClickListener(v -> {
            this.navController.navigate(R.id.singleListingFragment, bundle);

        });
    }


    @Override
    public int getItemCount() {
        return listings.size();
    }

        public static class VehicleViewHolder extends RecyclerView.ViewHolder {
            TextView vehicleName, vehicleYear, vehiclePrice, vehicleLocation;
            ImageView vehicleImage;
            Button viewListingButton, editListingButton;

            public VehicleViewHolder(@NonNull View itemView) {
                super(itemView);
                vehicleName = itemView.findViewById(R.id.tvVehicleName);
                vehicleYear = itemView.findViewById(R.id.tvVehicleYear);
                vehiclePrice = itemView.findViewById(R.id.tvVehiclePrice);
                vehicleLocation = itemView.findViewById(R.id.tvVehicleLocation);
                vehicleImage = itemView.findViewById(R.id.ivVehicleImage);
                viewListingButton = itemView.findViewById(R.id.viewListingButton);
                editListingButton = itemView.findViewById(R.id.editListingButton);
            }
        }


}
