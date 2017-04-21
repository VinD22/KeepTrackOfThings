package model;

// Pojo class for prodcuts!

import io.realm.RealmObject;
import io.realm.annotations.Required;


/*
 * Pojo class for each product!
 * Product details - Product Name, Quantity, Product Category, Price, Additional Data
 * Low Product Warning number! For notification!
 */

public class Thing extends RealmObject {

    @Required
    private String name;
    private int id;
    private String where;
    private String addtionalData;

    private byte[] image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getAddtionalData() {
        return addtionalData;
    }

    public void setAddtionalData(String addtionalData) {
        this.addtionalData = addtionalData;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }


//    @Override
//    public int compareTo(Object p) {
//        int compareQuantity = ((Product)p).getQuantity();
//        /* For Ascending order*/
//        return this.quantity - compareQuantity;
//
//        /* For Descending order do like this */
//        //return compareage-this.studentage;
//    }

}