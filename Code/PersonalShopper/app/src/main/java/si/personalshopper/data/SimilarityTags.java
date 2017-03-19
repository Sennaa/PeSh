package si.personalshopper.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import si.personalshopper.database.DatabaseHandler;
import si.personalshopper.global.GlobalClass;

/**
 * Created by Senna on 23-4-2016.
 */
public class SimilarityTags {

    private ArrayList<String> tags;
    private double[][] allSimilarities;

    // Database
    private DatabaseHandler handler;

    public SimilarityTags(DatabaseHandler handler) {
        this.handler = handler;

        this.tags = new ArrayList<>();
        this.tags = getTags();

        this.allSimilarities = new double[tags.size()][tags.size()];
        this.allSimilarities = getAllSimilarities();
    }

    // TODO: IMPLEMENT (not yet necessary)
    public void updateSimilaritiesWithTag(Tag tag) {

    }

    public double[] getSimilarities(String tag) {
        int index = tags.indexOf(tag);
        return allSimilarities[index];
    }

    // getSimilarities
    public double[][] getAllSimilarities() {
        if (allSimilarities == null) {
            ArrayList<Tag> tempSimilarities = (ArrayList<Tag>) handler.getTagTable().getAll();
            for (int i = 0 ; i < allSimilarities.length ; i++) {
                allSimilarities[i] = tempSimilarities.get(i).getSimilarities();
            }
        }
        return allSimilarities;
    }

    // getTags
    public ArrayList<String> getTags() {
        if (tags.isEmpty()) {
            ArrayList<Tag> tempTag = (ArrayList<Tag>) handler.getTagTable().getAll();
            for (Tag tag : tempTag) {
                tags.add(tag.getTag());
            }
        }
        return tags;
    }

    /* clusterTags
        Use k-means to cluster the tags
        Input: k - the amount of clusters
        Output: ArrayList of k clusters, a cluster being an ArrayList of Strings
     */
    public ArrayList<ArrayList<String>> clusterTags(int k) {
        /* k-means consists of 2 steps
            1. Assignment step - assign tags to closest cluster
            2. Update step - calculate new means
         */
        // First, initialize k means (Forgy)
        double[][] kmeans = new double[k][tags.size()];

        kmeans = intializeKMeans(k, kmeans);

        // Initialize clusters
        ArrayList<ArrayList<String>> oldClusters;
        ArrayList<ArrayList<String>> newClusters = new ArrayList<>();
        int[] clusters = new int[tags.size()];
        // Do at least once
        do {
            oldClusters = new ArrayList<>(newClusters);

            // Assignment step //
            // Assign the clusters
            clusters = assignClusters(clusters, k, kmeans);
            // Add clusters to newClusters
            newClusters = addToClusters(newClusters, k, clusters);

            // Update step //
            kmeans = updateMeans(kmeans, k, newClusters);
        }
        while (!oldClusters.equals(newClusters));

        return newClusters;
    }

    // TODO: check if this can be more efficient
    private double[][] updateMeans(double[][] kmeans, int k, ArrayList<ArrayList<String>> clusters) {
        // Look at each cluster what the new means will become
        for (int cluster = 0 ; cluster < k ; cluster++) {
            // Initialization of new means for cluster 'cluster'
            double[] newmeans = new double[tags.size()];
            // Look at each tag in the cluster
            for (String tag : clusters.get(cluster)) {
                // Get index of the tag to retrieve the similarities of the tag
                int index = tags.indexOf(tag);
                double[] similaritiesTag = allSimilarities[index];
                // Look at each of the values of the similarities separately to add them to the new means
                for (int simValues = 0; simValues < tags.size() ; simValues++) {
                    // Add value / size(ArrayList<String> cluster) --> to get the mean
                    newmeans[simValues] += similaritiesTag[simValues] / (double) clusters.get(cluster).size();
                }
            }
            kmeans[cluster] = newmeans;
        }
        return kmeans;
    }

    private int[] assignClusters(int[] clusters, int k, double[][] kmeans) {
        for (int tag = 0 ; tag < tags.size() ; tag++) {
            int centroid = -1;
            double lowest = 100000.0;
            // ..and go through the kmeans centroids..
            for (int c = 0; c < k; c++) {
                // ..to see to which centroid the tag belongs (using the euclidean distance)
                double eucdist = eucDistance(kmeans[c],allSimilarities[tag]);
                if (eucdist < lowest) {
                    // New lowest value
                    lowest = eucdist;
                    // New centroid
                    centroid = c;
                }
            }
            clusters[tag] = centroid;
        }
        return clusters;
    }

    private ArrayList<ArrayList<String>> addToClusters(ArrayList<ArrayList<String>> newClusters, int k, int[] clusters) {
        for (int c = 0 ; c < k ; c++) {
            // Create new ArrayList for each cluster
            ArrayList<String> cluster = new ArrayList<>();
            // Go through list of cluster assignments
            for (int tag = 0 ; tag < clusters.length ; tag++) {
                if (clusters[tag] == c) {
                    cluster.add(tags.get(tag));
                }
            }
            // Add the ArrayList<String> as one cluster to the newClusters
            newClusters.add(cluster);
        }
        return newClusters;
    }

    private double eucDistance(double[] centroid, double[] tagSimilarity) {
        double eucdist = 0.0;
        for (int c = 0 ; c < centroid.length ; c++) {
            eucdist += Math.sqrt((centroid[c] - tagSimilarity[c]) * (centroid[c] - tagSimilarity[c]));
        }
        return eucdist;
    }

    private double[][] intializeKMeans(int k, double[][] kmeans) {
        boolean added = false;
        int[] randomNumbers = new int[k];
        // find k random instances
        for (int i = 0 ; i < k ; i++) {
            do {
                // Pick random number
                Random rand = new Random();
                int randomNumber = rand.nextInt(tags.size());
                // if the random number has not been chosen before
                if (Arrays.asList(randomNumbers).contains(randomNumber)) {
                    // add the instance to kmeans
                    kmeans[i] = allSimilarities[i];
                    // add the number to randomNumbers
                    randomNumbers[i] = randomNumber;
                    // set added = true
                    added = true;
                }
            }
            // If the number was chosen before, try again
            while (!added);
            added = false;
        }
        return kmeans;
    }
}
