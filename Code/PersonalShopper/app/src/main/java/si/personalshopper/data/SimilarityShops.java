package si.personalshopper.data;

import java.util.ArrayList;

import si.personalshopper.global.GlobalClass;

/**
 * Created by Senna on 13-4-2016.
 */
public class SimilarityShops {

    private double[][] similarities;
    private ArrayList<Shop> allShops;

    /* SimilarityShops
        similarities - contains the similarity values between two shops.
        allShops - list of all shops.
     */
    public SimilarityShops(ArrayList<Shop> allShops,double[][] shopSimilarities) {
        this.allShops = allShops;
        this.similarities = shopSimilarities;
    }

    /* getSimValue
        Get Similarity value for Shop first and Shop second.
     */
    public double getSimValue(Shop first, Shop second) {
        int indexFirst  = allShops.indexOf(first);
        int indexSecond = allShops.indexOf(second);

        return similarities[indexFirst][indexSecond];
    }

    /* getSumSimValue
        Returns the sum of similarity values in a list of shops (∑u,v∈S s(u,v))
        Input: ArrayList<Shop>
        Output: double
     */
    public double getSumSimValue(ArrayList<Shop> S) {
        double sumSimValue = 0.0;
        // For each shop u..
        for (Shop u: S) {
            // ..and each shop v.
            for (Shop v: S) {
                // Don't use similarity value when shop u == shop v
                if (!u.equals(v)){
                    // When u!=v, add similarity value to sumSimValue
                    sumSimValue += getSimValue(u,v);
                }
            }
        }
        return sumSimValue;
    }

    /* getMostSimilarShop
        Return most similar shop of Shop s.
     */
    public Shop getMostSimilarShop(Shop s) {
        // Index of Shop s
        int index = allShops.indexOf(s);
        // Similarity values of Shop s
        double[] simValues = similarities[index];
        // Initialize highest similarity value and index of the shop belonging to that value
        double highestSimVal = 0.0;
        int indexSimVal = 0;
        // Look in the array for the index of the max value
        for (int i = 0 ; i < simValues.length ; i++) {
            // Do not look at the shop itself (because its similarity is 1 and thus the highest)
            if (i!=index) {
                if (simValues[i] > highestSimVal) {
                    highestSimVal = simValues[i];
                    indexSimVal = i;
                }
            }
        }
        // Return shop with highest similarity value
        return allShops.get(indexSimVal);
    }

    public Shop getMostSimilarShopFromList(Shop s, ArrayList<Shop> list) {
        Shop mostSimilar = null;
        double highestVal = -10.0;
        for (Shop from : list) {
            double simVal = getSimValue(s, from);
            if (simVal > highestVal) {
                mostSimilar = from;
                highestVal = simVal;
            }
        }
        return mostSimilar;
    }

    /* getMaxSimValueShopPair
        Input: Two Arraylists of shop
        Output: the maximum similarity value of any pair of the two arraylists
     */
    public double getMaxSimValueShopPair(ArrayList<Shop> firstList, ArrayList<Shop> secondList) {
        double simValue = 0.0;

        for (Shop first : firstList) {
            for (Shop second : secondList) {
                double newValue = getSimValue(first, second);
                if (newValue > simValue) {
                    simValue = newValue;
                }
            }
        }

        return simValue;
    }

    //TODO: check if it can be more efficient
    public ArrayList<ArrayList<Shop>> clusterShops(ArrayList<Shop> shops, SimilarityTags similarityTags, int k) {
        // First, cluster tags //
        ArrayList<ArrayList<String>> tagClusters = similarityTags.clusterTags(k);

        ArrayList<ArrayList<Integer>> newClusters = new ArrayList<>();
        for (int i = 0 ; i < tagClusters.size() ; i++) {
            newClusters.add(new ArrayList<Integer>());
        }

        // Then, assign a shop to the cluster in which most tags belonging to that shop occur //
        // Calculate for each shop how many tags of the shop occur in the cluster of tags
        for (int shop = 0 ; shop < shops.size() ; shop++) {
            int highest = -1;
            int currentCluster = -1;
            // For all clusters in tagClusters
            for (int cluster = 0 ; cluster < tagClusters.size() ; cluster++) {
                int amountTags = 0;
                // Go through tags of shop and tags of tagClusters.get(cluster)
                for (String c : tagClusters.get(cluster)) {
                    for (String shopTag : shops.get(shop).getTags()) {
                        // If the tag is equal, add 1 to amountTags
                        if (c.equals(shopTag)) {
                            amountTags += 1;
                        }
                    }
                }
                // If the amount of tags is higher, assign the shop to the current cluster
                if (amountTags > highest) {
                    currentCluster = cluster;
                }
            }
            // Add number of shop (index in ArrayList<Shop> shops) to the belonging cluster
            newClusters.get(currentCluster).add(shop);
        }

        // Initialize shopClusters
        ArrayList<ArrayList<Shop>> shopClusters = null;

        // Make clusters of shops instead of Integers
        for (int cluster = 0 ; cluster < k ; cluster++) {
            ArrayList<Integer> c = newClusters.get(cluster);
            ArrayList<Shop> shopCluster = new ArrayList<>();
            int size = c.size();
            for (Integer shop : c) {
                shopCluster.add(shops.get(shop));
            }
            shopClusters.add(shopCluster);
        }

        return shopClusters;
    }

}
