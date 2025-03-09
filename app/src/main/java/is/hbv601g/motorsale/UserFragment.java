package is.hbv601g.motorsale;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import is.hbv601g.motorsale.adapters.VehicleAdapter;
import is.hbv601g.motorsale.databinding.FragmentListingsBinding;
import is.hbv601g.motorsale.services.ListingService;

public class UserFragment extends Fragment {
    private Button createListingButton;
    private Button myListingsButton;
    private ListingService listingsService;

    public UserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        listingsService = new ListingService(requireContext());

        createListingButton = view.findViewById(R.id.button3);
        myListingsButton = view.findViewById(R.id.myListingsButton);
        createListingButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_userFragment_to_createListingFragment);
        });
        myListingsButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_userFragment_to_userListingsFragment);
        });

        return view;
    }
}