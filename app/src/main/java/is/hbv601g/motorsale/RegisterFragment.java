package is.hbv601g.motorsale;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import is.hbv601g.motorsale.R;
import is.hbv601g.motorsale.services.UserService;

public class RegisterFragment extends Fragment {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText registerUsernameEditText;
    private EditText registerFirstNameEditText;
    private EditText registerLastNameEditText;
    private EditText registerEmailEditText;
    private EditText registerPasswordEditText;
    private EditText registerPhoneNumberEditText;
    private Button registerButton;
    private UserService userService;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Initialize views
        registerUsernameEditText = view.findViewById(R.id.registerUsernameEditText);
        registerFirstNameEditText = view.findViewById(R.id.registerFirstNameEditText);
        registerLastNameEditText = view.findViewById(R.id.registerLastNameEditText);
        registerEmailEditText = view.findViewById(R.id.registerEmailEditText);
        registerPasswordEditText = view.findViewById(R.id.registerPasswordEditText);
        registerPhoneNumberEditText = view.findViewById(R.id.registerPhoneNumberEditText);
        registerButton = view.findViewById(R.id.registerButton);

        // Initialize your service (similar to how you did in LoginFragment)
        userService = new UserService(requireContext());

        // Set up onClickListener for registerButton
        registerButton.setOnClickListener(v -> attemptRegistration());

        return view;
    }

    private void attemptRegistration() {
        // Get user input
        String username = registerUsernameEditText.getText().toString().trim();
        String firstName = registerFirstNameEditText.getText().toString().trim();
        String lastName = registerLastNameEditText.getText().toString().trim();
        String email = registerEmailEditText.getText().toString().trim();
        String password = registerPasswordEditText.getText().toString().trim();
        String phoneNumberString = registerPhoneNumberEditText.getText().toString().trim();

        // Basic validation
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(firstName) ||
                TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(phoneNumberString)) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert phoneNumber to int (since your entity uses int)
        int phoneNumber;
        try {
            phoneNumber = Integer.parseInt(phoneNumberString);
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Invalid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call userService.register(...) (you'll create a register method similar to login)

        userService.register(
                username,
                firstName,
                lastName,
                email,
                password,
                phoneNumber,
                success -> {
            if (success) {
                Toast.makeText(getActivity(), "Registration Successful", Toast.LENGTH_SHORT).show();

                //NavController navController = Navigation.findNavController(requireView());
                //navController.navigate(R.id.action_registerFragment_to_loginFragment);
                NavController navController = Navigation.findNavController(requireView());
                navController.navigateUp();
            } else {
                Toast.makeText(getActivity(), "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
