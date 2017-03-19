package si.personalshopper.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Senna on 14-4-2016.
 */
public class Ratings {

    private ArrayList<Shop> allShops;
    private double[] allRatings;

    public Ratings(ArrayList<Shop> shops) {
        this.allShops = shops;
        this.allRatings = new double[shops.size()];
    }

    public Ratings(ArrayList<Shop> shops, double[] ratings) {
        this.allShops = shops;
        this.allRatings = ratings;
    }

    public String getRatingsString() {
        String ratings = "";
        if (allRatings == null) {
            return ratings;
        }
        for (double rating : allRatings) {
            ratings += (rating + ";");
        }
        return ratings;
    }

    public double getRating(Shop shop) {
        int index = -1;
        for (Shop s : allShops) {
            if (s.getName().equals(shop.getName())) {
                index = allShops.indexOf(s);
            }
        }
        return allRatings[index];
    }

    /* Compute ratings from posTags and negTags
        Look per shop how many of the tags occur in it, divided by the total amount of tags for that shop
    */
    public Ratings computeRatings(ArrayList<String> posTags, ArrayList<String> negTags) {
        double[] ratings = new double[allShops.size()];
        // Initialize index of ratings
        int index = 0;
        // Look per shop...
        for (Shop shop : allShops) {
            // ...how many of the posTags occur in it
            int nPos = 0;
            for (String posTag : posTags) {
                if (shop.getTags().contains(posTag)) {
                    nPos += 1;
                }
            }
            // ...and how many of the negTags occur in it
            int nNeg = 0;
            for (String negTag : negTags) {
                if (shop.getTags().contains(negTag)) {
                    nNeg += 1;
                }
            }
            // Total amount of tags of Shop shop
            int nTotal = shop.getTags().size();

            // Set new Ratings of Shop shop (#posTags / #total) - (#negTags / #total)
            ratings[index] = ((double)nPos / (double)nTotal) - ((double)nNeg / (double)nTotal);
            index += 1;
        }
        allRatings = ratings;
        return this;
    }

    public void setRatings(String ratings) {
        String[] ratingsSplit = ratings.split(";");
        if (ratings.equals("") || ratings.equals(" ")) {
            for (int index = 0; index < allRatings.length; index++) {
                allRatings[index] = 0.0;
            }
        }
        else {
            for (int index = 0; index < allRatings.length; index++) {
                allRatings[index] = Double.parseDouble(ratingsSplit[index]);
            }
        }
    }

    public double getSummedRatings(ArrayList<Shop> shops) {
        double ratings = 0.0;
        for (Shop shop : shops) {
            ratings += getRating(shop);
        }
        return ratings;
    }

    public void adaptRating(Shop shop, double rating) {
        int index = allShops.indexOf(shop);
        allRatings[index] = rating;
    }

    public void add(Ratings ratings) {
        for (int shop = 0 ; shop < allShops.size() ; shop++) {
            // Add to the ratings the ratings of all shops of 'Ratings ratings' to the current ratings.
            allRatings[shop] += ratings.getRating(allShops.get(shop));
        }
    }

    /* getHighestRatedShop
        Used to get a pivot for the composite recommender
        Input: ArrayList<Shop> of already chosen pivots (that should not be taken into account)
     */
    public Shop getHighestRatedShop(ArrayList<Shop> chosenPivots) {
        // Make sure the already chosen pivots are not taken into account
        int[] pivotsIndices = new int[chosenPivots.size()];
        for (int pivot = 0 ; pivot < chosenPivots.size() ; pivot++) {
            // Save index of pivot (shop) in allShops
            pivotsIndices[pivot] = allShops.indexOf(chosenPivots.get(pivot));
        }

        // Make new list of ratings without already chosen pivots
        int index = 0;
        double[] newRatingList = new double[allRatings.length];
        Arrays.fill(newRatingList, -1);
        Log.d("newRatingListSize", Integer.toString(newRatingList.length));
        for (int newRating = 0 ; newRating < allRatings.length ; newRating++) {
            boolean isPivot = false;
            for (int pivot = 0 ; pivot < pivotsIndices.length ; pivot++) {
                // The shop should not be in the list of pivots
                if (newRating == pivotsIndices[pivot]) {
                    isPivot = true;
                }
            }
            // Only add the rating if the shop is not already a pivot
            if (!isPivot) {
                newRatingList[index] = allRatings[newRating];
            }
            index += 1;
        }

        int maxIndex = 0;
        // Find max (excluding already chosen pivots)
        for (int i = 1 ; i < newRatingList.length ; i++) {
            if (newRatingList[i] > newRatingList[maxIndex]) {
                maxIndex = i;
            }
        }

        // Return shop that is the highest rated
        return allShops.get(maxIndex);
    }

    public ArrayList<Shop> getTopNShops(int n) {
        ArrayList<Shop> topNShops = new ArrayList<>();
        // Add the highest rated shop n times, without taking into account the shops already in the list
        for (int shop = 0 ; shop < n ; shop++) {
            topNShops.add(getHighestRatedShop(topNShops));
        }
        return topNShops;
    }

}
