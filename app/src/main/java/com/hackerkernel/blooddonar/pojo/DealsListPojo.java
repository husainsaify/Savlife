package com.hackerkernel.blooddonar.pojo;

/**
 * Created by Murtaza on 5/27/2016.
 */
public class DealsListPojo {
    private String imageUrl, hospitalName, description, deal, dealsId;

    public String getDealsId() {
        return dealsId;
    }

    public void setDealsId(String dealsId) {
        this.dealsId = dealsId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeal() {
        return deal;
    }

    public void setDeal(String deal) {
        this.deal = deal;
    }
}
