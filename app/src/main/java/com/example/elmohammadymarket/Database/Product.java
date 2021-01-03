package com.example.elmohammadymarket.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class Product {
    private String imageUrl;
    private String imageFileName;
    private String dep;
    private String subDep;
    @PrimaryKey
    @NonNull
    private String productName;
    private String price;
    private String unitWeight;
    private String discount;
    private String discountUnit;
    private float availableAmount;
    private String count;
    private boolean mostSold;
    private boolean todaysOffer;
    private float minimumOrderAmount;

    @Ignore
    public Product(String dep, String subDep, @NotNull String productName, String price, String unitWeight, String discount, String discountUnit, float availableAmount, boolean todaysOffer, boolean mostSold,float minimumOrderAmount) {
        this.dep = dep;
        this.subDep = subDep;
        this.productName = productName;
        this.price = price;
        this.unitWeight = unitWeight;
        this.discount = discount;
        this.discountUnit = discountUnit;
        this.availableAmount = availableAmount;
        this.todaysOffer = todaysOffer;
        this.mostSold = mostSold;
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public Product() {
        productName = null;
    }

    public float getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(float minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public void setAvailableAmount(float availableAmount) {
        this.availableAmount = availableAmount;
    }

    public float getAvailableAmount() {
        return availableAmount;
    }

    public boolean isMostSold() {
        return mostSold;
    }

    public void setMostSold(boolean mostSold) {
        this.mostSold = mostSold;
    }

    public boolean isTodaysOffer() {
        return todaysOffer;
    }

    public void setTodaysOffer(boolean todaysOffer) {
        this.todaysOffer = todaysOffer;
    }

    public String getSubDep() {
        return subDep;
    }

    public void setSubDep(String subDep) {
        this.subDep = subDep;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }


    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDep() {
        return dep;
    }

    public void setDep(String dep) {
        this.dep = dep;
    }

    @org.jetbrains.annotations.NotNull
    public String getProductName() {
        return productName;
    }

    public void setProductName(@org.jetbrains.annotations.NotNull String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnitWeight() {
        return unitWeight;
    }

    public void setUnitWeight(String unitWeight) {
        this.unitWeight = unitWeight;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDiscountUnit() {
        return discountUnit;
    }

    public void setDiscountUnit(String discountUnit) {
        this.discountUnit = discountUnit;
    }


}
