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
    private EditText registerUsernameEditText;
    private EditText registerFirstNameEditText;
    private EditText registerLastNameEditText;
    private EditText registerEmailEditText;
    private EditText registerPasswordEditText;
    private EditText registerPhoneNumberEditText;
    private Button registerButton;
    private UserService userService;

    public RegisterFragment() {
    }

    /**
     * Inflates the fragment layout and initializes UI elements.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        registerUsernameEditText = view.findViewById(R.id.registerUsernameEditText);
        registerFirstNameEditText = view.findViewById(R.id.registerFirstNameEditText);
        registerLastNameEditText = view.findViewById(R.id.registerLastNameEditText);
        registerEmailEditText = view.findViewById(R.id.registerEmailEditText);
        registerPasswordEditText = view.findViewById(R.id.registerPasswordEditText);
        registerPhoneNumberEditText = view.findViewById(R.id.registerPhoneNumberEditText);
        registerButton = view.findViewById(R.id.registerButton);

        userService = new UserService(requireContext());

        registerButton.setOnClickListener(v -> attemptRegistration());

        return view;
    }

    /**
     * Attempts to register a new user.
     */
    private void attemptRegistration() {
        String username = registerUsernameEditText.getText().toString().trim();
        String firstName = registerFirstNameEditText.getText().toString().trim();
        String lastName = registerLastNameEditText.getText().toString().trim();
        String email = registerEmailEditText.getText().toString().trim();
        String password = registerPasswordEditText.getText().toString().trim();
        String phoneNumberString = registerPhoneNumberEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(firstName) ||
                TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(phoneNumberString)) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int phoneNumber;
        try {
            phoneNumber = Integer.parseInt(phoneNumberString);
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Invalid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

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

                NavController navController = Navigation.findNavController(requireView());
                navController.navigateUp();
            } else {
                Toast.makeText(getActivity(), "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
