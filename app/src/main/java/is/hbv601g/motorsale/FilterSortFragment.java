package is.hbv601g.motorsale;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import is.hbv601g.motorsale.DTOs.ListingDTO;
import is.hbv601g.motorsale.services.ListingService;
import is.hbv601g.motorsale.services.NetworkingService;

public class FilterSortFragment extends Fragment {

    private Spinner spinnerCarBrand, spinnerCarModel, spinnerCarColor, spinnerTransmission;
    private EditText editTextMinModelYear, editTextMaxModelYear;
    private EditText editTextMinMileage, editTextMaxMileage;
    private EditText editTextMinPrice, editTextMaxPrice;
    private RadioGroup radioGroupSortBy, radioGroupSortDirection;
    private Button buttonApplyFilters, buttonResetFilters;

    private ListingService listingService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_sort, container, false);

        // Bind UI elements
        spinnerCarBrand = view.findViewById(R.id.spinner_car_brand);
        spinnerCarModel = view.findViewById(R.id.spinner_car_model);
        spinnerCarColor = view.findViewById(R.id.spinner_car_color);
        spinnerTransmission = view.findViewById(R.id.spinner_transmission);
        editTextMinModelYear = view.findViewById(R.id.editText_min_model_year);
        editTextMaxModelYear = view.findViewById(R.id.editText_max_model_year);
        editTextMinMileage = view.findViewById(R.id.editText_min_mileage);
        editTextMaxMileage = view.findViewById(R.id.editText_max_mileage);
        editTextMinPrice = view.findViewById(R.id.editText_min_price);
        editTextMaxPrice = view.findViewById(R.id.editText_max_price);
        radioGroupSortBy = view.findViewById(R.id.radioGroup_sort_by);
        radioGroupSortDirection = view.findViewById(R.id.radioGroup_sort_direction);
        buttonApplyFilters = view.findViewById(R.id.button_apply_filters);
        buttonResetFilters = view.findViewById(R.id.button_reset_filters);

        listingService = new ListingService(requireContext());

        setupSpinners();

        buttonApplyFilters.setOnClickListener(v -> applyFilters());
        buttonResetFilters.setOnClickListener(v -> resetFilters());

        return view;
    }

    /**
     * Populates spinners with sample data.
     * Replace these static lists with dynamic API calls if available.
     */
    private void setupSpinners() {
        // Car Brands
        List<String> carBrands = new ArrayList<>();
        carBrands.add(""); // Empty means "no filter"
        carBrands.add("Toyota");
        carBrands.add("Honda");
        carBrands.add("BMW");
        ArrayAdapter<String> adapterBrands = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, carBrands);
        adapterBrands.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCarBrand.setAdapter(adapterBrands);

        // Car Models
        List<String> carModels = new ArrayList<>();
        carModels.add("");
        carModels.add("Corolla");
        carModels.add("Civic");
        carModels.add("3 Series");
        ArrayAdapter<String> adapterModels = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, carModels);
        adapterModels.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCarModel.setAdapter(adapterModels);

        // Car Colors (Icelandic examples)
        List<String> carColors = new ArrayList<>();
        carColors.add("");
        carColors.add("Rautt");
        carColors.add("Blátt");
        carColors.add("Svart");
        ArrayAdapter<String> adapterColors = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, carColors);
        adapterColors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCarColor.setAdapter(adapterColors);

        // Transmission types
        List<String> transmissions = new ArrayList<>();
        transmissions.add("");
        transmissions.add("AUTOMATIC");
        transmissions.add("MANUAL");
        ArrayAdapter<String> adapterTrans = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, transmissions);
        adapterTrans.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTransmission.setAdapter(adapterTrans);
    }

    /**
     * Collects filter and sort parameters, builds a URL‑encoded query,
     * and calls the backend to fetch filtered listings.
     */
    private void applyFilters() {
        // Retrieve filter values
        String brand = spinnerCarBrand.getSelectedItem().toString();
        String model = spinnerCarModel.getSelectedItem().toString();
        String color = spinnerCarColor.getSelectedItem().toString();
        String transmission = spinnerTransmission.getSelectedItem().toString();

        String minYear = editTextMinModelYear.getText().toString();
        String maxYear = editTextMaxModelYear.getText().toString();
        String minMileage = editTextMinMileage.getText().toString();
        String maxMileage = editTextMaxMileage.getText().toString();
        String minPrice = editTextMinPrice.getText().toString();
        String maxPrice = editTextMaxPrice.getText().toString();

        // Determine sort options using radio groups
        String sortField = "";
        int selectedSortFieldId = radioGroupSortBy.getCheckedRadioButtonId();
        if (selectedSortFieldId != -1) {
            RadioButton rb = requireView().findViewById(selectedSortFieldId);
            String text = rb.getText().toString();
            if (text.equalsIgnoreCase("Keyrð")) {
                sortField = "mileage";
            } else if (text.equalsIgnoreCase("Árgerð")) {
                sortField = "modelYear";
            } else if (text.equalsIgnoreCase("Verð")) {
                sortField = "price";
            }
        }

        String sortDirection = "";
        int selectedSortDirId = radioGroupSortDirection.getCheckedRadioButtonId();
        if (selectedSortDirId != -1) {
            RadioButton rb = requireView().findViewById(selectedSortDirId);
            String text = rb.getText().toString();
            if (text.equalsIgnoreCase("Vaxandi")) {
                sortDirection = "asc";
            } else if (text.equalsIgnoreCase("Minnkandi")) {
                sortDirection = "desc";
            }
        }

        // Build query string with URL‑encoding for special characters
        StringBuilder queryBuilder = new StringBuilder("listings/filter?");
        try {
            if (!TextUtils.isEmpty(brand))
                queryBuilder.append("brand=").append(URLEncoder.encode(brand, "UTF-8")).append("&");
            if (!TextUtils.isEmpty(model))
                queryBuilder.append("model=").append(URLEncoder.encode(model, "UTF-8")).append("&");
            if (!TextUtils.isEmpty(color))
                queryBuilder.append("color=").append(URLEncoder.encode(color, "UTF-8")).append("&");
            if (!TextUtils.isEmpty(minYear))
                queryBuilder.append("minYear=").append(URLEncoder.encode(minYear, "UTF-8")).append("&");
            if (!TextUtils.isEmpty(maxYear))
                queryBuilder.append("maxYear=").append(URLEncoder.encode(maxYear, "UTF-8")).append("&");
            if (!TextUtils.isEmpty(minMileage))
                queryBuilder.append("minMileage=").append(URLEncoder.encode(minMileage, "UTF-8")).append("&");
            if (!TextUtils.isEmpty(maxMileage))
                queryBuilder.append("maxMileage=").append(URLEncoder.encode(maxMileage, "UTF-8")).append("&");
            if (!TextUtils.isEmpty(minPrice))
                queryBuilder.append("minPrice=").append(URLEncoder.encode(minPrice, "UTF-8")).append("&");
            if (!TextUtils.isEmpty(maxPrice))
                queryBuilder.append("maxPrice=").append(URLEncoder.encode(maxPrice, "UTF-8")).append("&");
            if (!TextUtils.isEmpty(transmission))
                queryBuilder.append("transmission=").append(URLEncoder.encode(transmission, "UTF-8")).append("&");
            if (!TextUtils.isEmpty(sortField))
                queryBuilder.append("sortField=").append(URLEncoder.encode(sortField, "UTF-8")).append("&");
            if (!TextUtils.isEmpty(sortDirection))
                queryBuilder.append("sortDirection=").append(URLEncoder.encode(sortDirection, "UTF-8")).append("&");
        } catch (Exception e) {
            Log.e("FilterSortFragment", "Encoding error", e);
        }
        // Remove trailing '&' or '?' if present
        String query = queryBuilder.toString();
        if (query.endsWith("&") || query.endsWith("?")) {
            query = query.substring(0, query.length() - 1);
        }

        Log.d("FilterSortFragment", "Filter query: " + query);

        // Call the backend using the filterListings method.
        listingService.filterListings(query, new NetworkingService.VolleyRawCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                try {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<ListingDTO>>(){}.getType();
                    List<ListingDTO> filteredListings = gson.fromJson(jsonResponse, listType);
                    Log.d("FilterSortFragment", "Filtered Listings Count: " + filteredListings.size());

                    // TODO: Pass the filtered listings back to the HomeFragment,
                    // for example using a shared ViewModel.
                    // Then navigate back:
                    Navigation.findNavController(requireView()).popBackStack();
                } catch (Exception e) {
                    Log.e("FilterSortFragment", "JSON parsing error", e);
                    Toast.makeText(getContext(), "Villa við að vinna úr niðurstöðum", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Log.e("FilterSortFragment", "API Error: " + error);
                Toast.makeText(getContext(), "Villa við að sækja niðurstöður", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Resets all filter inputs and selections.
     */
    private void resetFilters() {
        spinnerCarBrand.setSelection(0);
        spinnerCarModel.setSelection(0);
        spinnerCarColor.setSelection(0);
        spinnerTransmission.setSelection(0);
        editTextMinModelYear.setText("");
        editTextMaxModelYear.setText("");
        editTextMinMileage.setText("");
        editTextMaxMileage.setText("");
        editTextMinPrice.setText("");
        editTextMaxPrice.setText("");
        radioGroupSortBy.clearCheck();
        radioGroupSortDirection.clearCheck();
    }
}
