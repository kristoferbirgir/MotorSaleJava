package is.hbv601g.motorsale;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import is.hbv601g.motorsale.DTOs.ListingDTO;
import is.hbv601g.motorsale.Enums.FuelType;
import is.hbv601g.motorsale.Enums.MotorVehicleType;
import is.hbv601g.motorsale.Enums.TransmissionType;
import is.hbv601g.motorsale.services.ListingService;
import is.hbv601g.motorsale.services.MotorVehicleService;
import is.hbv601g.motorsale.viewModels.UserViewModel;

/**
 * Fragment for editing a listing and updating vehicle details.
 */
public class EditListingFragment extends Fragment {

    private EditText editTextBrand, editTextModel, editTextColor, editTextModelYear, editTextEngineSize, editTextHorsePower, editTextMileage, editTextFuelConsumption;
    private EditText editTextPrice, editTextDescription;
    private Button buttonSaveChanges;
    private ImageView imagePreview;
    private Spinner spinnerFuelType, spinnerTransmissionType, spinnerMotorVehicleType;

    private ListingService listingService;
    private MotorVehicleService motorVehicleService;
    private UserViewModel userViewModel;
    private String listingId;
    private Long vehicleId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_listing, container, false);

        // Initialize UI elements
        initializeUI(view);

        listingService = new ListingService(requireContext());
        motorVehicleService = new MotorVehicleService(requireContext());
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // Retrieve listing ID from arguments
        Bundle args = getArguments();
        if (args != null) {
            listingId = args.getString("listingId");
            fetchListingDetails();
        }

        // Set button click listener for updating listing
        buttonSaveChanges.setOnClickListener(v -> {
            Log.d("EditListingFragment", "Save button clicked! Updating listing...");
            updateListing();
        });

        return view;
    }

    /**
     * Initializes UI components by linking XML elements and populating spinners.
     *
     * @param view The parent view of the fragment.
     */
    private void initializeUI(View view) {
        editTextBrand = view.findViewById(R.id.editText_brand);
        editTextModel = view.findViewById(R.id.editText_model);
        editTextColor = view.findViewById(R.id.editText_color);
        editTextModelYear = view.findViewById(R.id.editText_modelYear);
        editTextEngineSize = view.findViewById(R.id.editText_engineSize);
        editTextHorsePower = view.findViewById(R.id.editText_horsePower);
        editTextMileage = view.findViewById(R.id.editText_mileage);
        editTextFuelConsumption = view.findViewById(R.id.editText_fuelConsumption);
        editTextPrice = view.findViewById(R.id.editText_price);
        editTextDescription = view.findViewById(R.id.editText_description);
        spinnerFuelType = view.findViewById(R.id.spinner_fuel_type);
        spinnerTransmissionType = view.findViewById(R.id.spinner_transmission_type);
        spinnerMotorVehicleType = view.findViewById(R.id.spinner_motor_vehicle_type);
        buttonSaveChanges = view.findViewById(R.id.button_save_changes);
        imagePreview = view.findViewById(R.id.image_preview);

        // Populate Enum Spinners
        populateEnumSpinner(spinnerFuelType, FuelType.class);
        populateEnumSpinner(spinnerTransmissionType, TransmissionType.class);
        populateEnumSpinner(spinnerMotorVehicleType, MotorVehicleType.class);
    }

    /**
     * Fetches the listing details from the backend.
     */
    private void fetchListingDetails() {
        if (listingId != null) {
            listingService.findById(listingId, listing -> {
                if (listing != null) {
                    Log.d("EditListingFragment", "Listing details fetched successfully.");
                    populateFields(listing);
                    vehicleId = listing.getMotorVehicle().getVehicleId();
                } else {
                    Log.e("EditListingFragment", "Failed to load listing details.");
                    Toast.makeText(getActivity(), "Failed to load listing details", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Populates the input fields with existing data from the listing.
     *
     * @param listing The listing object containing vehicle data.
     */
    private void populateFields(ListingDTO listing) {
        editTextBrand.setText(listing.getMotorVehicle().getBrand());
        editTextModel.setText(listing.getMotorVehicle().getModel());
        editTextColor.setText(listing.getMotorVehicle().getColor());
        editTextModelYear.setText(String.valueOf(listing.getMotorVehicle().getModelYear()));
        editTextEngineSize.setText(listing.getMotorVehicle().getEngineSize());
        editTextHorsePower.setText(String.valueOf(listing.getMotorVehicle().getHorsePower()));
        editTextMileage.setText(String.valueOf(listing.getMotorVehicle().getMileage()));
        editTextFuelConsumption.setText(String.valueOf(listing.getMotorVehicle().getFuelConsumption()));
        editTextPrice.setText(String.valueOf(listing.getPrice()));
        editTextDescription.setText(listing.getDescription());
    }

    /**
     * Updates the listing by sending PATCH requests for each changed field.
     */
    private void updateListing() {
        if (vehicleId == null) {
            Log.e("EditListingFragment", "Error: Vehicle ID is missing. Cannot update.");
            return;
        }

        Log.d("EditListingFragment", "Updating listing...");

        // Debugging: Log vehicleId and listingId
        Log.d("EditListingFragment", "📌 Vehicle ID: " + vehicleId);
        Log.d("EditListingFragment", "📌 Listing ID: " + listingId);

        // Updating Motor Vehicle fields
        updateField("updateMileage", editTextMileage.getText().toString());
        updateField("updateEngineSize", editTextEngineSize.getText().toString());
        updateField("updateHorsePower", editTextHorsePower.getText().toString());
        updateField("updateFuelConsumption", editTextFuelConsumption.getText().toString());
        updateField("updateColor", editTextColor.getText().toString());
        updateField("updateModelYear", editTextModelYear.getText().toString());

        // Updating Listing fields
        updateListingField("updatePrice", editTextPrice.getText().toString());
        updateListingField("updateDescription", editTextDescription.getText().toString());

        Log.d("EditListingFragment", "✅ All update requests sent!");
    }

    /**
     * Sends a PATCH request to update a motor vehicle field with a new value,
     * only if the new value is not empty. Logs success or failure.
     *
     * @param endpoint The specific backend endpoint for the field update (e.g., "updateMileage").
     * @param newValue The new value to apply to the field.
     */

    private void updateField(String endpoint, String newValue) {
        if (!TextUtils.isEmpty(newValue)) {
            Log.d("EditListingFragment", "📡 Sending PATCH request to MotorVehicle API: " + endpoint + " with value: " + newValue);

            motorVehicleService.updateField(vehicleId, endpoint, newValue, success -> {
                if (success) {
                    Log.d("EditListingFragment", "✅ Successfully updated " + endpoint);
                } else {
                    Log.e("EditListingFragment", "❌ Update failed for " + endpoint);
                }
            });
        }
    }


    /**
     * Sends a PATCH request to update a listing field with a new value,
     * only if the new value is not empty. Displays a toast and logs failure if the update fails.
     *
     * @param endpoint The specific backend endpoint for the listing update (e.g., "updatePrice").
     * @param newValue The new value to apply to the field.
     */

    private void updateListingField(String endpoint, String newValue) {
        if (!TextUtils.isEmpty(newValue)) {
            listingService.updateField(listingId, endpoint, newValue, success -> {
                if (!success) {
                    Log.e("EditListingFragment", "❌ Update failed for " + endpoint);
                    Toast.makeText(getActivity(), "Update failed for " + endpoint, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Populates a spinner with Enum values.
     *
     * @param spinner   The spinner to populate.
     * @param enumClass The enum class containing values.
     * @param <T>       The enum type.
     */
    private <T extends Enum<T>> void populateEnumSpinner(Spinner spinner, Class<T> enumClass) {
        List<String> enumValues = new ArrayList<>();
        for (T value : enumClass.getEnumConstants()) {
            enumValues.add(value.name()); // Convert Enum values to String
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, enumValues);
        spinner.setAdapter(adapter);
    }
}
