package is.hbv601g.motorsale;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import is.hbv601g.motorsale.DTOs.UserDTO;
import is.hbv601g.motorsale.adapters.VehicleAdapter;
import is.hbv601g.motorsale.databinding.FragmentUserListingsBinding;
import is.hbv601g.motorsale.services.UserService;
import is.hbv601g.motorsale.viewModels.UserViewModel;

/**
 * Fragment for displaying the listings created by the current user.
 */
public class UserListingsFragment extends Fragment {

    private FragmentUserListingsBinding binding;
    private UserService userService;
    private VehicleAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserListingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userService = new UserService(requireContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
        NavController navController = Navigation.findNavController(view);

        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(() -> fetchUserListings(navController));

        fetchUserListings(navController);
    }

    /**
     * Fetches listings created by the currently logged-in user.
     * Updates the RecyclerView with the listings or displays a toast if none are found.
     * Also handles swipe-to-refresh state.
     *
     * @param navController The NavController for navigation purposes.
     */

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
                    // isUserListings = true, isFavoritesView = false
                    adapter = new VehicleAdapter(
                            requireContext(),
                            userListings,
                            navController,
                            true,
                            false,
                            userViewModel
                    );
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
