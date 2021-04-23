package com.example.androidcarmanager.Database;

public class Vehical_info_DB {

        String vehicleName, odometerReading, manufacturer, vehicleModel;
        Double mileageRange, fuelLimit;
        String plateNumber;
        Long purchaseDate;

        public Vehical_info_DB() {
        }

        public Vehical_info_DB(String vehicleName, String odometerReading, String manufacturer, String vehicleModel,
                               Double mileageRange, Double fuelLimit, String plateNumber, Long purchaseDate) {
            this.vehicleName = vehicleName;
            this.odometerReading = odometerReading;
            this.manufacturer = manufacturer;
            this.vehicleModel = vehicleModel;
            this.mileageRange = mileageRange;
            this.fuelLimit = fuelLimit;
            this.plateNumber = plateNumber;
            this.purchaseDate = purchaseDate;
        }

        public String getModometerReading() {
            return odometerReading;
        }

        public void setodometerReading(String odometerReading) {
            this.odometerReading = odometerReading;
        }

        public Long getPurchaseDate() {
            return purchaseDate;
        }

        public void setPurchaseDate(Long purchaseDate) {
            this.purchaseDate = purchaseDate;
        }

        public String getVehicleName() {
            return vehicleName;
        }

        public void setVehicleName(String vehicleName) {
            this.vehicleName = vehicleName;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }

        public String getVehicleModel() {
            return vehicleModel;
        }

        public void setVehicleModel(String vehicleModel) {
            this.vehicleModel = vehicleModel;
        }

        public Double getMileageRange() {
            return mileageRange;
        }

        public void setMileageRange(Double mileageRange) {
            this.mileageRange = mileageRange;
        }

        public Double getFuelLimit() {
            return fuelLimit;
        }

        public void setFuelLimit(Double fuelLimit) {
            this.fuelLimit = fuelLimit;
        }

        public String getPlateNumber() {
            return plateNumber;
        }

        public void setPlateNumber(String plateNumber) {
            this.plateNumber = plateNumber;
        }

    }
