package is.hbv601g.motorsale.services;

import android.content.Context;
import android.util.Log;
import org.json.JSONObject;

/**
 * Service class for handling motor vehicle updates through PATCH requests.
 */
public class MotorVehicleService {
    private static final String BASE_URL = "https://motor-sale-73501b8c368e.herokuapp.com/api/MotorVehicle/";
    private final NetworkingService networkingService;

    /**
     * Constructor initializes networking service.
     *
     * @param context The application context.
     */
    public MotorVehicleService(Context context) {
        this.networkingService = new NetworkingService(context);
    }

    /**
     * Sends a PATCH request to update a specific field of a motor vehicle.
     *
     * @param vehicleId The ID of the motor vehicle to update.
     * @param field The field to update (e.g., "updateMileage").
     * @param newValue The new value for the field.
     * @param callback Callback to handle update results.
     */
    public void updateField(Long vehicleId, String field, String newValue, UpdateCallback callback) {
        if (vehicleId == null || field == null || newValue == null) {
            Log.e("MotorVehicleService", "❌ Invalid parameters for updating motor vehicle.");
            callback.onUpdateResult(false);
            return;
        }

        // Construct endpoint and query parameters
        String endpoint = vehicleId + "/" + field;
        String queryParams = "new" + field.substring(6) + "=" + newValue;

        Log.d("MotorVehicleService", "📡 Sending PATCH request to MotorVehicle API: " + endpoint + "?" + queryParams);

        networkingService.patchRequestFormEncoded(endpoint, queryParams, new NetworkingService.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.d("MotorVehicleService", "✅ Successfully updated: " + field);
                callback.onUpdateResult(true);
            }

            @Override
            public void onError(String error) {
                Log.e("MotorVehicleService", "❌ Failed to update " + field + ": " + error);
                callback.onUpdateResult(false);
            }
        });
    }

    /**
     * Callback interface for handling update results.
     */
    public interface UpdateCallback {
        void onUpdateResult(boolean success);
    }
}
