package feup.cmov.mobile.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Purchase implements Serializable {

    private UUID uuid;
    private String date;
    private ArrayList<Product> products;
    private float totalPrice;
    private float paidPrice;

    public Purchase(UUID uuid, String date, ArrayList<Product> products, float totalPrice, float paidPrice) {
        this.uuid = uuid;
        this.date = date;
        this.products = products;
        this.totalPrice = totalPrice;
        this.paidPrice = paidPrice;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getDate() { return date; }

    public void setDate(String date) {this.date=date;}

    public  ArrayList<Product> getProducts() {return products;}

    public void setProducts(ArrayList<Product> products){this.products = products;}

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public float getPaidPrice() {
        return paidPrice;
    }

    public void setPaidPrice(float paidPrice) {
        this.paidPrice = paidPrice;
    }

}
