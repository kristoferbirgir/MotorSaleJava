package is.hbv601g.motorsale;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.List;

import is.hbv601g.motorsale.adapters.VehicleAdapter;
import is.hbv601g.motorsale.databinding.FragmentListingsBinding;
import is.hbv601g.motorsale.DTOs.ListingDTO;
import is.hbv601g.motorsale.services.ListingService;

/**
 * Fragment that displays the list of vehicle listings.
 */
public class ListingsFragment extends Fragment {

    private FragmentListingsBinding binding;
    private ListingService listingsService;
    private VehicleAdapter adapter;
    private Button viewListingButton;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListingsBinding.inflate(inflater, container, false);
        swipeRefreshLayout = binding.swipeRefreshLayout;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listingsService = new ListingService(requireContext());

        // Setup RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        NavController navController = Navigation.findNavController(view);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchListings(navController);
        });

        // Fetch listingsThe
        fetchListings(navController);
    }


    private void fetchListings(NavController navController) {
        listingsService.findAll(listings -> {
            // If the fragment's view is destroyed, don't attempt to update the UI.
            if (binding == null) return;

            // Stop the refreshing animation if active.
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }

            if (listings == null || listings.isEmpty()) {
                Toast.makeText(getContext(), "No listings found", Toast.LENGTH_SHORT).show();
            } else {
                // If adapter is already initialized, update its listings.
                if (adapter != null) {
                    adapter.updateListings(listings);
                } else {
                    // Otherwise, create a new adapter and set it to the RecyclerView.
                    adapter = new VehicleAdapter(getContext(), listings, navController);
                    binding.recyclerView.setAdapter(adapter);
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
