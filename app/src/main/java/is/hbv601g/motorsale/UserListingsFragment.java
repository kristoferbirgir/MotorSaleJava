package is.hbv601g.motorsale;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;

import is.hbv601g.motorsale.DTOs.UserDTO;
import is.hbv601g.motorsale.adapters.VehicleAdapter;
import is.hbv601g.motorsale.databinding.FragmentUserListingsBinding;
import is.hbv601g.motorsale.services.ListingService;
import is.hbv601g.motorsale.services.UserService;
import is.hbv601g.motorsale.viewModels.UserViewModel;

public class UserListingsFragment extends Fragment {
    private FragmentUserListingsBinding binding;
    private UserService userService;
    private VehicleAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton deleteListingButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserListingsBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_listingitem, container, false);
        deleteListingButton = view.findViewById(R.id.deleteListingButton);



        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userService = new UserService(requireContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        NavController navController = Navigation.findNavController(view);

        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(() -> fetchUserListings(navController));

        fetchUserListings(navController);
    }

    private void fetchUserListings(NavController navController) {

        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        UserDTO user = userViewModel.getUser().getValue();

        if (user == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        Long userId = user.getUserId();
        userService.fetchUserListings(userId, userListings -> {
            if (binding == null) return;
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }

            if (userListings == null || userListings.isEmpty()) {
                Toast.makeText(getContext(), "No listings found", Toast.LENGTH_SHORT).show();
            } else {
                if (adapter != null) {
                    adapter.updateListings(userListings);
                } else {
                    adapter = new VehicleAdapter(getContext(), userListings, navController, true);
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
