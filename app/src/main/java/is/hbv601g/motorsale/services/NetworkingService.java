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

public class NetworkingService {
    private static final String BASE_URL = "https://motor-sale-73501b8c368e.herokuapp.com/api/";
    private final RequestQueue requestQueue;

    public NetworkingService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

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

    public interface VolleyCallback {
        void onSuccess(JSONObject result);
        void onError(String error);
    }
}
