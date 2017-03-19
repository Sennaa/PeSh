package si.personalshopper.data;

import java.util.ArrayList;

/**
 * Created by Senna on 13-4-2016.
 */
public class Shop {

    private String name;
    private String address;
    private ArrayList<String> tags;
    private int time;
    private double[] similarities;

    public Shop(String name, String address, ArrayList<String> tags, int time, double[] similarities) {
        this.name = name;
        this.address = address;
        this.tags = tags;
        this.time = time;
        this.similarities = similarities;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Shop)) {
            return false;
        }
        Shop shop = (Shop) obj;
        return this.name.equals(shop.getName()) &&
                this.address.equals(shop.getAddress());
    }

    /* getTime
        returns estimated time that a user spends in the shop
     */
    public int getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public ArrayList<String> getTags() { return tags; }

    public String getTagsString() {
        String tagsString = "";
        for (String tag : tags) {
            tagsString += (tag + ";");
        }
        return tagsString;
    }

    public double[] getSimilarities() { return similarities; }

    public String getSimilaritiesString() {
        String similaritiesString = "";
        for (double sim : similarities ) {
            similaritiesString += (sim + "; ");
        }
        return similaritiesString;
    }

    public void setSimilarities(double[] similarities) { this.similarities = similarities; }

}
