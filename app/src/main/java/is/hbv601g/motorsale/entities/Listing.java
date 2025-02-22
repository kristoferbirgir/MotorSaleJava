package is.hbv601g.motorsale.entities;



public class Listing {

    
    private Long listingId;

  
    private MotorVehicle motorVehicle;

    private Double price;


    private String address;

    private String postalCode;

    private String city;

    private String description;

    private Long userId;


    private byte[] image;

    // Default constructor
    public Listing() {}

    // Parameterized constructor
    public Listing(MotorVehicle motorVehicle, Double price, String address, String postalCode, String city,
                   String description, Long userId, byte[] image) {
        this.motorVehicle = motorVehicle;
        this.price = price;
        this.address = address;
        this.postalCode = postalCode;
        this.city = city;
        this.description = description;
        this.image = image;
        this.userId = userId;
    }

    // Getters and Setters
    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    public MotorVehicle getMotorVehicle() {
        return motorVehicle;
    }

    public void setMotorVehicle(MotorVehicle motorVehicle) {
        this.motorVehicle = motorVehicle;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
