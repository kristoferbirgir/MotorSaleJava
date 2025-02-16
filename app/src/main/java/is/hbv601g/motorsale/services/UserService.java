package is.hbv601g.motorsale.services;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

public class UserService {
    private final NetworkingService networkingService;
    public UserService(Context context) {
        this.networkingService = new NetworkingService(context);
    }

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

    public interface LoginCallback {
        void onLoginResult(boolean success);
    }
}
