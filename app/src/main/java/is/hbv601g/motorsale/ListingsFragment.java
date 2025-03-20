package is.hbv601g.motorsale;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.List;
import java.util.function.Consumer;

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
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Inflates the fragment's view.
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
        binding = FragmentListingsBinding.inflate(inflater, container, false);
        swipeRefreshLayout = binding.swipeRefreshLayout;
        return binding.getRoot();
    }

    /**
     * Initializes the fragment's view.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
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

        fetchListings(navController);
    }

    /**
     * Fetches the list of vehicle listings from the server.
     *
     * @param navController
     */
    private void fetchListings(NavController navController) {
        listingsService.findAll(listings -> {
            if (binding == null) return;

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }

            if (listings == null || listings.isEmpty()) {
                Toast.makeText(getContext(), "No listings found", Toast.LENGTH_SHORT).show();
            } else {
                if (adapter != null) {
                    adapter.updateListings(listings);
                } else {
                    adapter = new VehicleAdapter(getContext(), listings, navController, false);
                    binding.recyclerView.setAdapter(adapter);
                }
            }
        });
    }

    /**
     * Destroys the fragment's view.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
