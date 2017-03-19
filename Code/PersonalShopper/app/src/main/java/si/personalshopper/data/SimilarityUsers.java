package si.personalshopper.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import si.personalshopper.database.DatabaseHandler;
import si.personalshopper.global.GlobalClass;

/**
 * Created by Senna on 14-4-2016.
 */
public class SimilarityUsers {

    private GlobalClass globalClass;
    private double[][] allSimilarities;

    public SimilarityUsers(GlobalClass globalClass, double[][] similarities) {
        this.globalClass = globalClass;
        /*
                   | User 1 | User 2 | User 3 | User 4 | etc.
            -------------------------------------------------
            User 1 |
            User 2 |
            User 3 |
            User 4 |
            etc.   |
         */
        allSimilarities = similarities;
    }

    public double[] getSimilarities(User user) {
        DatabaseHandler handler = globalClass.getHandler();
        ArrayList<User> users = (ArrayList<User>) handler.getUserTable().getAll();
        int index = 0;
        for (User u : users) {
            if(u.getID().equals(user.getID())) {
                continue;
            }
            else {
                index += 1;
            }
        }
        double[] similarities = allSimilarities[index];
        return similarities;
    }

    public String getSimilaritiesString(User user) {
        double[] similarities = getSimilarities(user);
        String similaritiesString = "";
        for (double similarity : similarities) {
            similaritiesString += (similarity + "; ");
        }
        return similaritiesString;
    }

    public int getN(int n) {
        DatabaseHandler handler = globalClass.getHandler();
        ArrayList<User> users = (ArrayList<User>) handler.getUserTable().getAll();
        if (users.size() < n) {
            n = users.size();
        }
        return n;
    }

    public ArrayList<User> getNClosestUsers(User user, int n) {
        ArrayList<User> topNUsers = new ArrayList<>();
        // Add the highest rated shop n times, without taking into account the shops already in the list
        for (int u = 0 ; u < n ; u++) {
            topNUsers.add(getClosestUser(user, topNUsers));
        }
        return topNUsers;
    }

    /* get closest user to current user
        nonUsers not taken into account
     */
    public User getClosestUser(User user, ArrayList<User> nonUsers) {
        DatabaseHandler handler = globalClass.getHandler();
        ArrayList<User> users = (ArrayList<User>) handler.getUserTable().getAll();
        int indexCurrentUser = 0;
        for (User u : users) {
            if(u.getID().equals(user.getID())) {
                continue;
            }
            else {
                indexCurrentUser += 1;
            }
        }
        // Make sure the nonUsers are not taken into account
        int[] userIndices = new int[nonUsers.size()];
        for (int nonUser = 0 ; nonUser < nonUsers.size() ; nonUser++) {
            int tempIndex = 0;
            for (User u : users) {
                if(u.getID().equals(nonUsers.get(nonUser).getID())) {
                    continue;
                }
                else {
                    tempIndex += 1;
                }
            }
            userIndices[nonUser] = tempIndex;
        }

        // Make new list of ratings without already chosen pivots
        int index = 0;
        double[] newUserList = new double[allSimilarities[indexCurrentUser].length - nonUsers.size()];
        for (int newUser = 0 ; newUser < allSimilarities[indexCurrentUser].length ; newUser++) {
            boolean isNonUser = false;
            for (int nonUser = 0 ; nonUser < userIndices.length ; nonUser++) {
                if (newUser == userIndices[nonUser]) {
                    isNonUser = true;
                }
            }
            if (!isNonUser) {
                newUserList[index] = allSimilarities[indexCurrentUser][newUser];
                index += 1;
            }
        }

        // Find max (excluding nonUsers)
        int maxIndex = -1;
        double max = -1.0;
        for (int i = 0 ; i < newUserList.length ; i++) {
            if (newUserList[i] > max) {
                maxIndex = i;
            }
        }
        // Return user that is the closest
        return users.get(maxIndex);
    }

    // getSimilarities
    public double[][] getAllSimilarities() {
        return allSimilarities;
    }

    // getUsers
    public ArrayList<User> getUsers() {
        DatabaseHandler handler = globalClass.getHandler();
        return (ArrayList<User>) handler.getUserTable().getAll();
    }

    /* clusterUsers
        Use k-means to cluster the users
        Input: k - the amount of clusters
        Output: ArrayList of k clusters, a cluster being an ArrayList of Strings
     */
    public ArrayList<ArrayList<User>> clusterUsers(int k) {
        /* k-means consists of 2 steps
            1. Assignment step - assign users to closest cluster
            2. Update step - calculate new means
         */
        // First, initialize k means (Forgy)
        DatabaseHandler handler = globalClass.getHandler();
        ArrayList<User> users = (ArrayList<User>) handler.getUserTable().getAll();
        double[][] kmeans = new double[k][users.size()];

        kmeans = intializeKMeans(k, kmeans);

        // Initialize clusters
        ArrayList<ArrayList<User>> oldClusters;
        ArrayList<ArrayList<User>> newClusters = new ArrayList<>();
        int[] clusters = new int[users.size()];
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
    private double[][] updateMeans(double[][] kmeans, int k, ArrayList<ArrayList<User>> clusters) {
        DatabaseHandler handler = globalClass.getHandler();
        ArrayList<User> users = (ArrayList<User>) handler.getUserTable().getAll();
        // Look at each cluster what the new means will become
        for (int cluster = 0 ; cluster < k ; cluster++) {
            // Initialization of new means for cluster 'cluster'
            double[] newmeans = new double[users.size()];
            // Look at each user in the cluster
            for (User user : clusters.get(cluster)) {
                // Get index of the user to retrieve the similarities of the user
                int index = 0;
                for (User u : users) {
                    if(u.getID().equals(user.getID())) {
                        continue;
                    }
                    else {
                        index += 1;
                    }
                }
                double[] similaritiesUser = allSimilarities[index];
                // Look at each of the values of the similarities separately to add them to the new means
                for (int simValues = 0; simValues < users.size() ; simValues++) {
                    // Add value / size(ArrayList<User> cluster) --> to get the mean
                    newmeans[simValues] += similaritiesUser[simValues] / (double) clusters.get(cluster).size();
                }
            }
            kmeans[cluster] = newmeans;
        }
        return kmeans;
    }

    private int[] assignClusters(int[] clusters, int k, double[][] kmeans) {
        DatabaseHandler handler = globalClass.getHandler();
        ArrayList<User> users = (ArrayList<User>) handler.getUserTable().getAll();
        for (int user = 0 ; user < users.size() ; user++) {
            int centroid = -1;
            double lowest = 100000.0;
            // ..and go through the kmeans centroids..
            for (int c = 0; c < k; c++) {
                // ..to see to which centroid the user belongs (using the euclidean distance)
                double eucdist = eucDistance(kmeans[c],allSimilarities[user]);
                if (eucdist < lowest) {
                    // New lowest value
                    lowest = eucdist;
                    // New centroid
                    centroid = c;
                }
            }
            clusters[user] = centroid;
        }
        return clusters;
    }

    private ArrayList<ArrayList<User>> addToClusters(ArrayList<ArrayList<User>> newClusters, int k, int[] clusters) {
        DatabaseHandler handler = globalClass.getHandler();
        ArrayList<User> users = (ArrayList<User>) handler.getUserTable().getAll();
        for (int c = 0 ; c < k ; c++) {
            // Create new ArrayList for each cluster
            ArrayList<User> cluster = new ArrayList<>();
            // Go through list of cluster assignments
            for (int user = 0 ; user < clusters.length ; user++) {
                if (clusters[user] == c) {
                    cluster.add(users.get(user));
                }
            }
            // Add the ArrayList<User> as one cluster to the newClusters
            newClusters.add(cluster);
        }
        return newClusters;
    }

    private double eucDistance(double[] centroid, double[] userSimilarity) {
        double eucdist = 0.0;
        for (int c = 0 ; c < centroid.length ; c++) {
            eucdist += Math.sqrt((centroid[c] - userSimilarity[c]) * (centroid[c] - userSimilarity[c]));
        }
        return eucdist;
    }

    private double[][] intializeKMeans(int k, double[][] kmeans) {
        boolean added = false;
        DatabaseHandler handler = globalClass.getHandler();
        ArrayList<User> users = (ArrayList<User>) handler.getUserTable().getAll();
        int[] randomNumbers = new int[k];
        // find k random instances
        for (int i = 0 ; i < k ; i++) {
            do {
                // Pick random number
                Random rand = new Random();
                int randomNumber = rand.nextInt(users.size());
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
