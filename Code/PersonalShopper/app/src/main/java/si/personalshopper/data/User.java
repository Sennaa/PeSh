package si.personalshopper.data;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Senna on 13-4-2016.
 */
public class User {

    private String sID;
    private ArrayList<Shop> allShops;
    private Persona persona;
    private int timeBudget = 0;
    private Visits visitedList;
    private Ratings ratingList;
    private ArrayList<String> posTags;
    private ArrayList<String> negTags;
    private double[] usersimilarities;

    private ArrayList<Shop> recommendedShops;

    private ArrayList<String> extraSelectedTags;

    /* User
        Data from current user
        persona - the persona that is assigned to the user
        timeBudget - the amount of time a user has (minutes (?))
        visitedList - the list of shops visited by the user
        ratingList - the list of ratings of shops by the user
     */
    public User(String sID, ArrayList<Shop> allShops, int userSize) {
        // TODO: read in data
        this.sID = sID;
        this.allShops = allShops;
        persona = new Persona();
        timeBudget = 0;
        visitedList = new Visits(allShops);
        double[] ratings = new double[allShops.size()];
        ratingList = new Ratings(allShops, ratings);
        usersimilarities = new double[userSize];
        recommendedShops = new ArrayList<>();
        extraSelectedTags = new ArrayList<>();
    }

    public User() {
        // Empty user
        this.sID = "empty";
    }

    public boolean isEmpty() {
        return (sID.equals("empty"));
    }

    public double[] getUsersimilarities() { return usersimilarities; }

    public String getUsersimilaritiesString() {
        String similaritiesString = "";
        if (usersimilarities == null) {
            return similaritiesString;
        }
        for (double similarity : usersimilarities) {
            similaritiesString += (similarity + ";");
        }
        return similaritiesString;
    }

    public String getID() { return sID; }

    public Visits getVisitedList() {
        return visitedList;
    }

    public Ratings getRatingList() { return ratingList; }

    public int getTimeBudget() {
        return timeBudget;
    }

    public void setTimeBudget(int timeBudget) { this.timeBudget = timeBudget; }

    public Persona getPersona() {
        // return persona if not empty
        if (persona!=null)
            return persona;
        return new Persona();
    }

    public void setSelectedTags(ArrayList<String> tags) { this.extraSelectedTags = tags; }

    public ArrayList<String> getExtraSelectedTags() {
        if(extraSelectedTags == null) {
            return new ArrayList<>();
        }
        else {
            return extraSelectedTags;
        }
    }

    public String getExtraSelectedTagsString(ArrayList<Tag> allTags) {
        String extraTagsString = "";
        if (extraSelectedTags.size() == 0) {
            return extraTagsString;
        }
        else {
            for (Tag tag : allTags) {
                if (extraSelectedTags.contains(tag.getTag())) {
                    extraTagsString += "1;";
                }
                else {
                    extraTagsString += "0;";
                }
            }
            return extraTagsString;
        }
    }

    // TODO: hier gaat het fout, String wordt hier Tag, allTags is in de vorm van Tag ipv String
    public void setSelectedTagsFromString(String selectedTags, ArrayList<String> allTags) {
        String[] parts = selectedTags.split(";");
        ArrayList<String> selectedTagsList = new ArrayList<>();
        if (selectedTags.equals("") || selectedTags.equals(" ")) {
            // do nothing
        }
        else {
            for (int index = 0; index < parts.length; index++) {
                int value = Integer.parseInt(parts[index]);
                if (value == 1) {
                    selectedTagsList.add(allTags.get(index));
                } else if (value == 0) {
                    // Don't add anything
                } else {
                    // SHOULD NOT COME HERE
                }
            }
        }
        extraSelectedTags = selectedTagsList;
    }

    public void setPersona(Persona persona) { this.persona = persona; }

    public ArrayList<String> getPosTags() { return posTags; }

    public void setPosTags(String tags, ArrayList<String> allTags) {
        Log.d("User.Tags", tags);
        String[] parts = tags.split(";");
        ArrayList<String> posTagList = new ArrayList<>();
        if (tags.equals("") || tags.equals(" ")) {
            // do nothing
        }
        else {
            for (int index = 0; index < parts.length; index++) {
                // Remove the spaces if they're there and check if there's an int (no empty string)
                if ((parts[index]).replaceAll("\\s+","").equals("")) {
                    // do nothing
                }
                else {
                    int value = Integer.parseInt((parts[index]).replaceAll("\\s+", ""));
                    if (value == 1) {
                        posTagList.add(allTags.get(index));
                    } else if (value == 0) {
                        // Don't add anything
                    } else {
                        // SHOULD NOT COME HERE
                    }
                }
            }
        }
        posTags = posTagList;
    }

    public void setRecommendedShops(ArrayList<Shop> shops) {
        this.recommendedShops = shops;
    }

    public ArrayList<Shop> getRecommendedShops() {
        if (recommendedShops != null) {
            return recommendedShops;
        }
        else
            return new ArrayList<>();
    }

    public void setNegTags(String tags, ArrayList<String> allTags) {
        String[] parts = tags.split(";");
        ArrayList<String> negTagList = new ArrayList<>();
        if (tags.equals("") || tags.equals(" ")) {
            // do nothing
        }
        else {
            for (int index = 0; index < parts.length; index++) {
                // Remove the spaces if they're there and check if there's an int (no empty string)
                if ((parts[index]).replaceAll("\\s+","").equals("")) {
                    // do nothing
                }
                else {
                    int value = Integer.parseInt((parts[index]).replaceAll("\\s+", ""));
                    if (value == 1) {
                        negTagList.add(allTags.get(index));
                    } else if (value == 0) {
                        // Don't add anything
                    } else {
                        // SHOULD NOT COME HERE
                    }
                }
            }
        }
        negTags = negTagList;
    }

    public void setVisitedList(String visits) {
        visitedList.setVisited(visits);
    }

    public void setRatingList(String ratings) {
        ratingList.setRatings(ratings);
    }

    public void setUsersimilarities(String similarities) {
        String[] similaritiesSplit = similarities.split(";");
        if (similarities.equals("") || similarities.equals(" ")) {
            for (int index = 0 ; index < usersimilarities.length ; index++) {
                usersimilarities[index] = 0.0;
            }
        }
        else {
            for (int index = 0; index < usersimilarities.length; index++) {
                usersimilarities[index] = Double.parseDouble(similaritiesSplit[index]);
            }
        }
    }

    public void setPosTags(ArrayList<String> posTags) {
        this.posTags = posTags;
    }

    public void setNegTags(ArrayList<String> negTags) {
        this.negTags = negTags;
    }

    public String getPosTagsString(ArrayList<Tag> allTags) {
        String posTagsString = "";
        if (posTags == null) {
            return posTagsString;
        }
        else if (posTags.size() == 0) {
            return posTagsString;
        }
        else {
            for (Tag tag : allTags) {
                if (posTags.contains(tag.getTag())) {
                    posTagsString += "1;";
                }
                else {
                    posTagsString += "0;";
                }
            }
            return posTagsString;
        }
    }

    public ArrayList<String> getNegTags() { return negTags; }

    public String getNegTagsString(ArrayList<Tag> allTags) {
        String negTagsString = "";
        if (posTags == null) {
            return negTagsString;
        }
        else if (posTags.size() == 0) {
            return negTagsString;
        }
        else {
            for (Tag tag : allTags) {
                if (negTags.contains(tag.getTag())) {
                    negTagsString += "1;";
                }
                else {
                    negTagsString += "0;";
                }
            }
            return negTagsString;
        }
    }
}