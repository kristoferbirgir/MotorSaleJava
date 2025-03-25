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

import androidx.annotation.ColorLong;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import is.hbv601g.motorsale.DTOs.ListingDTO;
import is.hbv601g.motorsale.services.ListingService;
import is.hbv601g.motorsale.services.NetworkingService;
import is.hbv601g.motorsale.viewModels.UserViewModel;

public class FilterSortFragment extends Fragment {

    private Spinner spinnerCarBrand, spinnerCarModel, spinnerCarColor, spinnerTransmission;
    private EditText editTextMinModelYear, editTextMaxModelYear;
    private EditText editTextMinMileage, editTextMaxMileage;
    private EditText editTextMinPrice, editTextMaxPrice;
    private RadioGroup radioGroupSortBy, radioGroupSortDirection;
    private Button buttonApplyFilters, buttonResetFilters;
    private UserViewModel userViewModel;

    private ListingService listingService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_filter_sort, container, false);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

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

        userViewModel.getAllListings().observe(getViewLifecycleOwner(), listings -> {
            if (listings == null || listings.isEmpty()) {
                Toast.makeText(getContext(), "No listings available to populate filters", Toast.LENGTH_SHORT).show();
                return;
            }
            populateSpinners(listings);
        });

        listingService = new ListingService(requireContext());

        buttonApplyFilters.setOnClickListener(v -> applyFilters());
        buttonResetFilters.setOnClickListener(v -> resetFilters());

        return view;

    }

    private List<ListingDTO> parseListingList(String jsonResponse) {
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ListingDTO>>(){}.getType();
            return gson.fromJson(jsonResponse, listType);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void populateSpinners(List<ListingDTO> listings) {
        // 1) Collect unique brand/model/color/transmission from the motorVehicle object
        Set<String> brandSet = new HashSet<>();
        Set<String> modelSet = new HashSet<>();
        Set<String> colorSet = new HashSet<>();
        Set<String> transSet = new HashSet<>();

        for (ListingDTO listing : listings) {
            if (listing.getMotorVehicle() != null) {
                if (listing.getMotorVehicle().getBrand() != null) {
                    brandSet.add(listing.getMotorVehicle().getBrand());
                }
                if (listing.getMotorVehicle().getModel() != null) {
                    modelSet.add(listing.getMotorVehicle().getModel());
                }
                if (listing.getMotorVehicle().getColor() != null) {
                    colorSet.add(listing.getMotorVehicle().getColor());
                }
                if (listing.getMotorVehicle().getTransmissionType() != null) {
                    transSet.add(listing.getMotorVehicle().getTransmissionType().toString());
                }
            }
        }

        // 2) Convert sets to lists and sort
        List<String> brands = new ArrayList<>(brandSet);
        List<String> models = new ArrayList<>(modelSet);
        List<String> colors = new ArrayList<>(colorSet);
        List<String> transmissions = new ArrayList<>(transSet);

        Collections.sort(brands);
        Collections.sort(models);
        Collections.sort(colors);
        Collections.sort(transmissions);

        // 3) Optionally prepend "" for “no filter”
        brands.add(0, "Ekkert valið");
        models.add(0, "Ekkert valið");
        colors.add(0, "Ekkert valið");
        transmissions.add(0, "Bæði");

        // 4) Create adapters
        ArrayAdapter<String> adapterBrands = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                brands
        );
        adapterBrands.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapterModels = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                models
        );
        adapterModels.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapterColors = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                colors
        );
        adapterColors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapterTrans = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                transmissions
        );
        adapterTrans.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 5) Assign to spinners
        spinnerCarBrand.setAdapter(adapterBrands);
        spinnerCarModel.setAdapter(adapterModels);
        spinnerCarColor.setAdapter(adapterColors);
        spinnerTransmission.setAdapter(adapterTrans);
    }


    /**
     * Collects filter and sort parameters, builds a URL‑encoded query,
     * and calls the backend to fetch filtered listings.
     */
    private void applyFilters() {
        // Retrieve filter values
        String brand = spinnerCarBrand.getSelectedItem() != null ? spinnerCarBrand.getSelectedItem().toString() : "Ekkert valið";
        String model = spinnerCarModel.getSelectedItem() != null ? spinnerCarModel.getSelectedItem().toString() : "Ekkert valið";
        String color = spinnerCarColor.getSelectedItem() != null ? spinnerCarColor.getSelectedItem().toString() : "Ekkert valið";
        String transmission = spinnerTransmission.getSelectedItem() != null ? spinnerTransmission.getSelectedItem().toString() : "Bæði";

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

        String endpoint;

        if (!brand.equalsIgnoreCase("Ekkert valið")) {
            // e.g. filterByCarBrand?brand=Toyota
            endpoint = "listings/filterByCarBrand?brand=" + urlEncode(brand);
        }
        else if (!model.equalsIgnoreCase("Ekkert valið")) {
            endpoint = "listings/filterByCarModel?model=" + urlEncode(model);
        }
        else if (!color.equalsIgnoreCase("Ekkert valið")) {
            endpoint = "listings/filterByColor?color=" + urlEncode(color);
        }
        else if (!TextUtils.isEmpty(minPrice) && !TextUtils.isEmpty(maxPrice)) {
            // e.g. filterByPrice?minPrice=300000&maxPrice=4000000
            endpoint = "listings/filterByPrice?minPrice=" + minPrice + "&maxPrice=" + maxPrice;
        }
        else if (!transmission.equalsIgnoreCase("Bæði")) {
            // e.g. filterByTransmission?transmissionType=AUTOMATIC
            endpoint = "listings/filterByTransmission?transmissionType=" + urlEncode(transmission);
        }
        else {
            // no filter => just fetch everything
            listingService.findAll(listings -> {
                if (listings == null) {
                    Toast.makeText(getContext(), "No results.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // store or handle
                userViewModel.setFilteredListings(listings);
                Navigation.findNavController(requireView()).popBackStack();
            });
            return; // end here
        }

        Log.d("FilterSortFragment", "Filter query: " + endpoint);
        // Call the backend using the filterListings method.

        listingService.filterListings(endpoint, new NetworkingService.VolleyRawCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                // parse the JSON here or let ListingService do it in callback
                // ListingService returns raw JSON, so parse manually or create a similar callback
                Type listType = new TypeToken<List<ListingDTO>>() {}.getType();
                List<ListingDTO> filtered;
                try {
                    filtered = new Gson().fromJson(jsonResponse, listType);
                } catch (Exception e) {
                    Log.e("FilterSortFragment", "JSON parse error", e);
                            filtered = null;
                }

                if (filtered == null || filtered.isEmpty()) {
                    Toast.makeText(getContext(), "No results.", Toast.LENGTH_SHORT).show();
                } else {
                    userViewModel.setFilteredListings(filtered);
                    // Guard against a destroyed fragment:
                    if (!isAdded() || getView() == null) {
                        return;
                    }
                // go back to previous screen
                Navigation.findNavController(requireView()).popBackStack();
                }
            }

            @Override
            public void onError(String error) {
                Log.e("FilterSortFragment", "API Error: " + error);
                        Toast.makeText(getContext(), "Filter failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *  Helper method to safely URL-encode
     * @param value
     * @return
     */
    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value; // fallback
        }
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
