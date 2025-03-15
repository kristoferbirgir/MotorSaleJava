package is.hbv601g.motorsale;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import is.hbv601g.motorsale.DTOs.UserDTO;
import is.hbv601g.motorsale.services.UserService;
import is.hbv601g.motorsale.viewModels.UserViewModel;


public class EditUserFragment extends Fragment {

    private EditText editTextUsername, editTextEmailAddress, editTextPhone, editTextTextPassword;
    private Button saveButton;
    private UserService userService;
    private UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_user, container, false);
        editTextUsername = view.findViewById(R.id.editTextUsername);
        editTextEmailAddress = view.findViewById(R.id.editTextEmailAddress);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextTextPassword = view.findViewById(R.id.editTextTextPassword);
        saveButton = view.findViewById(R.id.button);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userService = new UserService(requireContext());
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        UserDTO currentUser = userViewModel.getUser().getValue();
        if (currentUser != null) {
            editTextUsername.setText(currentUser.getUsername());
            editTextEmailAddress.setText(currentUser.getEmail());
            editTextPhone.setText(String.valueOf(currentUser.getPhoneNumber()));
            editTextTextPassword.setText("");
        } else {
            Toast.makeText(getContext(), "User info not available", Toast.LENGTH_SHORT).show();
        }

        saveButton.setOnClickListener(v -> updateUserInfo());
    }

    private void updateUserInfo() {
        String newUsername = editTextUsername.getText().toString().trim();
        String newEmail = editTextEmailAddress.getText().toString().trim();
        String newPhone = editTextPhone.getText().toString().trim();
        String newPassword = editTextTextPassword.getText().toString().trim();

        if (userViewModel.getUser().getValue() == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        Long userId = userViewModel.getUser().getValue().getUserId();

        final int[] updatesSent = {0};
        final int totalUpdates = 4;

        Runnable checkCompletion = () -> {
            updatesSent[0]++;
            if (updatesSent[0] == totalUpdates) {
                String keyForFetch = !TextUtils.isEmpty(newUsername)
                        ? newUsername
                        : userViewModel.getUser().getValue().getUsername();
                userService.fetchUserByEmail(keyForFetch, userDTO -> {
                    if (userDTO != null) {
                        userViewModel.setUser(userDTO);
                    } else {
                        Toast.makeText(getContext(), "Failed to refresh user info", Toast.LENGTH_SHORT).show();
                    }
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.action_editUserFragment_to_userFragment);
                });
            }
        };

        if (!TextUtils.isEmpty(newUsername)) {
            userService.updateUserField(userId, "updateUsername", newUsername, success -> {
                if (!success) {
                    Toast.makeText(getContext(), "Failed to update username", Toast.LENGTH_SHORT).show();
                }
                checkCompletion.run();
            });
        } else {
            checkCompletion.run();
        }

        if (!TextUtils.isEmpty(newEmail)) {
            userService.updateUserField(userId, "updateEmail", newEmail, success -> {
                if (!success) {
                    Toast.makeText(getContext(), "Failed to update email", Toast.LENGTH_SHORT).show();
                }
                checkCompletion.run();
            });
        } else {
            checkCompletion.run();
        }

        if (!TextUtils.isEmpty(newPhone)) {
            userService.updateUserField(userId, "updatePhoneNumber", newPhone, success -> {
                if (!success) {
                    Toast.makeText(getContext(), "Failed to update phone number", Toast.LENGTH_SHORT).show();
                }
                checkCompletion.run();
            });
        } else {
            checkCompletion.run();
        }

        if (!TextUtils.isEmpty(newPassword)) {
            userService.updateUserField(userId, "updatePassword", newPassword, success -> {
                if (!success) {
                    Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                }
                checkCompletion.run();
            });
        } else {
            checkCompletion.run();
        }
        Toast.makeText(getContext(), "User info updated successfully", Toast.LENGTH_SHORT).show();
    }
}