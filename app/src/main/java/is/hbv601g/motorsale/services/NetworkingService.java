package is.hbv601g.motorsale.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

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


    public void getRequest(String reqURL, final VolleyRawCallback callback) {
        JsonRequest request = new JsonRequest(Request.Method.GET, BASE_URL + reqURL, null,
                response -> callback.onSuccess(response.toString()),  // Pass raw JSON response
                error -> callback.onError(error.toString())) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };

        requestQueue.add(request);
    }


    /**
     * Sends a PATCH request with form-encoded data.
     *
     * @param endpoint  The API endpoint (e.g., "listings/update").
     * @param formBody  The form-encoded request body.
     * @param callback  A callback interface to handle the response or error.
     */
    public void patchRequestFormEncoded(String endpoint, String formBody, final VolleyCallback callback) {
        String url = BASE_URL + endpoint + "?" + formBody;
        Log.d("NetworkingService", "📡 PATCH Request to: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.PATCH, url,
                response -> {
                    Log.d("NetworkingService", "✅ Server Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject();
                        jsonResponse.put("message", response);
                        callback.onSuccess(jsonResponse);
                    } catch (JSONException e) {
                        Log.e("NetworkingService", "❌ JSON Parsing Error: " + e.getMessage());
                        callback.onError("JSON Parsing Error: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("NetworkingService", "❌ Network Error: " + error.toString());
                    callback.onError(error.toString());
                });

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
