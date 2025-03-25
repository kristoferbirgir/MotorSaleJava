package is.hbv601g.motorsale;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import is.hbv601g.motorsale.adapters.VehicleAdapter;
import is.hbv601g.motorsale.databinding.FragmentListingsBinding;
import is.hbv601g.motorsale.services.ListingService;
import is.hbv601g.motorsale.viewModels.UserViewModel;

public class ListingsFragment extends Fragment {

    private FragmentListingsBinding binding;
    private ListingService listingsService;
    private VehicleAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserViewModel userViewModel;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable options menu in this fragment so the filter icon shows up.
        setHasOptionsMenu(true);
    }

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
        NavController navController = Navigation.findNavController(view);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        adapter = new VehicleAdapter(
                getContext(),
                new ArrayList<>(),
                Navigation.findNavController(requireView()),
                false, // isUserListings
                false, // isFavoritesView
                userViewModel
        );
        binding.recyclerView.setAdapter(adapter);

        userViewModel.getFilteredListings().observe(getViewLifecycleOwner(), filteredListings -> {
            if (filteredListings != null && !filteredListings.isEmpty()) {
                adapter.updateListings(filteredListings);
                adapter.notifyDataSetChanged();
            }
        });

        if (userViewModel.getFilteredListings().getValue() == null || userViewModel.getFilteredListings().getValue().isEmpty()) {
            swipeRefreshLayout.setOnRefreshListener(() -> fetchListings(navController));
            fetchListings(navController);
        }
    }

    private void fetchListings(NavController navController) {
        listingsService.findAll(listings -> {
            if (binding == null) return;

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }

            if (listings == null || listings.isEmpty()) {
                Toast.makeText(getContext(), "No listings found", Toast.LENGTH_SHORT).show();
            } else {
                userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
                userViewModel.setAllListings(listings);
                if (adapter != null) {
                    adapter.updateListings(listings);
                } else {
                    adapter = new VehicleAdapter(
                            getContext(),
                            listings,
                            Navigation.findNavController(requireView()),
                            false, // isUserListings
                            false, // isFavoritesView
                            userViewModel
                    );
                    binding.recyclerView.setAdapter(adapter);
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Inflate the fragment-specific options menu.
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_listings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Handle clicks on menu items.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            // Navigate to the FilterSortFragment when the filter icon is tapped.
            Navigation.findNavController(requireView()).navigate(R.id.action_listingsFragment_to_filterSortFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
