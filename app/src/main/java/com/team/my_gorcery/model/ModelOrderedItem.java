package com.team.my_gorcery.model;

public class ModelOrderedItem {
    private String pId, name, cost, price, quantity, profileImage;

    public ModelOrderedItem() {

    }


    public ModelOrderedItem(String pId, String name, String cost, String price, String quantity, String profileImage) {
        this.pId = pId;
        this.name = name;
        this.cost = cost;
        this.price = price;
        this.quantity = quantity;
        this.profileImage = profileImage;

    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
