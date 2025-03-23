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
import is.hbv601g.motorsale.adapters.FavoritesAdapter;
import is.hbv601g.motorsale.data.FavoritesDbHelper;
import is.hbv601g.motorsale.viewModels.UserViewModel;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoritesAdapter adapter;
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
        if (favorites.isEmpty()){
            Toast.makeText(getActivity(), "No listings have been saved to favorites", Toast.LENGTH_SHORT).show();
        }
        adapter = new FavoritesAdapter(favorites, navController, requireContext(), dbHelper);
        recyclerView.setAdapter(adapter);
    }
}
