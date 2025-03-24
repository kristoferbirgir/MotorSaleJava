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

    /**
     * Inflates the view for this fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSingleListingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Called after onCreateView.
     *
     * @param view The View returned by onCreateView.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
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

    /**
     * Fetches a listing by its ID from the API.
     *
     * @param listingId
     */
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

    /**
     * Populates the UI elements with the listing details.
     *
     * @param listing
     */
    private void populateListingDetails(ListingDTO listing) {
        // Set vehicle name and model
        binding.tvVehicleName.setText(listing.getMotorVehicle().getBrand() + " " + listing.getMotorVehicle().getModel());
        binding.tvVehicleYear.setText("Árgerð: " + listing.getMotorVehicle().getModelYear());
        binding.tvVehicleFuelType.setText("Orkugjafi: " + listing.getMotorVehicle().getFuelType());
        binding.tvVehicleEngineSize.setText("Vélarstærð: " + listing.getMotorVehicle().getEngineSize() + " L");
        binding.tvVehicleHorsePower.setText("Hestöfl: " + listing.getMotorVehicle().getHorsePower() + " HP");
        binding.tvVehicleMileage.setText("Akstur: " + listing.getMotorVehicle().getMileage() + " km");
        binding.tvVehicleTransmission.setText("Skipting: " + listing.getMotorVehicle().getTransmissionType());
        binding.tvVehiclePrice.setText("Verð: " + listing.getPrice() + " kr.");
        binding.tvVehicleLocation.setText("Staðsetning: " + listing.getCity() + ", " + listing.getPostalCode());
        binding.tvVehicleDescription.setText("Lýsing: " + listing.getDescription());

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