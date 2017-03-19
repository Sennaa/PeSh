/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.data;

import java.util.ArrayList;

public class Shop {
    private String name;
    private String address;
    private ArrayList<String> tags;
    private int time;
    private double[] similarities;
    private double lat;
    private double lng;
    private int[] distances;

    public Shop(String name, String address, ArrayList<String> tags, int time, double[] similarities, double lat, double lng, int[] distances) {
        this.name = name;
        this.address = address;
        this.tags = tags;
        this.time = time;
        this.similarities = similarities;
        this.lat = lat;
        this.lng = lng;
        this.distances = distances;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Shop)) {
            return false;
        }
        Shop shop = (Shop)obj;
        return this.name.equals(shop.getName()) && this.address.equals(shop.getAddress());
    }

    public double getLat() {
        return this.lat;
    }

    public double getLng() {
        return this.lng;
    }

    public int getTime() {
        return this.time;
    }

    public int[] getDistances() {
        return this.distances;
    }

    public String getDistancesString() {
        String distancesString = "";
        for (int distance : this.distances) {
            distancesString = distancesString + distance + "; ";
        }
        return distancesString;
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    public ArrayList<String> getTags() {
        return this.tags;
    }

    public String getTagsString() {
        String tagsString = "";
        for (String tag : this.tags) {
            tagsString = tagsString + tag + ";";
        }
        return tagsString;
    }

    public double[] getSimilarities() {
        return this.similarities;
    }

    public String getSimilaritiesString() {
        String similaritiesString = "";
        for (double sim : this.similarities) {
            similaritiesString = similaritiesString + sim + "; ";
        }
        return similaritiesString;
    }

    public void setSimilarities(double[] similarities) {
        this.similarities = similarities;
    }
}

