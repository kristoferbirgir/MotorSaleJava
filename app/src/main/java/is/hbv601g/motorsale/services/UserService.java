package is.hbv601g.motorsale.services;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * UserService handles user-related operations such as login.
 * It interacts with the backend server using NetworkingService.
 */
public class UserService {
    private final NetworkingService networkingService;

    /**
     * Constructor that initializes the UserService with a NetworkingService instance.
     *
     * @param context The application context used for networking requests.
     */
    public UserService(Context context) {
        this.networkingService = new NetworkingService(context);
    }

    /**
     * Sends a login request to the server with the provided username and password.
     *
     * @param username The username of the user attempting to log in.
     * @param password The password of the user.
     * @param callback A callback interface to handle the login response.
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
                callback.onLoginResult(true);
            }
            @Override
            public void onError(String error) {
                callback.onLoginResult(false);
            }
        });
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

    public void register(String username,
                         String firstName,
                         String lastName,
                         String email,
                         String password,
                         int phoneNumber,
                         RegisterCallback callback) {
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

        // 'User/signup' hits POST /api/User/signup
        networkingService.postRequest("User/signup", jsonBody, new NetworkingService.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                // Registration succeeded (HTTP 200)
                callback.onRegisterResult(true);
            }

            @Override
            public void onError(String error) {
                // Registration failed (HTTP 400 or something else)
                callback.onRegisterResult(false);
            }
        });
    }

    public interface RegisterCallback {
        void onRegisterResult(boolean success);
    }
}