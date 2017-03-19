package si.personalshopper.data;

import java.util.ArrayList;

/**
 * Created by Senna on 14-4-2016.
 */
public class Persona {

    private int ID;
    private String name;
    private ArrayList<String> posTags;
    private ArrayList<String> negTags;
    private Visits visitedList;
    private Ratings ratinglist;
    private double weight;
    private String description;

    /* Persona
        name: name of the persona.
        posTags: list of positive tags belonging to the persona
        negTags: list of negative tags belonging to the persona
        visitedList: list of shops "visited" by the persona
        ratingList: list of "ratings" of shops by the persona
        weight: how much is the user assigned to the persona. Weights add up to 1.
     */
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
        name = "empty";
    }

    public int getID() {
        if (isEmpty()) {
            return -1;
        }
        return ID;
    }

    public boolean isEmpty() {
        if (name.equals("empty")) {
            return true;
        }
        return false;
    }

    public String getDescription() { return description; }

    public double getWeight() {
        return weight;
    }

    public ArrayList<String> getPosTags() { return posTags; }

    public ArrayList<String> getNegTags() { return negTags; }

    public String getName() { return name; }

    public Visits getVisitedList() { return visitedList; }

    public Ratings getRatinglist() { return ratinglist; }


    public String getPosTagsString() {
        String posTagsString = "";
        for (String tag : posTags) {
            posTagsString += (tag + "; ");
        }
        return posTagsString;
    }

    public String getNegTagsString() {
        String negTagsString = "";
        for (String tag : negTags) {
            negTagsString += (tag + "; ");
        }
        return negTagsString;
    }

    public Shop getHighestRatedShop() {
        return ratinglist.getHighestRatedShop(null);
    }

}
