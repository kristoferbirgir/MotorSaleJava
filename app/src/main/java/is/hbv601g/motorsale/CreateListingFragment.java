package is.hbv601g.motorsale;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import is.hbv601g.motorsale.DTOs.ListingDTO;
import is.hbv601g.motorsale.Enums.FuelType;
import is.hbv601g.motorsale.Enums.MotorVehicleType;
import is.hbv601g.motorsale.Enums.TransmissionType;
import is.hbv601g.motorsale.entities.Listing;
import is.hbv601g.motorsale.entities.MotorVehicle;
import is.hbv601g.motorsale.services.ListingService;
import is.hbv601g.motorsale.viewModels.UserViewModel;

public class CreateListingFragment extends Fragment {

    private static final int CAMERA_REQUEST_CODE = 100;
    private EditText editTextBrand, editTextModel, editTextModelYear, editTextEngineSize, editTextHorsePower, editTextMileage, editTextFuelConsumption;
    private EditText editTextPrice, editTextAddress, editTextPostalCode, editTextCity, editTextDescription;
    private Button buttonAddImage, buttonSubmitListing;
    private ImageView imagePreview;
    private ListingService listingService;
    private UserViewModel userViewModel;
    private String encodedImage = null;
    private Spinner spinnerFuelType, spinnerMotorVehicleType, spinnerTransmissionType;
    private Uri photoUri;
    private File photoFile;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_listing, container, false);

        // Initialize UI elements
        editTextBrand = view.findViewById(R.id.editText_brand);
        editTextModel = view.findViewById(R.id.editText_model);
        editTextModelYear = view.findViewById(R.id.editText_modelYear);
        editTextEngineSize = view.findViewById(R.id.editText_engineSize);
        editTextHorsePower = view.findViewById(R.id.editText_horsePower);
        editTextMileage = view.findViewById(R.id.editText_mileage);
        editTextFuelConsumption = view.findViewById(R.id.editText_fuelConsumption);
        editTextPrice = view.findViewById(R.id.editText_price);
        editTextAddress = view.findViewById(R.id.editText_address);
        editTextPostalCode = view.findViewById(R.id.editText_postalCode);
        editTextCity = view.findViewById(R.id.editText_city);
        editTextDescription = view.findViewById(R.id.editText_description);
        spinnerFuelType = view.findViewById(R.id.spinner_fuel_type);
        spinnerMotorVehicleType = view.findViewById(R.id.spinner_motor_vehicle_type);
        spinnerTransmissionType = view.findViewById(R.id.spinner_transmission_type);
        buttonAddImage = view.findViewById(R.id.button_upload_image);
        buttonSubmitListing = view.findViewById(R.id.button_submit_listing);
        imagePreview = view.findViewById(R.id.image_preview);

        listingService = new ListingService(requireContext());
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // Open gallery to select image
        buttonAddImage.setOnClickListener(v -> openImagePicker());

        // Submit listing
        buttonSubmitListing.setOnClickListener(v -> submitListing());

        return view;
    }

    // Open gallery to pick an image
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the selected image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                imagePreview.setImageBitmap(bitmap);
                imagePreview.setVisibility(View.VISIBLE);
                encodedImage = encodeImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Convert image to Base64
    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Submit listing
    private void submitListing() {
        String brand = editTextBrand.getText().toString().trim();
        String model = editTextModel.getText().toString().trim();
        String modelYearStr = editTextModelYear.getText().toString().trim();
        String engineSize = editTextEngineSize.getText().toString().trim();
        String horsePowerStr = editTextHorsePower.getText().toString().trim();
        String mileageStr = editTextMileage.getText().toString().trim();
        String fuelConsumptionStr = editTextFuelConsumption.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String postalCode = editTextPostalCode.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        MotorVehicleType motorVehicleType = (MotorVehicleType.valueOf(spinnerMotorVehicleType.getSelectedItem().toString()));
        FuelType fuelType = (FuelType.valueOf(spinnerFuelType.getSelectedItem().toString()));
        TransmissionType transmissionType = (TransmissionType.valueOf(spinnerTransmissionType.getSelectedItem().toString()));

        if (brand.isEmpty() || model.isEmpty() || modelYearStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int modelYear = Integer.parseInt(modelYearStr);
        int horsePower = Integer.parseInt(horsePowerStr);
        int mileage = Integer.parseInt(mileageStr);
        double fuelConsumption = Double.parseDouble(fuelConsumptionStr);
        double price = Double.parseDouble(priceStr);

        // Get logged-in user ID
        Long userId = (userViewModel.getUser().getValue() != null) ? userViewModel.getUser().getValue().getUserId() : null;
        if (userId == null) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        MotorVehicle motorVehicle = new MotorVehicle(motorVehicleType, brand, model, modelYear, fuelType, null, engineSize, horsePower, mileage, 0, fuelConsumption, transmissionType);

        // Create Listing DTO with MotorVehicle
        Listing newListing = new Listing(motorVehicle, price, address, postalCode, city, description, userId, null);

        // Upload the image first, then create the listing
        listingService.createListing(newListing, success -> {
            if (success) {
                Toast.makeText(getActivity(), "Listing Created!", Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.listingsFragment);
            } else {
                Toast.makeText(getActivity(), "Failed to create listing", Toast.LENGTH_SHORT).show();
            }
        });

    }
}