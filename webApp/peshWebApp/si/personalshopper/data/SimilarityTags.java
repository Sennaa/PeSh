/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import si.personalshopper.data.Tag;
import si.personalshopper.database.DatabaseHandler;
import si.personalshopper.database.TagTable;

public class SimilarityTags {
    private ArrayList<String> tags;
    private double[][] allSimilarities;
    private DatabaseHandler handler;

    public SimilarityTags(DatabaseHandler handler) {
        this.handler = handler;
        this.tags = new ArrayList();
        this.tags = this.getTags();
        this.allSimilarities = new double[this.tags.size()][this.tags.size()];
        this.allSimilarities = this.getAllSimilarities();
    }

    public double[] getSimilarities(String tag) {
        int index = this.tags.indexOf(tag);
        return this.allSimilarities[index];
    }

    public double[][] getAllSimilarities() {
        if (this.allSimilarities == null) {
            ArrayList tempSimilarities = (ArrayList)this.handler.getTagTable().getAll();
            for (int i = 0; i < this.allSimilarities.length; ++i) {
                this.allSimilarities[i] = ((Tag)tempSimilarities.get(i)).getSimilarities();
            }
        }
        return this.allSimilarities;
    }

    public ArrayList<String> getTags() {
        if (this.tags.isEmpty()) {
            ArrayList tempTag = (ArrayList)this.handler.getTagTable().getAll();
            for (Tag tag : tempTag) {
                this.tags.add(tag.getTag());
            }
        }
        return this.tags;
    }

    public ArrayList<ArrayList<String>> clusterTags(int k) {
        ArrayList oldClusters;
        double[][] kmeans = new double[k][this.tags.size()];
        kmeans = this.intializeKMeans(k, kmeans);
        ArrayList<ArrayList<String>> newClusters = new ArrayList<ArrayList<String>>();
        int[] clusters = new int[this.tags.size()];
        do {
            oldClusters = new ArrayList(newClusters);
            clusters = this.assignClusters(clusters, k, kmeans);
            newClusters = this.addToClusters(newClusters, k, clusters);
            kmeans = this.updateMeans(kmeans, k, newClusters);
        } while (!oldClusters.equals(newClusters));
        return newClusters;
    }

    private double[][] updateMeans(double[][] kmeans, int k, ArrayList<ArrayList<String>> clusters) {
        for (int cluster = 0; cluster < k; ++cluster) {
            double[] newmeans = new double[this.tags.size()];
            for (String tag : clusters.get(cluster)) {
                int index = this.tags.indexOf(tag);
                double[] similaritiesTag = this.allSimilarities[index];
                for (int simValues = 0; simValues < this.tags.size(); ++simValues) {
                    double[] arrd = newmeans;
                    int n = simValues;
                    arrd[n] = arrd[n] + similaritiesTag[simValues] / (double)clusters.get(cluster).size();
                }
            }
            kmeans[cluster] = newmeans;
        }
        return kmeans;
    }

    private int[] assignClusters(int[] clusters, int k, double[][] kmeans) {
        for (int tag = 0; tag < this.tags.size(); ++tag) {
            int centroid = -1;
            double lowest = 100000.0;
            for (int c = 0; c < k; ++c) {
                double eucdist = this.eucDistance(kmeans[c], this.allSimilarities[tag]);
                if (eucdist >= lowest) continue;
                lowest = eucdist;
                centroid = c;
            }
            clusters[tag] = centroid;
        }
        return clusters;
    }

    private ArrayList<ArrayList<String>> addToClusters(ArrayList<ArrayList<String>> newClusters, int k, int[] clusters) {
        for (int c = 0; c < k; ++c) {
            ArrayList<String> cluster = new ArrayList<String>();
            for (int tag = 0; tag < clusters.length; ++tag) {
                if (clusters[tag] != c) continue;
                cluster.add(this.tags.get(tag));
            }
            newClusters.add(cluster);
        }
        return newClusters;
    }

    private double eucDistance(double[] centroid, double[] tagSimilarity) {
        double eucdist = 0.0;
        for (int c = 0; c < centroid.length; ++c) {
            eucdist += Math.sqrt((centroid[c] - tagSimilarity[c]) * (centroid[c] - tagSimilarity[c]));
        }
        return eucdist;
    }

    private double[][] intializeKMeans(int k, double[][] kmeans) {
        boolean added = false;
        int[] randomNumbers = new int[k];
        for (int i = 0; i < k; ++i) {
            do {
                Random rand = new Random();
                int randomNumber = rand.nextInt(this.tags.size());
                if (!Arrays.asList(randomNumbers).contains(randomNumber)) continue;
                kmeans[i] = this.allSimilarities[i];
                randomNumbers[i] = randomNumber;
                added = true;
            } while (!added);
            added = false;
        }
        return kmeans;
    }
}

