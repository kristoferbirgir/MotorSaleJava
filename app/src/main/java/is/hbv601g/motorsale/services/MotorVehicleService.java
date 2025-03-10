package is.hbv601g.motorsale.services;

import android.content.Context;
import android.util.Log;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

/**
 * Service class for handling motor vehicle updates through PATCH requests.
 */
public class MotorVehicleService {
    private final NetworkingService networkingService;

    /**
     * Constructor initializes networking service.
     *
     * @param context The application context used for networking requests.
     */
    public MotorVehicleService(Context context) {
        this.networkingService = new NetworkingService(context);
    }

    /**
     * Generic method to send PATCH requests for updating motor vehicle fields.
     *
     * @param vehicleId The ID of the motor vehicle to be updated.
     * @param endpoint  The specific field to update (e.g., "updateMileage").
     * @param newValue  The new value to set.
     * @param callback  The callback to handle update results.
     */
    public void updateField(Long vehicleId, String endpoint, String newValue, UpdateCallback callback) {
        if (vehicleId == null || endpoint == null || newValue == null || newValue.isEmpty()) {
            Log.e("MotorVehicleService", "❌ Invalid parameters: vehicleId=" + vehicleId + ", endpoint=" + endpoint + ", newValue=" + newValue);
            callback.onUpdateResult(false);
            return;
        }

        try {
            // Correct the URL structure to match the working ListingService logic
            String url = "MotorVehicle/" + vehicleId + "/" + endpoint;

            // Get the correct query parameter name
            String paramName = getFormParamName(endpoint);
            if (paramName == null) {
                Log.e("MotorVehicleService", "❌ Unknown endpoint: " + endpoint);
                callback.onUpdateResult(false);
                return;
            }

            // Encode the parameter properly
            String encodedValue = URLEncoder.encode(newValue, StandardCharsets.UTF_8.toString());
            String formBody = paramName + "=" + encodedValue;

            Log.d("MotorVehicleService", "📡 PATCH Request: " + url + " | Body: " + formBody);

            // Send the PATCH request
            networkingService.patchRequestFormEncoded(url, formBody, new NetworkingService.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    Log.d("MotorVehicleService", "✅ Update successful for " + endpoint + " | Response: " + result.toString());
                    callback.onUpdateResult(true);
                }

                @Override
                public void onError(String error) {
                    Log.e("MotorVehicleService", "❌ Error updating " + endpoint + ": " + error);
                    callback.onUpdateResult(false);
                }
            });
        } catch (Exception e) {
            Log.e("MotorVehicleService", "❌ Encoding error: " + e.getMessage());
            callback.onUpdateResult(false);
        }
    }

    /**
     * Determines the correct query parameter name based on the API endpoint.
     *
     * @param endpoint The endpoint being accessed (e.g., "updateMileage").
     * @return The corresponding form parameter name for the backend request.
     */
    private String getFormParamName(String endpoint) {
        switch (endpoint) {
            case "updateMileage":
                return "newMileage"; // int
            case "updateEngineSize":
                return "newEngineSize"; // String
            case "updateHorsePower":
                return "newHorsePower"; // int
            case "updateFuelConsumption":
                return "newFuelConsumption"; // double
            case "updateColor":
                return "newColor"; // String
            case "updateModelYear":
                return "newModelYear"; // int
            case "updateMotorVehicleType":
                return "newMotorVehicleType"; // Enum (CAR, SUV, TRUCK, etc.)
            case "updateTransmissionType":
                return "newTransmissionType"; // Enum (AUTOMATIC, MANUAL)
            case "updateFuelType":
                return "newFuelType"; // Enum (PETROL, DIESEL, ELECTRIC, etc.)
            case "updateBatteryRange":
                return "newBatteryRange"; // int
            default:
                Log.e("MotorVehicleService", "❌ Unknown endpoint: " + endpoint);
                return null;
        }
    }

    /**
     * Callback interface for handling update results.
     */
    public interface UpdateCallback {
        void onUpdateResult(boolean success);
    }
}
