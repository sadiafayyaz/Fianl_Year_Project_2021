package com.example.androidcarmanager.Database;

public class Expence_DB {
    String expenseTitle;
    Long date, time;
    Double odometer, price, liter;
    String images;

    public Expence_DB() {
    }
//odometer
    public Expence_DB(Long date, Long time, Double odometer) {
        this.date = date;
        this.time = time;
        this.odometer = odometer;
    }
//other
    public Expence_DB(String expenseTitle, Long date, Long time, Double odometer, Double price) {
        this.expenseTitle = expenseTitle;
        this.date = date;
        this.time = time;
        this.odometer = odometer;
        this.price = price;
    }
//fuel
    public Expence_DB(String expenseTitle, Long date, Long time, Double odometer, Double price, Double liter) {
        this.expenseTitle = expenseTitle;
        this.date = date;
        this.time = time;
        this.odometer = odometer;
        this.price = price;
        this.liter = liter;
    }
    //images

    public Expence_DB(String expenseTitle, Long date, Long time, Double odometer, Double price, Double liter, String images) {
        this.expenseTitle = expenseTitle;
        this.date = date;
        this.time = time;
        this.odometer = odometer;
        this.price = price;
        this.liter = liter;
        this.images = images;
    }

    public String getExpenseTitle() {
        return expenseTitle;
    }

    public void setExpenseTitle(String expenseTitle) {
        this.expenseTitle = expenseTitle;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Double getOdometer() {
        return odometer;
    }

    public void setOdometer(Double odometer) {
        this.odometer = odometer;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getLiter() {
        return liter;
    }

    public void setLiter(Double liter) {
        this.liter = liter;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}

