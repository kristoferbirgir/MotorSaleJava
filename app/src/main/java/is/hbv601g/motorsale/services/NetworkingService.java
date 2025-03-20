package is.hbv601g.motorsale.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * NetworkingService is responsible for handling network requests using Volley.
 * It provides methods to send HTTP requests to the backend server.
 */
public class NetworkingService {
    private static final String BASE_URL = "https://motor-sale-73501b8c368e.herokuapp.com/api/";
    private final RequestQueue requestQueue;
    private final Context context;
    private final Gson gson; // ✅ Use Gson for JSON parsing

    /**
     * Constructor that initializes the Volley request queue.
     *
     * @param context The application context used to create the request queue.
     */
    public NetworkingService(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        gson = new Gson();
    }

    /**
     * Retrieves the session ID stored in SharedPreferences.
     *
     * @return The saved session ID, or an empty string if not available.
     */
    public String getSessionId() {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return prefs.getString("SESSION_ID", "");
    }

    /**
     * Saves the session ID to SharedPreferences.
     *
     * @param sessionId The session ID received from the server.
     */
    public void saveSessionId(String sessionId) {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("SESSION_ID", sessionId);
        editor.apply();
        Log.d("NetworkingService", "✅ Saved session ID: " + sessionId);
    }

    /**
     * Extracts and saves session ID from response headers.
     *
     * @param headers Response headers from the server.
     */
    private void extractAndSaveSessionId(Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("Set-Cookie")) {
                    String[] cookies = entry.getValue().split(";");
                    for (String cookie : cookies) {
                        if (cookie.startsWith("JSESSIONID=")) {
                            String sessionId = cookie.split("=")[1];
                            saveSessionId(sessionId);
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * Sends a GET request to the specified API endpoint.
     *
     * @param reqURL    The endpoint URL.
     * @param callback  Callback interface to handle the response.
     */
    public void getRequest(String reqURL, final VolleyRawCallback callback) {
        StringRequest request = new StringRequest(Request.Method.GET, BASE_URL + reqURL,
                callback::onSuccess,
                error -> {
                    Log.e("NetworkingService", "❌ GET Error: " + error.toString());
                    callback.onError(error.toString());
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String sessionId = getSessionId();
                if (!sessionId.isEmpty()) {
                    headers.put("Cookie", "JSESSIONID=" + sessionId);
                }
                return headers;
            }
        };

        requestQueue.add(request);
    }

    /**
     * Sends a POST request to the specified API endpoint.
     *
     * @param endpoint  The relative path of the API endpoint (e.g., "User/login").
     * @param jsonBody  The JSON payload to be sent in the request body.
     * @param callback  A callback interface to handle the response or error.
     */
    public void postRequest(String endpoint, JSONObject jsonBody, final VolleyCallback callback) {
        String url = BASE_URL + endpoint;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    Log.d("NetworkingService", "✅ POST Success: " + response.toString());
                    callback.onSuccess(response);
                },
                error -> {
                    Log.e("NetworkingService", "❌ POST Error: " + error.toString());
                    callback.onError(error.toString());
                }) {
            @Override
            protected com.android.volley.Response<JSONObject> parseNetworkResponse(com.android.volley.NetworkResponse response) {
                extractAndSaveSessionId(response.headers);
                return super.parseNetworkResponse(response);
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Sends a PATCH request with form-encoded data.
     *
     * @param endpoint  The API endpoint (e.g., "User/updateUsername").
     * @param formBody  The form-encoded request body.
     * @param callback  A callback interface to handle the response or error.
     */
    public void patchRequestFormEncoded(String endpoint, String formBody, final VolleyCallback callback) {
        String url = BASE_URL + endpoint + "?" + formBody;
        Log.d("NetworkingService", "📡 PATCH Request to: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.PATCH, url,
                response -> {
                    Log.d("NetworkingService", "✅ PATCH Success: " + response);
                    callback.onSuccess(new JSONObject());
                },
                error -> {
                    Log.e("NetworkingService", "❌ PATCH Error: " + error.toString());
                    callback.onError(error.toString());
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String sessionId = getSessionId();
                if (!sessionId.isEmpty()) {
                    headers.put("Cookie", "JSESSIONID=" + sessionId);
                }
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

    /**
     * Sends a DELETE request to the specified API endpoint.
     *
     * @param endpoint  The API endpoint to delete from.
     * @param callback  A callback interface to handle the response or error.
     */
    public void deleteRequest(String endpoint, final VolleyCallback callback) {
        String url = BASE_URL + endpoint;

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Log.d("NetworkingService", "✅ Delete Success: " + response);
                    callback.onSuccess(new JSONObject());
                },
                error -> {
                    Log.e("NetworkingService", "❌ Delete Error: " + error.toString());
                    callback.onError(error.toString());
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String sessionId = getSessionId();
                if (!sessionId.isEmpty()) {
                    headers.put("Cookie", "JSESSIONID=" + sessionId);
                }
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

    /**
     * Callback interface for handling raw JSON responses.
     */
    public interface VolleyRawCallback {
        void onSuccess(String jsonResponse);
        void onError(String error);
    }

    /**
     * Interface for handling asynchronous responses from Volley requests.
     */
    public interface VolleyCallback {
        void onSuccess(JSONObject result);
        void onError(String error);
    }
}
