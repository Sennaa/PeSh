/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.data;

import java.util.ArrayList;
import si.personalshopper.data.Persona;
import si.personalshopper.data.Ratings;
import si.personalshopper.data.Shop;
import si.personalshopper.data.Tag;
import si.personalshopper.data.TagValue;
import si.personalshopper.data.Visits;

public class User {
    private ArrayList<Shop> allShops;
    private Persona persona;
    private int timeBudget;
    private Visits visitedList;
    private Ratings ratingList;
    private ArrayList<TagValue> tags;
    private String sessionID;

    public User(ArrayList<Shop> allShops, String sessionID) {
        this.sessionID = sessionID;
        this.allShops = allShops;
        this.persona = new Persona();
        this.timeBudget = 60;
        this.visitedList = new Visits(allShops);
        double[] ratings = new double[allShops.size()];
        this.ratingList = new Ratings(allShops, ratings);
    }

    public User() {
    }

    public String getSessionID() {
        return this.sessionID;
    }

    public Visits getVisitedList() {
        return this.visitedList;
    }

    public Ratings getRatingList() {
        return this.ratingList;
    }

    public int getTimeBudget() {
        return this.timeBudget;
    }

    public void setTimeBudget(int timeBudget) {
        this.timeBudget = timeBudget;
    }

    public Persona getPersona() {
        if (this.persona != null) {
            return this.persona;
        }
        return new Persona();
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public ArrayList<TagValue> getTags() {
        return this.tags;
    }

    public void setTags(String posTagsString, String negTagsString, ArrayList<String> allTags) {
        String[] posParts = posTagsString.split(";");
        String[] negParts = negTagsString.split(";");
        ArrayList<TagValue> tagList = new ArrayList<TagValue>();
        if (posTagsString.equals("") || posTagsString.equals(" ")) {
            if (!negTagsString.equals("") && !negTagsString.equals(" ")) {
                for (int index = 0; index < negParts.length; ++index) {
                    String tag = negParts[index].replaceAll("\\s+", "");
                    if (tag.equals("")) continue;
                    double value = Double.parseDouble(tag);
                    tagList.add(new TagValue(allTags.get(index), -1.0 * value));
                }
            }
        } else if (!negTagsString.equals("") && !negTagsString.equals(" ")) {
            for (int index = 0; index < posParts.length; ++index) {
                double posValue;
                String posTag = posParts[index].replaceAll("\\s+", "");
                String negTag = negParts[index].replaceAll("\\s+", "");
                if (posTag.equals("")) {
                    if (negTag.equals("")) continue;
                    double negValue = Double.parseDouble(negTag);
                    tagList.add(new TagValue(allTags.get(index), -1.0 * negValue));
                    continue;
                }
                if (negTag.equals("")) {
                    posValue = Double.parseDouble(posTag);
                    tagList.add(new TagValue(allTags.get(index), posValue));
                    continue;
                }
                posValue = Double.parseDouble(posTag);
                double negValue = Double.parseDouble(negTag);
                double value = posValue - negValue;
                tagList.add(new TagValue(allTags.get(index), value));
            }
        } else {
            for (int index = 0; index < posParts.length; ++index) {
                String tag = posParts[index].replaceAll("\\s+", "");
                if (tag.equals("")) continue;
                double value = Double.parseDouble(tag);
                tagList.add(new TagValue(allTags.get(index), value));
            }
        }
        this.tags = tagList;
    }

    public void setVisitedList(String visits) {
        this.visitedList.setVisited(visits);
    }

    public void setRatingList(String ratings) {
        this.ratingList.setRatings(ratings);
    }

    public void setRatingList(Ratings ratingList) {
        this.ratingList = ratingList;
    }

    public String getTagsString(ArrayList<Tag> allTags) {
        String tagsString = "";
        if (this.tags == null) {
            return tagsString;
        }
        if (this.tags.size() == 0) {
            return tagsString;
        }
        for (int i = 0; i < this.tags.size(); ++i) {
            tagsString = tagsString + Double.toString(this.tags.get(i).getRating()) + ";";
        }
        return tagsString;
    }

    public String getNegTagsString() {
        String negTagsString = "";
        if (this.tags == null) {
            return negTagsString;
        }
        if (this.tags.size() == 0) {
            return negTagsString;
        }
        for (int i = 0; i < this.tags.size(); ++i) {
            double rating = this.tags.get(i).getRating();
            negTagsString = rating < 0.0 ? negTagsString + Double.toString(this.tags.get(i).getRating()) + ";" : negTagsString + "0.0;";
        }
        return negTagsString;
    }

    public double getPosRatio() {
        double nPos = 0.0;
        for (Shop shop : this.allShops) {
            double rating = this.ratingList.getRating(shop);
            if (rating <= 0.0) continue;
            nPos += rating;
        }
        return nPos / (double)this.allShops.size();
    }

    public String getPosTagsString() {
        String posTagsString = "";
        if (this.tags == null) {
            return posTagsString;
        }
        if (this.tags.size() == 0) {
            return posTagsString;
        }
        for (int i = 0; i < this.tags.size(); ++i) {
            double rating = this.tags.get(i).getRating();
            posTagsString = rating > 0.0 ? posTagsString + Double.toString(rating) + ";" : posTagsString + "0.0;";
        }
        return posTagsString;
    }
}

