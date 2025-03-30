package is.hbv601g.motorsale;

import static android.app.ProgressDialog.show;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import is.hbv601g.motorsale.services.UserService;
import is.hbv601g.motorsale.viewModels.UserViewModel;

public class UserFragment extends Fragment {
    private TextView userName, userPhone, userEmail;
    private Button editUserButton, createListingButton, myListingsButton, deleteUserButton, signoutButton;
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
        deleteUserButton = view.findViewById(R.id.deleteUserButton);
        createListingButton = view.findViewById(R.id.createListingButton);
        myListingsButton = view.findViewById(R.id.myListingsButton);
        signoutButton = view.findViewById(R.id.signoutButton);


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

        deleteUserButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            deleteMyUser(navController);
        });
        signoutButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            userService.signOut(userViewModel);
            navController.navigate(R.id.listingsFragment);
            Toast.makeText(getActivity(), "User Signed out successfully!", Toast.LENGTH_SHORT).show();


        });

        return view;
    }


    /**
     * Initiates the user deletion process by showing a password confirmation dialog,
     * if a user is currently logged in.
     *
     * @param navController The NavController used for navigation upon successful deletion.
     */

    private void deleteMyUser(NavController navController) {
        if (userViewModel.getUser().getValue() != null) {
            showPasswordDialog(navController);
        }
    }



    /**
     * Displays a dialog prompting the user to enter their password for account deletion.
     * On confirmation, proceeds to delete the user using the provided credentials.
     *
     * @param navController The NavController used for navigation after deletion.
     */

    private void showPasswordDialog(NavController navController) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sláðu inn lykilorðið þitt til að staðfesta:");
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Hætta við", (dialog, which) -> dialog.cancel());

        builder.setNegativeButton("Eyða aðgang", (dialog, which) -> {});

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button deleteButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            deleteButton.setBackgroundColor(Color.RED);
            deleteButton.setTextColor(Color.WHITE);

            deleteButton.setOnClickListener(v -> {
                String password = input.getText().toString();
                if (userViewModel.getUser().getValue() != null) {
                    deleteUser(userViewModel.getUser().getValue().getUserId(), password, navController);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }


    /**
     * Calls the backend to delete the current user using their ID and password.
     * If successful, signs the user out and navigates back to the listings screen.
     *
     * @param UserId        The ID of the user to delete.
     * @param password      The user's password for authentication.
     * @param navController The NavController used for navigation after deletion.
     */

    public void deleteUser(Long UserId, String password, NavController navController) {
        userService.deleteUser(UserId.toString(), password, success -> {
            if (success) {
                Toast.makeText(getActivity(), "User deleted successfully!", Toast.LENGTH_SHORT).show();
                userService.signOut(userViewModel);
                navController.navigate(R.id.listingsFragment);

            } else {
                Toast.makeText(getActivity(), "Failed to delete user", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Fetches the logged-in user's information from the backend and updates the UI
     * and view model accordingly. Displays a toast if the request fails.
     */

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
