package is.hbv601g.motorsale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import is.hbv601g.motorsale.DTOs.ListingDTO;
import is.hbv601g.motorsale.databinding.FragmentSingleListingBinding;
import is.hbv601g.motorsale.services.ListingService;

public class SingleListingFragment extends Fragment {

    private FragmentSingleListingBinding binding;
    private ListingDTO listing;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSingleListingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            String listingId = args.getString("listingId");
            listing = (ListingDTO) args.getSerializable("listing");

            if (listingId != null) {
                Log.d("SingleListingFragment", "Received listingId: " + listingId);
                fetchListingById(listingId);
            } else if (listing != null) {
                Log.d("SingleListingFragment", "Using passed ListingDTO data");
                populateListingDetails(listing);
            } else {
                Log.e("SingleListingFragment", "No valid listing data received!");
            }
        }
    }

    private void fetchListingById(String listingId) {
        ListingService listingService = new ListingService(requireContext());

        listingService.findById(listingId, listing -> {
            if (listing != null) {
                Log.d("SingleListingFragment", "Fetched listing: " + listing.getMotorVehicle().getBrand());
                populateListingDetails(listing);
            } else {
                Log.e("SingleListingFragment", "Error: Listing not found!");
            }
        });
    }

    private void populateListingDetails(ListingDTO listing) {
        binding.tvVehicleName.setText(listing.getMotorVehicle().getBrand() + " " + listing.getMotorVehicle().getModel());
        binding.tvVehicleYear.setText("Year: " + listing.getMotorVehicle().getModelYear());
        binding.tvVehiclePrice.setText("$" + listing.getPrice());
        binding.tvVehicleLocation.setText("Location: " + listing.getCity() + ", " + listing.getPostalCode());
        binding.tvVehicleDescription.setText("Description: " + listing.getDescription());

        // Handle Base64 Image
        String base64String = listing.getImageBase64();
        if (base64String != null && !base64String.isEmpty()) {
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            binding.ivVehicleImage.setImageBitmap(decodedByte);
        } else {
            binding.ivVehicleImage.setImageResource(R.drawable.placeholder_car);
        }
    }
}