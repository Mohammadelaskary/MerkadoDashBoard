package com.example.elmohammadymarket.Model;


public class OrderProduct {
    private int id;
    private String ImageURL;
    private String productName;
    private String ordered;
    private String originalPrice;
    private String discount;
    private String discountType;
    private String finalPrice;
    private String totalCost;
    private String unitWeight;
    private String available;
    private String minimumOrderAmount;

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

    public String getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(String minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public OrderProduct(String imageURL, String productName, String ordered, String originalPrice, String discount, String discountType, String unitWeight, String minimumOrderAmount) {
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

    public String getOrdered() {
        return ordered;
    }

    public void setOrdered(String ordered) {
        this.ordered = ordered;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }
}
