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
import is.hbv601g.motorsale.viewModels.UserViewModel;

/**
 * Service class for handling operations related to vehicle listings.
 * Uses Volley for networking and Gson for JSON parsing.
 */
public class ListingService {
    private final NetworkingService networkingService;
    private final Gson gson;

    /**
     * Constructor to initialize `ListingService`.
     *
     * @param context The application context used for networking requests.
     */
    public ListingService(Context context) {
        this.networkingService = new NetworkingService(context);
        this.gson = new Gson();
    }

    /**
     * Fetches all listings from the backend.
     *
     * @param callback Callback to handle the response (list of listings or error).
     */
    public void findAll(FindAllCallback callback) {
        networkingService.getRequest("listings", new NetworkingService.VolleyRawCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                try {
                    Log.d("ListingService", "📡 API Response: " + jsonResponse);
                    Type listType = new TypeToken<List<ListingDTO>>() {}.getType();
                    List<ListingDTO> listings = gson.fromJson(jsonResponse, listType);
                    Log.d("ListingService", "✅ Parsed Listings Count: " + listings.size());
                    callback.onFindAllResult(listings);
                } catch (Exception e) {
                    Log.e("ListingService", "❌ JSON parsing error", e);
                    callback.onFindAllResult(null);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("ListingService", "❌ API Error: " + error);
                callback.onFindAllResult(null);
            }
        });
    }

    /**
     * Fetches a listing by its ID.
     *
     * @param listingId The unique identifier of the listing.
     * @param callback  Callback to handle the response (listing object or error).
     */
    public void findById(String listingId, FindByIdCallback callback) {
        if (listingId == null) {
            Log.e("ListingService", "❌ Invalid listing ID provided.");
            callback.onFindByIdResult(null);
            return;
        }

        String url = "listings/getListing?listingId=" + listingId;
        networkingService.getRequest(url, new NetworkingService.VolleyRawCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                try {
                    Log.d("ListingService", "📡 API Response: " + jsonResponse);
                    ListingDTO listing = gson.fromJson(jsonResponse, ListingDTO.class);
                    callback.onFindByIdResult(listing);
                } catch (Exception e) {
                    Log.e("ListingService", "❌ JSON parsing error", e);
                    callback.onFindByIdResult(null);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("ListingService", "❌ API Error: " + error);
                callback.onFindByIdResult(null);
            }
        });
    }

    /**
     * Creates a new listing in the backend.
     *
     * @param listing  The `Listing` object to be created.
     * @param callback Callback to handle the response (created listing object or error).
     */
    public void createListing(Listing listing, FindByIdCallback callback) {
        if (listing == null) {
            Log.e("ListingService", "❌ Cannot create listing: Invalid data.");
            callback.onFindByIdResult(null);
            return;
        }

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
                    Log.e("ListingService", "❌ Error creating listing: " + error);
                    callback.onFindByIdResult(null);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onFindByIdResult(null);
        }
    }

    /**
     * Updates a specific field of a listing by sending a PATCH request.
     *
     * @param listingId The unique ID of the listing to update.
     * @param field     The field to update (e.g., "updatePrice", "updateDescription").
     * @param newValue  The new value to set for the field.
     * @param callback  Callback to handle the update success or failure.
     */
    public void updateField(String listingId, String field, String newValue, UpdateCallback callback) {
        if (listingId == null || field == null || newValue == null) {
            Log.e("ListingService", "❌ Invalid parameters: listingId=" + listingId + ", field=" + field + ", newValue=" + newValue);
            callback.onUpdateResult(false);
            return;
        }

        try {
            // Ensure special characters in the newValue are properly encoded
            String encodedValue = URLEncoder.encode(newValue, StandardCharsets.UTF_8.toString());

            // Construct API endpoint with query parameters
            String endpoint = "listings/" + listingId + "/" + field;
            String queryParams = "new" + field.substring(6) + "=" + encodedValue; // Converts "updatePrice" to "newPrice"

            Log.d("ListingService", "📡 Sending PATCH request to: " + endpoint + "?" + queryParams);

            // Send PATCH request using NetworkingService
            networkingService.patchRequestFormEncoded(endpoint, queryParams, new NetworkingService.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    Log.d("ListingService", "✅ Update successful for " + field);
                    callback.onUpdateResult(true);
                }

                @Override
                public void onError(String error) {
                    Log.e("ListingService", "❌ Update failed for " + field + ": " + error);
                    callback.onUpdateResult(false);
                }
            });
        } catch (Exception e) {
            Log.e("ListingService", "❌ Encoding error in updateField: " + e.getMessage());
            callback.onUpdateResult(false);
        }
    }

    /**
     * Deletes a listing by its ID.
     *
     * @param listingId The ID of the listing to delete.
     * @param callback  Callback to handle success or failure.
     */
    public void deleteListing(String listingId, DeleteCallback callback) {
        if (listingId == null) {
            Log.e("ListingService", "Cannot delete listing: ID is null.");
            callback.onDeleteResult(false);
            return;
        }

        String endpoint = "listings/deleteListing?listingId=" + listingId;

        networkingService.deleteRequest(endpoint, new NetworkingService.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.d("ListingService", " Listing deleted successfully.");
                callback.onDeleteResult(true);
            }

            @Override
            public void onError(String error) {
                Log.e("ListingService", "Error deleting listing: " + error);
                callback.onDeleteResult(false);
            }
        });
    }

    public void filterListings(String query, NetworkingService.VolleyRawCallback callback) {
        networkingService.getRequest(query, callback);
    }


    /**
     * Interface for handling delete results.
     */
    public interface DeleteCallback {
        void onDeleteResult(boolean success);
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
