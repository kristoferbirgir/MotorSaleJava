package is.hbv601g.motorsale.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import is.hbv601g.motorsale.DTOs.ListingDTO;
import is.hbv601g.motorsale.R;
import is.hbv601g.motorsale.data.FavoritesDbHelper;
import is.hbv601g.motorsale.services.ListingService;
import is.hbv601g.motorsale.viewModels.UserViewModel;

/**
 * Adapter class for displaying vehicle listings in a RecyclerView.
 */
public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private final Context context;
    private final List<ListingDTO> listings;
    private final NavController navController;
    private final boolean isUserListings;
    private final boolean isFavoritesView;
    private final UserViewModel userViewModel;

    public VehicleAdapter(Context context, List<ListingDTO> listings, NavController navController,
                          boolean isUserListings, boolean isFavoritesView, UserViewModel userViewModel) {
        this.context = context;
        this.listings = listings;
        this.navController = navController;
        this.isUserListings = isUserListings;
        this.isFavoritesView = isFavoritesView;
        this.userViewModel = userViewModel;
    }

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

        holder.vehicleName.setText(listing.getMotorVehicle().getBrand() + " " + listing.getMotorVehicle().getModel());
        holder.vehicleYear.setText(String.valueOf(listing.getMotorVehicle().getModelYear()));
        holder.vehiclePrice.setText("Verð: " + String.format("%.0f", listing.getPrice()) + " kr.");
        holder.vehicleLocation.setText("Staðsetning: " + listing.getCity() + ", " + listing.getPostalCode());

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

        Bundle bundle = new Bundle();
        if (listing.getListingId() != null) {
            bundle.putString("listingId", String.valueOf(listing.getListingId()));
        }

        holder.cardView.setOnClickListener(v -> navController.navigate(R.id.singleListingFragment, bundle));

        if (isUserListings) {
            holder.editListingButton.setVisibility(View.VISIBLE);
            holder.editListingButton.setOnClickListener(v -> {
                if (listing.getListingId() != null) {
                    Bundle editBundle = new Bundle();
                    editBundle.putString("listingId", String.valueOf(listing.getListingId()));
                    navController.navigate(R.id.editListingFragment, editBundle);
                }
            });

            holder.deleteListingButton.setVisibility(View.VISIBLE);
            ListingService listingService = new ListingService(context);
            holder.deleteListingButton.setOnClickListener(v -> deleteListing(listing.getListingId(), listingService));
        } else {
            holder.editListingButton.setVisibility(View.GONE);
        }

        // Favorite handling
        if (userViewModel.getUser().getValue() != null) {
            String userId = String.valueOf(userViewModel.getUser().getValue().getUserId());
            FavoritesDbHelper dbHelper = new FavoritesDbHelper(context);
            boolean[] isFavorite = {dbHelper.isFavorite(listing.getListingId(), userId)};

            updateHeartIcon(holder.favoriteIcon, isFavorite[0]);

            holder.favoriteIcon.setVisibility(View.VISIBLE);
            holder.favoriteIcon.setOnClickListener(v -> {
                isFavorite[0] = !isFavorite[0];
                if (isFavorite[0]) {
                    dbHelper.addFavorite(userId, listing);
                    Toast.makeText(context, "Vistað í uppáhaldi", Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.removeFavorite(listing.getListingId(), userId);
                    Toast.makeText(context, "Fjarlægt úr uppáhaldi", Toast.LENGTH_SHORT).show();

                    // Remove from list only if in favorites fragment
                    if (isFavoritesView) {
                        int currentPosition = holder.getAdapterPosition();
                        if (currentPosition != RecyclerView.NO_POSITION) {
                            listings.remove(currentPosition);
                            notifyItemRemoved(currentPosition);
                            return;
                        }
                    }
                }

                updateHeartIcon(holder.favoriteIcon, isFavorite[0]);
            });

        } else {
            holder.favoriteIcon.setVisibility(View.GONE);
        }
    }


    private void updateHeartIcon(ImageButton button, boolean isFavorite) {
        int iconRes = isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border;
        int color = isFavorite ? R.color.button_dark_red : android.R.color.darker_gray;
        button.setImageResource(iconRes);
        button.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN);
    }

    public void deleteListing(Long listingId, ListingService listingService) {
        listingService.deleteListing(listingId.toString(), success -> {
            if (success) {
                Toast.makeText(context, "Skráningu eytt með góðum árangri!", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.userListingsFragment);
            } else {
                Toast.makeText(context, "Ekki tókst að eyða skráningu", Toast.LENGTH_SHORT).show();
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
        ImageButton editListingButton, deleteListingButton, favoriteIcon;
        CardView cardView;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            vehicleName = itemView.findViewById(R.id.tvVehicleName);
            vehicleYear = itemView.findViewById(R.id.tvVehicleYear);
            vehiclePrice = itemView.findViewById(R.id.tvVehiclePrice);
            vehicleLocation = itemView.findViewById(R.id.tvVehicleLocation);
            vehicleImage = itemView.findViewById(R.id.ivVehicleImage);
            editListingButton = itemView.findViewById(R.id.editListingButton);
            deleteListingButton = itemView.findViewById(R.id.deleteListingButton);
            favoriteIcon = itemView.findViewById(R.id.favoriteIcon);
        }
    }
}
