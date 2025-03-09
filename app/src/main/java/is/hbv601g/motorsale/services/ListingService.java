package is.hbv601g.motorsale.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
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
