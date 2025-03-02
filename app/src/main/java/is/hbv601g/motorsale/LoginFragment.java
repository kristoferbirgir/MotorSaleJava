package is.hbv601g.motorsale;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import is.hbv601g.motorsale.DTOs.UserDTO;
import is.hbv601g.motorsale.entities.User;
import is.hbv601g.motorsale.services.UserService;
import is.hbv601g.motorsale.viewModels.UserViewModel;

/**
 * LoginFragment handles the user login UI and authentication logic.
 * It interacts with UserService to authenticate users.
 */
public class LoginFragment extends Fragment {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private UserService userService;
    private TextView createAccountTextView;
    private UserViewModel userViewModel;

    /**
     * Inflates the fragment layout and initializes UI elements.
     *
     * @param inflater LayoutInflater used to inflate the view.
     * @param container The parent container of the fragment.
     * @param savedInstanceState The saved instance state.
     * @return The root view of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        loginButton = view.findViewById(R.id.loginButton);
        userService = new UserService(requireContext());
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class); // Use ViewModelProvider

        loginButton.setOnClickListener(v -> attemptLogin());

        createAccountTextView = view.findViewById(R.id.createAccountTextView);

        createAccountTextView.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_loginFragment_to_registerFragment);
        });

        // Observe user session and update UI
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                navigateToProfile();
            }
        });

        return view;
    }

    /**
     * Attempts to log in by validating input fields and calling UserService.
     * If fields are empty, it shows a toast message.
     */
    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        userService.login(email, password, success -> {
            if (success) {
                Toast.makeText(getActivity(), "Login Successful!", Toast.LENGTH_SHORT).show();

                userService.fetchUserByEmail(email, user -> {
                    if (user != null) {
                        userViewModel.setUser(user);
                        Log.d("Login Success", "User found: " + user.getEmail());


                    } else {
                        Log.e("Login Error", "Failed to fetch user data");
                    }
                });
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_loginFragment_to_listingsFragment);

            } else {
                Toast.makeText(getActivity(), "Login Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToProfile() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_loginFragment_to_listingsFragment);
    }
}
