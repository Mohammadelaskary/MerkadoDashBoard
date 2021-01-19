package com.example.elmohammadymarket.Model;

public class City {
    private String governorate;
    private String city;

    public City() {
    }

    public City(String governorate, String city) {
        this.governorate = governorate;
        this.city = city;
    }

    public String getGovernorate() {
        return governorate;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
