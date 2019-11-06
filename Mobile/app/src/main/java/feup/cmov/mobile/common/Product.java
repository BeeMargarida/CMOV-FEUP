package feup.cmov.mobile.common;

import java.io.Serializable;
import java.util.UUID;

public class Product implements Serializable {

    private UUID uuid;
    private String name;
    private float price;

    public Product(UUID uuid, String name, float price) {
        this.uuid = uuid;
        this.name = name;
        this.price = price;
    }

    public Product(String prod) {
        String[] tmp = prod.split(":");
        this.uuid = UUID.fromString(tmp[0]);
        this.name = tmp[1];
        this.price = Float.parseFloat(tmp[2]);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return this.uuid + ":" + this.name + ":" + this.price;
    }

}
