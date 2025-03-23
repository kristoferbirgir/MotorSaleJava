package is.hbv601g.motorsale.DTOs;

import is.hbv601g.motorsale.entities.MotorVehicle;

import android.util.Base64;

public class ListingDTO {


    private Long listingId;

    private MotorVehicle motorVehicle;

    private Double price;

    private String address;

    private String postalCode;

    private String city;

    private String description;


    private Long userId;

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    private String imageBase64;


    public ListingDTO(Long listingId, MotorVehicle motorVehicle, Double price, String address, String postalCode, String city, String description, Long userId, byte[] image) {
        this.listingId = listingId;
        this.motorVehicle = motorVehicle;
        this.price = price;
        this.address = address;
        this.postalCode = postalCode;
        this.city = city;
        this.description = description;
        this.imageBase64 = image != null ? Base64.encodeToString(image, Base64.DEFAULT) : null;
        this.userId = userId;
    }

    public Long getListingId() {
        return listingId;
    }

    public MotorVehicle getMotorVehicle() {
        return motorVehicle;
    }

    public Double getPrice() {
        return price;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getDescription() {
        return description;
    }

    public Long getUserId() {
        return userId;
    }

    public String getImageBase64() {
        return imageBase64;
    }



}
