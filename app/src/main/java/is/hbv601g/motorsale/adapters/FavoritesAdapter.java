package is.hbv601g.motorsale.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import is.hbv601g.motorsale.DTOs.ListingDTO;
import is.hbv601g.motorsale.data.FavoritesDbHelper;
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
import android.widget.Toast;
import is.hbv601g.motorsale.R;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {
    private final List<ListingDTO> favorites;
    private final Context context;
    private final FavoritesDbHelper dbHelper;
    private final NavController navController;


    public FavoritesAdapter(List<ListingDTO> favorites, NavController navController, Context context, FavoritesDbHelper dbHelper) {
        this.favorites = favorites;
        this.context = context;
        this.dbHelper = dbHelper;
        this.navController = navController;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_listingitem, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        ListingDTO listing = favorites.get(position);

        holder.vehicleName.setText(listing.getMotorVehicle().getBrand() + " " + listing.getMotorVehicle().getModel());
        holder.vehicleYear.setText(String.valueOf(listing.getMotorVehicle().getModelYear()));
        holder.vehiclePrice.setText("$" + listing.getPrice());
        holder.vehicleLocation.setText(listing.getCity() + ", " + listing.getPostalCode());


        String base64 = listing.getImageBase64();
        if (base64 != null) {
            byte[] imageBytes = Base64.decode(base64, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.vehicleImage.setImageBitmap(bmp);
        }

        holder.favoriteButton.setVisibility(View.VISIBLE);
        // This below is Aser's version of a heart break
        holder.favoriteButton.setText("Remove from favorites \uD83D\uDC94");
        holder.favoriteButton.setOnClickListener(v -> {
            dbHelper.removeFavorite(String.valueOf(listing.getListingId()));
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                favorites.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
            }

            Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
        });

        Bundle bundle = new Bundle();
        if (listing.getListingId() != null) {
            bundle.putString("listingId", String.valueOf(listing.getListingId()));
        } else {
            Log.e("VehicleAdapter", "Listing ID is null!");
        }

        holder.viewListingButton.setOnClickListener(v -> {
            navController.navigate(R.id.singleListingFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView vehicleName, vehicleYear, vehiclePrice, vehicleLocation;
        ImageView vehicleImage;
        Button viewListingButton, editListingButton, favoriteButton;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            vehicleName = itemView.findViewById(R.id.tvVehicleName);
            vehicleYear = itemView.findViewById(R.id.tvVehicleYear);
            vehiclePrice = itemView.findViewById(R.id.tvVehiclePrice);
            vehicleLocation = itemView.findViewById(R.id.tvVehicleLocation);
            vehicleImage = itemView.findViewById(R.id.ivVehicleImage);
            viewListingButton = itemView.findViewById(R.id.viewListingButton);
            editListingButton = itemView.findViewById(R.id.editListingButton);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
        }
    }
}
