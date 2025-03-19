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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import is.hbv601g.motorsale.DTOs.ListingDTO;
import is.hbv601g.motorsale.R;
import is.hbv601g.motorsale.UserListingsFragment;
import is.hbv601g.motorsale.services.ListingService;

/**
 * Adapter class for displaying vehicle listings in a RecyclerView.
 */
public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private final Context context;
    private final List<ListingDTO> listings;
    private final NavController navController;
    private final boolean isUserListings;

    /**
     * Constructor for the VehicleAdapter.
     *
     * @param context         The application context.
     * @param listings        List of vehicle listings.
     * @param navController   Navigation controller for fragment transitions.
     * @param isUserListings  Flag indicating if the adapter is used for user's listings.
     */
    public VehicleAdapter(Context context, List<ListingDTO> listings, NavController navController, boolean isUserListings) {
        this.context = context;
        this.listings = listings;
        this.navController = navController;
        this.isUserListings = isUserListings;
    }

    /**
     * Updates the vehicle listings in the adapter.
     *
     * @param newListings The new list of vehicle listings.
     */
    public void updateListings(List<ListingDTO> newListings) {
        listings.clear();
        listings.addAll(newListings);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_listingitem, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        ListingDTO listing = listings.get(position);

        // Set text values
        holder.vehicleName.setText(listing.getMotorVehicle().getBrand() + " " + listing.getMotorVehicle().getModel());
        holder.vehicleYear.setText(String.valueOf(listing.getMotorVehicle().getModelYear()));
        holder.vehiclePrice.setText("$" + listing.getPrice());
        holder.vehicleLocation.setText(listing.getCity() + ", " + listing.getPostalCode());

        // Decode Base64-encoded image and set it in ImageView
        String base64String = listing.getImageBase64();
        if (base64String != null && !base64String.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.vehicleImage.setImageBitmap(decodedByte);
            } catch (IllegalArgumentException e) {
                Log.e("VehicleAdapter", "Invalid Base64 format: " + e.getMessage());
                holder.vehicleImage.setImageResource(R.drawable.placeholder_car);
            }
        } else {
            holder.vehicleImage.setImageResource(R.drawable.placeholder_car);
        }

        // Pass listing ID in bundle for navigation
        Bundle bundle = new Bundle();
        if (listing.getListingId() != null) {
            bundle.putString("listingId", String.valueOf(listing.getListingId()));
        } else {
            Log.e("VehicleAdapter", "Listing ID is null!");
        }

        // Navigate to Single Listing Fragment when clicked
        holder.viewListingButton.setOnClickListener(v -> {
            navController.navigate(R.id.singleListingFragment, bundle);
        });

        // Show edit button only in "My Listings"
        if (isUserListings) {
            holder.editListingButton.setVisibility(View.VISIBLE);
            holder.editListingButton.setOnClickListener(v -> {
                if (listing.getListingId() != null) {
                    Bundle editBundle = new Bundle();
                    editBundle.putString("listingId", String.valueOf(listing.getListingId()));

                    // Navigate to Edit Listing Fragment
                    navController.navigate(R.id.editListingFragment, editBundle);
                } else {
                    Log.e("VehicleAdapter", "Listing ID is null, cannot navigate to edit page!");
                }
            });
            holder.deleteListingButton.setVisibility(View.VISIBLE);
            ListingService listingService = new ListingService(context);
            holder.deleteListingButton.setOnClickListener(v -> deleteListing(listing.getListingId(), listingService));



        } else {
            holder.editListingButton.setVisibility(View.GONE);
        }
    }
    public void deleteListing(Long listingId, ListingService listingService) {
        listingService.deleteListing(listingId.toString(), success -> {
            if (success) {
                Toast.makeText(context, "Listing deleted successfully!", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.userListingsFragment);

            } else {
                Toast.makeText(context, "Failed to delete listing", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    /**
     * ViewHolder class for holding listing item views.
     */
    public static class VehicleViewHolder extends RecyclerView.ViewHolder {
        TextView vehicleName, vehicleYear, vehiclePrice, vehicleLocation;
        ImageView vehicleImage;
        Button viewListingButton, editListingButton;
        ImageButton deleteListingButton;

        /**
         * Constructor for VehicleViewHolder.
         *
         * @param itemView The item view.
         */
        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            vehicleName = itemView.findViewById(R.id.tvVehicleName);
            vehicleYear = itemView.findViewById(R.id.tvVehicleYear);
            vehiclePrice = itemView.findViewById(R.id.tvVehiclePrice);
            vehicleLocation = itemView.findViewById(R.id.tvVehicleLocation);
            vehicleImage = itemView.findViewById(R.id.ivVehicleImage);
            viewListingButton = itemView.findViewById(R.id.viewListingButton);
            editListingButton = itemView.findViewById(R.id.editListingButton);
            deleteListingButton = itemView.findViewById(R.id.deleteListingButton);
        }
    }
}
