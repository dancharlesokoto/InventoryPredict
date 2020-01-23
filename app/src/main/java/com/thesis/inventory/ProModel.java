package com.thesis.inventory;

public class ProModel {

    String id, title, description, barcode, quantity,price, status,timestamp,prono;

    public ProModel(){

    }

    public ProModel(String id, String title, String description, String barcode, String price , String quantity, String timestamp, String status,String prono) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.barcode = barcode;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = timestamp;
        this.status = status;
        this.prono = prono;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
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


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getProno() {
        return prono;
    }

    public void setProno(String prono) {
        this.prono = prono;
    }
}
