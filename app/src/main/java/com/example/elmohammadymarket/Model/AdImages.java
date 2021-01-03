package com.example.elmohammadymarket.Model;

public class AdImages {
    private String adImageUrl;
    private String imageFileName;
    private String adText;


    public AdImages() {
    }

    public AdImages(String adImageUrl, String imageFileName, String adText) {
        this.adImageUrl = adImageUrl;
        this.imageFileName = imageFileName;
        this.adText = adText;
    }

    public String getAdText() {
        return adText;
    }

    public void setAdText(String adText) {
        this.adText = adText;
    }

    public String getAdImageUrl() {
        return adImageUrl;
    }

    public void setAdImageUrl(String adImageUrl) {
        this.adImageUrl = adImageUrl;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }
}
