package com.team.my_gorcery.model;

public class ModelCartItem {

    String id, pId, price, cost, quantity, name;

    public ModelCartItem() {

    }

    public ModelCartItem(String id, String pId, String price, String cost, String quantity, String name) {
        this.id = id;
        this.pId = pId;
        this.price = price;
        this.cost = cost;
        this.quantity = quantity;
        this.name = name;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
