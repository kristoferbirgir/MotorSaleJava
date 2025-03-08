package is.hbv601g.motorsale.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import is.hbv601g.motorsale.DTOs.ListingDTO;
import is.hbv601g.motorsale.entities.Listing;
import is.hbv601g.motorsale.services.NetworkingService;
import is.hbv601g.motorsale.viewModels.UserViewModel;

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

    /**
     * Finds all listings in the database.
     *
     * @param callback
     */
    public void findAll(FindAllCallback callback) {
        networkingService.getRequest("listings", new NetworkingService.VolleyRawCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                try {
                    Log.d("ListingsService", "API Response: " + jsonResponse);
                    Type listType = new TypeToken<List<ListingDTO>>() {}.getType();
                    List<ListingDTO> listings = gson.fromJson(jsonResponse, listType);
                    Log.d("ListingsService", "Parsed Listings Count: " + listings.size());
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
     * Finds a listing by its ID.
     *
     * @param listingId
     * @param callback
     */
    public void findById(String listingId, FindByIdCallback callback) {
        String url = "listings/getListing?listingId=" + listingId;
        networkingService.getRequest(url, new NetworkingService.VolleyRawCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                try {
                    Log.d("ListingService" , "API Response: " + jsonResponse);
                    ListingDTO listing = gson.fromJson(jsonResponse, ListingDTO.class);
                    callback.onFindByIdResult(listing);
                } catch (Exception e) {
                    Log.e("ListingService", "JSON parsing error", e);
                    callback.onFindByIdResult(null);
                }
            }
            @Override
            public void onError(String error) {
                Log.e("ListingService", "API Error: " + error);
                callback.onFindByIdResult(null);
            }
        });
    }

    /**
     * Creates a new listing in the database.
     *
     * @param listing
     * @param callback
     */
    public void createListing(Listing listing, FindByIdCallback callback) {
        String listingJson = gson.toJson(listing);
        try {
            JSONObject jsonBody = new JSONObject(listingJson);
            networkingService.postRequest("listings/createListing", jsonBody, new NetworkingService.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    ListingDTO createdListing = gson.fromJson(result.toString(), ListingDTO.class);
                    callback.onFindByIdResult(createdListing);
                }

                @Override
                public void onError(String error) {
                    Log.e("ListingService", "Error creating listing: " + error);
                    callback.onFindByIdResult(null);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onFindByIdResult(null);
        }
    }

    public void updateField(String listingId, String field, String newValue, UpdateCallback callback) {
        try {
            String encodedValue = URLEncoder.encode(newValue, StandardCharsets.UTF_8.toString());
            String formBody = "new" + capitalizeFirstLetter(field.replace("update", "")) + "=" + encodedValue;

            String url = "listings/" + listingId + "/" + field;

            Log.d("ListingService", "PATCH URL: " + url);
            Log.d("ListingService", "Form Body: " + formBody);

            networkingService.patchRequestFormEncoded(url, formBody, new NetworkingService.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    Log.d("ListingService", "Update successful for " + field);
                    callback.onUpdateResult(true);
                }

                @Override
                public void onError(String error) {
                    Log.e("ListingService", "Error updating " + field + ": " + error);
                    callback.onUpdateResult(false);
                }
            });
        } catch (Exception e) {
            Log.e("ListingService", "Encoding error: " + e.getMessage());
            callback.onUpdateResult(false);
        }
    }


    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
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
    /**
     * Interface for handling update results.
     */
    public interface UpdateCallback {
        void onUpdateResult(boolean success);
    }
}
