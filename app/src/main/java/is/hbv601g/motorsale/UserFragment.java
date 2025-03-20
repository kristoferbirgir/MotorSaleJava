package is.hbv601g.motorsale;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import is.hbv601g.motorsale.DTOs.UserDTO;
import is.hbv601g.motorsale.services.UserService;
import is.hbv601g.motorsale.viewModels.UserViewModel;

public class UserFragment extends Fragment {
    private TextView userName, userPhone, userEmail;
    private Button editUserButton, createListingButton, myListingsButton;
    private View userCard; // ✅ Add this for CardView visibility control
    private UserService userService;
    private UserViewModel userViewModel;

    public UserFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // Initialize UI elements
        userCard = view.findViewById(R.id.userCard);  // ✅ Ensure CardView is initialized
        userName = view.findViewById(R.id.userName);
        userPhone = view.findViewById(R.id.userPhone);
        userEmail = view.findViewById(R.id.userEmail);
        editUserButton = view.findViewById(R.id.editUserButton);
        createListingButton = view.findViewById(R.id.createListingButton);
        myListingsButton = view.findViewById(R.id.myListingsButton);

        // Initialize services
        userService = new UserService(requireContext());
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // Fetch user data and update UI
        fetchLoggedInUser();

        // Navigation for buttons
        editUserButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_userFragment_to_editUserFragment);
        });

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

    private void fetchLoggedInUser() {
        userService.fetchLoggedInUser(userDTO -> {
            if (userDTO != null) {
                requireActivity().runOnUiThread(() -> {
                    userViewModel.setUser(userDTO); // Save user info in ViewModel

                    userName.setText("Name: " + userDTO.getUsername());
                    userPhone.setText("Phone: " + (userDTO.getPhoneNumber() != 0 ? userDTO.getPhoneNumber() : "N/A"));
                    userEmail.setText("Email: " + (userDTO.getEmail() != null ? userDTO.getEmail() : "N/A"));

                    // ✅ Make the CardView visible after data is loaded
                    userCard.setVisibility(View.VISIBLE);
                });
            } else {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
