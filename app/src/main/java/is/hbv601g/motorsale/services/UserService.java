package is.hbv601g.motorsale.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

import is.hbv601g.motorsale.DTOs.ListingDTO;
import is.hbv601g.motorsale.DTOs.UserDTO;
import is.hbv601g.motorsale.viewModels.UserViewModel;

/**
 * UserService handles user-related operations such as login, registration, fetching user details, and updates.
 */
public class UserService {
    private final NetworkingService networkingService;
    private final Gson gson;

    /**
     * Constructor that initializes the UserService with a NetworkingService instance.
     *
     * @param context The application context used for networking requests.
     */
    public UserService(Context context) {
        this.networkingService = new NetworkingService(context);
        this.gson = new Gson();
    }

    /**
     * Sends a login request to the server and handles session persistence.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @param callback A callback interface to handle login response.
     */
    public void login(String username, String password, LoginCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onLoginResult(false);
            return;
        }

        networkingService.postRequest("User/login", jsonBody, new NetworkingService.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.d("UserService", "✅ Login Successful");
                callback.onLoginResult(true);
            }

            @Override
            public void onError(String error) {
                Log.e("UserService", "❌ Login Failed: " + error);
                callback.onLoginResult(false);
            }
        });
    }

    /**
     * Fetches details of the currently logged-in user.
     *
     * @param callback Callback to handle user data.
     */
    public void fetchLoggedInUser(LoggedInCallback callback) {
        String url = "User/loggedIn";  // ✅ Correct endpoint

        networkingService.getRequest(url, new NetworkingService.VolleyRawCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                Log.d("UserService", "✅ LoggedIn API Response: " + jsonResponse);
                UserDTO user = gson.fromJson(jsonResponse, UserDTO.class);
                callback.onFound(user);
            }

            @Override
            public void onError(String error) {
                Log.e("UserService", "❌ LoggedIn API Error: " + error);
                callback.onFound(null);
            }
        });
    }
    /**
     * Sends a request to delete a user from the backend by ID and password.
     *
     * @param userId The ID of the user to be deleted.
     * @param password The password of the user for verification.
     * @param callback Callback to handle the success or failure of the delete operation.
     */
    public void deleteUser(String userId, String password, UserService.DeleteCallback callback) {
        if (userId == null) {
            Log.e("UserService", "Cannot delete User: ID is null.");
            callback.onDeleteResult(false);
            return;
        }

        String endpoint = "User/deleteUser?userId=" + userId + "&password=" + password;

        networkingService.deleteRequest(endpoint, new NetworkingService.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.d("UserService", " User deleted successfully.");
                callback.onDeleteResult(true);
            }

            @Override
            public void onError(String error) {
                Log.e("UserService", "Error deleting listing: " + error);
                callback.onDeleteResult(false);
            }
        });
    }

    /**
     * Signs out the currently logged-in user by clearing session storage
     * and resetting the user in the provided ViewModel.
     *
     * @param userViewModel The UserViewModel to clear the user from.
     */
    public void signOut(UserViewModel userViewModel) {
        networkingService.clearSession();
        userViewModel.setUser(null);
        Log.d("UserService", "User signed out");
    }



    /**
     * Registers a new user.
     *
     * @param username The desired username.
     * @param firstName User's first name.
     * @param lastName User's last name.
     * @param email User's email.
     * @param password User's password.
     * @param phoneNumber User's phone number.
     * @param callback Callback for registration result.
     */
    public void register(String username, String firstName, String lastName, String email, String password, int phoneNumber, RegisterCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("firstName", firstName);
            jsonBody.put("lastName", lastName);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("phoneNumber", phoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onRegisterResult(false);
            return;
        }

        networkingService.postRequest("User/signup", jsonBody, new NetworkingService.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                callback.onRegisterResult(true);
            }

            @Override
            public void onError(String error) {
                callback.onRegisterResult(false);
            }
        });
    }

    /**
     * Fetches user details by email.
     *
     * @param email The email to search for.
     * @param callback Callback to handle user data.
     */
    public void fetchUserByEmail(String email, FindByUsernameCallback callback) {
        String url = "User/?username=" + email;

        networkingService.getRequest(url, new NetworkingService.VolleyRawCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                Log.d("UserService", "✅ API Response: " + jsonResponse);
                UserDTO user = gson.fromJson(jsonResponse, UserDTO.class);
                callback.onFound(user);
            }

            @Override
            public void onError(String error) {
                Log.e("UserService", "❌ API Error: " + error);
                callback.onFound(null);
            }
        });
    }

    /**
     * Updates a specific field of the user profile.
     *
     * @param userId The user ID.
     * @param field The field to update.
     * @param newValue The new value.
     * @param callback Callback for update status.
     */
    public void updateUserField(Long userId, String field, String newValue, UpdateCallback callback) {
        if (userId == null || field == null || newValue == null) {
            Log.e("UserService", "❌ Invalid input parameters");
            callback.onUpdateResult(false);
            return;
        }
        try {
            String encodedValue = java.net.URLEncoder.encode(newValue, StandardCharsets.UTF_8.toString());
            String endpoint = "User/" + field;
            String queryParams = "userId=" + userId + "&new" + field.substring(6) + "=" + encodedValue;
            Log.d("UserService", "📡 Sending PATCH to: " + endpoint + "?" + queryParams);

            networkingService.patchRequestFormEncoded(endpoint, queryParams, new NetworkingService.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    callback.onUpdateResult(true);
                }

                @Override
                public void onError(String error) {
                    callback.onUpdateResult(false);
                }
            });
        } catch (Exception e) {
            Log.e("UserService", "❌ Error in updateUserField: " + e.getMessage());
            callback.onUpdateResult(false);
        }
    }

    /**
     * Fetches all listings for a given user.
     *
     * @param userId The user ID.
     * @param callback Callback for listings result.
     */
    public void fetchUserListings(Long userId, FindUserListingsCallback callback) {
        String url = "listings/listingsByUserId?userId=" + userId;

        networkingService.getRequest(url, new NetworkingService.VolleyRawCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                try {
                    Log.d("ListingsService", "✅ API Response: " + jsonResponse);
                    Type listType = new TypeToken<List<ListingDTO>>() {}.getType();
                    List<ListingDTO> listings = gson.fromJson(jsonResponse, listType);
                    callback.onFindAllResult(listings);
                } catch (Exception e) {
                    Log.e("ListingsService", "❌ JSON Parsing Error", e);
                    callback.onFindAllResult(null);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("ListingsService", "❌ API Error: " + error);
                callback.onFindAllResult(null);
            }
        });
    }

    // Callback interfaces

    /**
     * Callback interface for handling the result of a logged-in user fetch operation.
     */
    public interface LoggedInCallback {
        /**
         * Called when the user fetch request completes.
         *
         * @param userDTO The UserDTO if found, or null if the request failed.
         */
        void onFound(UserDTO userDTO);
    }


    /**
     * Callback interface for handling the result of a user update operation.
     */
    public interface UpdateCallback {
        /**
         * Called when the update request completes.
         *
         * @param success True if the update was successful, false otherwise.
         */
        void onUpdateResult(boolean success);
    }

    /**
     * Callback interface for handling the result of a user registration request.
     */
    public interface RegisterCallback {
        /**
         * Called when the registration request completes.
         *
         * @param success True if registration was successful, false otherwise.
         */
        void onRegisterResult(boolean success);
    }


    /**
     * Callback interface for handling the result of fetching a user by username (email).
     */
    public interface FindByUsernameCallback {
        /**
         * Called when the user fetch request completes.
         *
         * @param userDTO The UserDTO if found, or null if the request failed.
         */
        void onFound(UserDTO userDTO);
    }

    /**
     * Interface for handling user listings callback.
     */
    public interface FindUserListingsCallback {
        void onFindAllResult(List<ListingDTO> listings);
    }

    /**
     * Interface for handling delete results.
     */
    public interface DeleteCallback {
        void onDeleteResult(boolean success);
    }
    /**
     * Interface for handling login results.
     */
    public interface LoginCallback {
        /**
         * Called when the login request completes.
         *
         * @param success True if login was successful, false otherwise.
         */
        void onLoginResult(boolean success);
    }

}
