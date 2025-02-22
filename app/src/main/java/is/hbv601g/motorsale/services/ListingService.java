package is.hbv601g.motorsale.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import is.hbv601g.motorsale.DTOs.ListingDTO;
import is.hbv601g.motorsale.services.NetworkingService;

/**
 * ListingsService handles operations related to vehicle listings using GSON for JSON parsing.
 */
public class ListingService {
    private final NetworkingService networkingService;

    private final Gson gson;

    /**
     * Constructor initializes the ListingsService with a NetworkingService instance.
     *
     * @param context The application context used for networking requests.
     */
    public ListingService(Context context) {
        this.networkingService = new NetworkingService(context);
        this.gson = new Gson();
    }

    public void findAll(FindAllCallback callback) {
        networkingService.getRequest("listings", new NetworkingService.VolleyRawCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                try {
                    // 🔍 Debug: Print raw API response
                    Log.d("ListingsService", "API Response: " + jsonResponse);

                    // Convert JSON response to List<ListingDTO>
                    Type listType = new TypeToken<List<ListingDTO>>() {}.getType();
                    List<ListingDTO> listings = gson.fromJson(jsonResponse, listType);

                    // 🔍 Debug: Print parsed listings count
                    Log.d("ListingsService", "Parsed Listings Count: " + listings.size());

                    // Return the parsed listings
                    callback.onFindAllResult(listings);

                } catch (Exception e) {
                    Log.e("ListingsService", "JSON parsing error", e);
                    callback.onFindAllResult(null);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("ListingsService", "API Error: " + error);
                callback.onFindAllResult(null);
            }
        });
    }



    /**
     * Interface for handling multiple listings results.
     */
    public interface FindAllCallback {
        void onFindAllResult(List<ListingDTO> listings);
    }

    /**
     * Interface for handling a single listing result.
     */
    public interface FindByIdCallback {
        void onFindByIdResult(ListingDTO listing);
    }
}
