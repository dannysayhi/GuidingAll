package org.wood.guidingall;

/**
 * Created by Danny on 2015/10/29.
 */
public class LostData {
    private String owner;
    private String itemName;
    private String location;
    private String PNGsrc;
    private String geoPoint;

    public final static String OWNER = "OWNER";
    public final static String ITEMNAME = "ITEMNAME";
    public final static String LOCATION = "LOCATION";
    public final static String PNGSRC = "PNGSRC";
    public final static String GEOPOINT = "GEOPOINT";

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPNGsrc() {
        return PNGsrc;
    }

    public void setPNGsrc(String PNGsrc) {
        this.PNGsrc = PNGsrc;
    }

    public String getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(String geoPoint) {
        this.geoPoint = geoPoint;
    }
}
