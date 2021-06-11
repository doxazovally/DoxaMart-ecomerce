package com.team.my_gorcery.model;

public class ModelProduct {
    private String productId, productName, productDescription, productCategory, productQuantity,
            productPrice, priceDiscount, discountNote, productImage, discountAvailability, timestamp, uid, deliveryDate, returnDate;

    public ModelProduct(){

    }

    public ModelProduct(String productId, String productName, String productDescription, String productCategory, String productQuantity,
                        String productPrice, String priceDiscount, String discountNote, String productImage,
                        String discountAvailability, String timestamp, String uid, String deliveryDate, String returnDate) {


        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productCategory = productCategory;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
        this.priceDiscount = priceDiscount;
        this.discountNote = discountNote;
        this.productImage = productImage;
        this.discountAvailability = discountAvailability;
        this.timestamp = timestamp;
        this.uid = uid;
        this.deliveryDate = deliveryDate;
        this.returnDate = returnDate;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {

        this.productId = productId;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {

        this.productName = productName;
    }


    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }


    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }


    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getPriceDiscount() {
        return priceDiscount;
    }

    public void setPriceDiscount(String priceDiscount) {

        this.priceDiscount = priceDiscount;
    }

    public String getDiscountNote() {
        return discountNote;
    }

    public void setDiscountNote(String discountNote) {

        this.discountNote = discountNote;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {

        this.productImage = productImage;
    }

    public String getDiscountAvailability() {
        return discountAvailability;
    }

    public void setDiscountAvailability(String discountAvailability) {
        this.discountAvailability = discountAvailability;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {

        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {

        this.uid = uid;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {

        this.deliveryDate = deliveryDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {

        this.returnDate = returnDate;
    }
}