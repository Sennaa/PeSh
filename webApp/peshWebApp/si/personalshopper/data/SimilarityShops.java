/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.data;

import java.util.ArrayList;
import si.personalshopper.data.Shop;
import si.personalshopper.data.SimilarityTags;

public class SimilarityShops {
    private double[][] similarities;
    private ArrayList<Shop> allShops;

    public SimilarityShops(ArrayList<Shop> allShops, double[][] shopSimilarities) {
        this.allShops = allShops;
        this.similarities = shopSimilarities;
    }

    public double getSimValue(Shop first, Shop second) {
        int indexFirst = this.allShops.indexOf(first);
        int indexSecond = this.allShops.indexOf(second);
        return this.similarities[indexFirst][indexSecond];
    }

    public double getSumSimValue(ArrayList<Shop> S) {
        double sumSimValue = 0.0;
        for (Shop u : S) {
            for (Shop v : S) {
                if (u.equals(v)) continue;
                sumSimValue += this.getSimValue(u, v);
            }
        }
        return sumSimValue;
    }

    public Shop getMostSimilarShop(Shop s) {
        int index = this.allShops.indexOf(s);
        double[] simValues = this.similarities[index];
        double highestSimVal = 0.0;
        int indexSimVal = 0;
        for (int i = 0; i < simValues.length; ++i) {
            if (i == index || simValues[i] <= highestSimVal) continue;
            highestSimVal = simValues[i];
            indexSimVal = i;
        }
        return this.allShops.get(indexSimVal);
    }

    public Shop getMostSimilarShopFromList(Shop s, ArrayList<Shop> list) {
        Shop mostSimilar = null;
        double highestVal = -10.0;
        for (Shop from : list) {
            double simVal = this.getSimValue(s, from);
            if (simVal <= highestVal) continue;
            mostSimilar = from;
            highestVal = simVal;
        }
        return mostSimilar;
    }

    public double getMaxSimValueShopPair(ArrayList<Shop> firstList, ArrayList<Shop> secondList) {
        double simValue = 0.0;
        for (Shop first : firstList) {
            for (Shop second : secondList) {
                double newValue = this.getSimValue(first, second);
                if (newValue <= simValue) continue;
                simValue = newValue;
            }
        }
        return simValue;
    }

    public ArrayList<ArrayList<Shop>> clusterShops(ArrayList<Shop> shops, SimilarityTags similarityTags, int k) {
        ArrayList<ArrayList<String>> tagClusters = similarityTags.clusterTags(k);
        ArrayList newClusters = new ArrayList();
        for (int i = 0; i < tagClusters.size(); ++i) {
            newClusters.add(new ArrayList());
        }
        for (int shop = 0; shop < shops.size(); ++shop) {
            int highest = -1;
            int currentCluster = -1;
            for (int cluster = 0; cluster < tagClusters.size(); ++cluster) {
                int amountTags = 0;
                for (String c : tagClusters.get(cluster)) {
                    for (String shopTag : shops.get(shop).getTags()) {
                        if (!c.equals(shopTag)) continue;
                        ++amountTags;
                    }
                }
                if (amountTags <= highest) continue;
                currentCluster = cluster;
            }
            ((ArrayList)newClusters.get(currentCluster)).add(shop);
        }
        ArrayList shopClusters = null;
        for (int cluster = 0; cluster < k; ++cluster) {
            ArrayList c = (ArrayList)newClusters.get(cluster);
            ArrayList<Shop> shopCluster = new ArrayList<Shop>();
            int size = c.size();
            for (Integer shop2 : c) {
                shopCluster.add(shops.get(shop2));
            }
            shopClusters.add(shopCluster);
        }
        return shopClusters;
    }
}

