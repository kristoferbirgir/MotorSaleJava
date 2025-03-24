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
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import is.hbv601g.motorsale.DTOs.ListingDTO;
import is.hbv601g.motorsale.adapters.VehicleAdapter;
import is.hbv601g.motorsale.data.FavoritesDbHelper;
import is.hbv601g.motorsale.viewModels.UserViewModel;

/**
 * Fragment for displaying user's favorite listings.
 */
public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private VehicleAdapter adapter;
    private FavoritesDbHelper dbHelper;
    private UserViewModel userViewModel;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        recyclerView = view.findViewById(R.id.favoritesFragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new FavoritesDbHelper(requireContext());
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);

        loadFavorites();
        return view;
    }

    private void loadFavorites() {
        if (userViewModel.getUser().getValue() == null) return;

        String userId = String.valueOf(userViewModel.getUser().getValue().getUserId());
        List<ListingDTO> favorites = dbHelper.getFavoritesForUser(userId);

        if (favorites.isEmpty()) {
            Toast.makeText(getActivity(), "Engar skráningar í uppáhaldi", Toast.LENGTH_SHORT).show();
        }

        // `true` indicates this is the Favorites view
        adapter = new VehicleAdapter(requireContext(), favorites, navController, false, true, userViewModel);
        recyclerView.setAdapter(adapter);
    }
}
