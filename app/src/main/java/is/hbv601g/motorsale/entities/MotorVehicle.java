package is.hbv601g.motorsale.entities;


import is.hbv601g.motorsale.Enums.FuelType;
import is.hbv601g.motorsale.Enums.MotorVehicleType;
import is.hbv601g.motorsale.Enums.TransmissionType;

public class MotorVehicle {


    private Long vehicleId;


    private MotorVehicleType motorVehicleType;

    private String brand;

    private String model;

    private int modelYear;


    private FuelType fuelType;

    private String color;

    private String engineSize;

    private int horsePower;

    private int mileage;

    private int batteryRange;

    private double fuelConsumption;


    private TransmissionType transmissionType;

    // Default constructor
    public MotorVehicle() {}

    // Parameterized constructor
    public MotorVehicle(MotorVehicleType motorVehicleType, String brand, String model, int modelYear, FuelType fuelType,
                        String color, String engineSize, int horsePower, int mileage, int batteryRange,
                        double fuelConsumption, TransmissionType transmissionType) {
        this.motorVehicleType = motorVehicleType;
        this.brand = brand;
        this.model = model;
        this.modelYear = modelYear;
        this.fuelType = fuelType;
        this.color = color;
        this.engineSize = engineSize;
        this.horsePower = horsePower;
        this.mileage = mileage;
        this.batteryRange = batteryRange;
        this.fuelConsumption = fuelConsumption;
        this.transmissionType = transmissionType;
    }

    // Getters and Setters
    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public MotorVehicleType getMotorVehicleType() {
        return motorVehicleType;
    }

    public void setMotorVehicleType(MotorVehicleType motorVehicleType) {
        this.motorVehicleType = motorVehicleType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getModelYear() {
        return modelYear;
    }

    public void setModelYear(int modelYear) {
        this.modelYear = modelYear;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEngineSize() {
        return engineSize;
    }

    public void setEngineSize(String engineSize) {
        this.engineSize = engineSize;
    }

    public int getHorsePower() {
        return horsePower;
    }

    public void setHorsePower(int horsePower) {
        this.horsePower = horsePower;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public int getBatteryRange() {
        return batteryRange;
    }

    public void setBatteryRange(int batteryRange) {
        this.batteryRange = batteryRange;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public void setFuelConsumption(double fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    public TransmissionType getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(TransmissionType transmissionType) {
        this.transmissionType = transmissionType;
    }


}