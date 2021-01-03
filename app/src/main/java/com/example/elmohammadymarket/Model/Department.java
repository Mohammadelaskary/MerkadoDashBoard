package com.example.elmohammadymarket.Model;

import com.example.elmohammadymarket.Database.Product;

import java.util.List;

public class Department {
    private String depName;
    private List<Product> productList;


    public Department(String depName, List<Product> productList) {
        this.depName = depName;
        this.productList = productList;
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }
}
