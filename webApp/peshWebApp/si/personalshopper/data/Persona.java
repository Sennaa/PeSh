/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.data;

import java.util.ArrayList;
import si.personalshopper.data.Ratings;
import si.personalshopper.data.Shop;
import si.personalshopper.data.Visits;

public class Persona {
    private int ID;
    private String name;
    private ArrayList<String> posTags;
    private ArrayList<String> negTags;
    private Visits visitedList;
    private Ratings ratinglist;
    private double weight;
    private String description;

    public Persona(int ID, String name, ArrayList<String> posTags, ArrayList<String> negTags, Visits visitedList, Ratings ratingList, double weight, String description) {
        this.ID = ID;
        this.name = name;
        this.posTags = posTags;
        this.negTags = negTags;
        this.visitedList = visitedList;
        this.ratinglist = ratingList;
        this.weight = weight;
        this.description = description;
    }

    public Persona() {
        this.name = "empty";
    }

    public int getID() {
        if (this.isEmpty()) {
            return -1;
        }
        return this.ID;
    }

    public boolean isEmpty() {
        if (this.name.equals("empty")) {
            return true;
        }
        return false;
    }

    public String getDescription() {
        return this.description;
    }

    public double getWeight() {
        return this.weight;
    }

    public ArrayList<String> getPosTags() {
        return this.posTags;
    }

    public ArrayList<String> getNegTags() {
        return this.negTags;
    }

    public String getName() {
        return this.name;
    }

    public Visits getVisitedList() {
        return this.visitedList;
    }

    public Ratings getRatinglist() {
        return this.ratinglist;
    }

    public String getPosTagsString() {
        String posTagsString = "";
        for (String tag : this.posTags) {
            posTagsString = posTagsString + tag + "; ";
        }
        return posTagsString;
    }

    public String getNegTagsString() {
        String negTagsString = "";
        for (String tag : this.negTags) {
            negTagsString = negTagsString + tag + "; ";
        }
        return negTagsString;
    }

    public Shop getHighestRatedShop() {
        return this.ratinglist.getHighestRatedShop(null);
    }
}

