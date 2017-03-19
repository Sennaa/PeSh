/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import si.personalshopper.data.Shop;
import si.personalshopper.data.TagValue;

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
        if (this.allRatings == null) {
            return ratings;
        }
        for (double rating : this.allRatings) {
            ratings = ratings + rating + ";";
        }
        return ratings;
    }

    public double getRating(Shop shop) {
        int index = -1;
        for (Shop s : this.allShops) {
            if (!s.getName().equals(shop.getName())) continue;
            index = this.allShops.indexOf(s);
        }
        return this.allRatings[index];
    }

    public Ratings computeRatings(ArrayList<TagValue> tags) {
        double[] ratings = new double[this.allShops.size()];
        int index = 0;
        for (Shop shop : this.allShops) {
            ArrayList<String> shopTags = shop.getTags();
            while (shopTags == null) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            double tagRatings = 0.0;
            for (TagValue tag : tags) {
                if (!shopTags.contains(tag.getTag())) continue;
                tagRatings += tag.getRating();
            }
            int nTotal = shop.getTags().size();
            ratings[index] = tagRatings / (double)nTotal;
            ++index;
        }
        this.allRatings = ratings;
        return this;
    }

    public double calculateStDev(double average) {
        double sumDev = 0.0;
        for (Shop shop : this.allShops) {
            double rating = this.getRating(shop);
            sumDev += (rating - average) * (rating - average);
        }
        double variance = sumDev / (double)this.allShops.size();
        return Math.sqrt(variance);
    }

    public void setRatings(String ratings) {
        String[] ratingsSplit = ratings.split(";");
        if (ratings.equals("") || ratings.equals(" ")) {
            for (int index = 0; index < this.allRatings.length; ++index) {
                this.allRatings[index] = 0.0;
            }
        } else {
            for (int index = 0; index < this.allRatings.length; ++index) {
                this.allRatings[index] = Double.parseDouble(ratingsSplit[index]);
            }
        }
    }

    public double getSummedRatings(ArrayList<Shop> shops) {
        double ratings = 0.0;
        for (Shop shop : shops) {
            ratings += this.getRating(shop);
        }
        return ratings;
    }

    public void adaptRating(Shop shop, double rating) {
        int index = this.allShops.indexOf(shop);
        this.allRatings[index] = rating;
    }

    public void add(Ratings ratings) {
        for (int shop = 0; shop < this.allShops.size(); ++shop) {
            double[] arrd = this.allRatings;
            int n = shop;
            arrd[n] = arrd[n] + ratings.getRating(this.allShops.get(shop));
        }
    }

    public Shop getHighestRatedShop(ArrayList<Shop> chosenPivots) {
        int[] pivotsIndices = new int[chosenPivots.size()];
        for (int pivot = 0; pivot < chosenPivots.size(); ++pivot) {
            pivotsIndices[pivot] = this.allShops.indexOf(chosenPivots.get(pivot));
        }
        double[] newRatingList = new double[this.allRatings.length];
        Arrays.fill(newRatingList, -1.0);
        for (int newRating = 0; newRating < this.allRatings.length; ++newRating) {
            boolean isPivot = false;
            for (int pivot2 = 0; pivot2 < pivotsIndices.length; ++pivot2) {
                if (newRating != pivotsIndices[pivot2]) continue;
                isPivot = true;
            }
            newRatingList[newRating] = !isPivot ? this.allRatings[newRating] : -10.0;
        }
        int maxIndex = 0;
        ArrayList<Integer> maxIndices = new ArrayList<Integer>();
        for (int i = 0; i < newRatingList.length; ++i) {
            if (newRatingList[i] > newRatingList[maxIndex]) {
                maxIndices = new ArrayList();
                maxIndices.add(i);
                maxIndex = i;
                continue;
            }
            if (newRatingList[i] != newRatingList[maxIndex]) continue;
            maxIndices.add(i);
        }
        Collections.shuffle(maxIndices);
        maxIndex = (Integer)maxIndices.get(0);
        return this.allShops.get(maxIndex);
    }

    public ArrayList<Shop> getTopNShops(int n) {
        ArrayList<Shop> topNShops = new ArrayList<Shop>();
        for (int shop = 0; shop < n; ++shop) {
            topNShops.add(this.getHighestRatedShop(topNShops));
        }
        return topNShops;
    }
}

