package com.example.elmohammadymarket.Model;


public class OrderProduct {
    private int id;
    private String ImageURL;
    private String productName;
    private float ordered;
    private String originalPrice;
    private String discount;
    private String discountType;
    private String finalPrice;
    private String totalCost;
    private String unitWeight;
    private float available;
    private float minimumOrderAmount;

    public OrderProduct() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUnitWeight() {
        return unitWeight;
    }

    public void setUnitWeight(String unitWeight) {
        this.unitWeight = unitWeight;
    }

    public String getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(String finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public float getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(float minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public OrderProduct(String imageURL, String productName, float ordered, String originalPrice, String discount, String discountType, String unitWeight, float minimumOrderAmount) {
        this.ImageURL = imageURL;
        this.productName = productName;
        this.ordered = ordered;
        this.originalPrice = originalPrice;
        this.discount = discount;
        this.discountType = discountType;
        this.unitWeight = unitWeight;
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public String getTotalCost() {

        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getOrdered() {
        return ordered;
    }

    public void setOrdered(float ordered) {
        this.ordered = ordered;
    }

    public float getAvailable() {
        return available;
    }

    public void setAvailable(float available) {
        this.available = available;
    }
}
