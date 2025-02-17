package is.hbv601g.motorsale.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * NetworkingService is responsible for handling network requests using Volley.
 * It provides methods to send HTTP requests to the backend server.
 */
public class NetworkingService {
    private static final String BASE_URL = "https://motor-sale-73501b8c368e.herokuapp.com/api/";
    private final RequestQueue requestQueue;



    /**
     * Constructor that initializes the Volley request queue.
     *
     * @param context The application context used to create the request queue.
     */
    public NetworkingService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }


    /**
     * Sends a POST request to the specified API endpoint.
     *
     * @param endpoint  The relative path of the API endpoint (e.g., "users/login").
     * @param jsonBody  The JSON payload to be sent in the request body.
     * @param callback  A callback interface to handle the response or error.
     */
    public void postRequest(String endpoint, JSONObject jsonBody, final VolleyCallback callback) {
        String url = BASE_URL + endpoint;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> callback.onSuccess(response),
                error -> {
                    Log.e("Volley Error", "Error: " + error.toString());
                    callback.onError(error.toString());
                });

        requestQueue.add(jsonObjectRequest);
    }


    /**
     * Interface for handling asynchronous responses from Volley requests.
     */
    public interface VolleyCallback {
        void onSuccess(JSONObject result);
        void onError(String error);
    }
}
