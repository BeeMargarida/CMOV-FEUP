package feup.cmov.mobile.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Purchase {

    private UUID uuid;
    private ArrayList<Product> products;
    private Date date;
    private float totalPrice;
    private float paidPrice;

    public Purchase(UUID uuid, ArrayList<Product> products, Date date, float totalPrice, float paidPrice) {
        this.uuid = uuid;
        this.products = products;
        this.date = date;
        this.totalPrice = totalPrice;
        this.paidPrice = paidPrice;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public Date getDate() {
        return date;
    }

    public String getDateString() {
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

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
