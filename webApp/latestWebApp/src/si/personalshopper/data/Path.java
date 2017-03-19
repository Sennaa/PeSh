/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.data;

import java.util.ArrayList;

import si.personalshopper.Main;
import si.personalshopper.MainRecommender;
import si.personalshopper.data.Shop;
import si.personalshopper.data.TSP;

public class Path {
    private ArrayList<Shop> shops;
    private ArrayList<Shop> allShops;
    private double[][] distances;
    private ArrayList<Shop> path;
    private double[] pathDistances;
    private double totalPathDistance;
    private MainRecommender mainRecommender;

    public Path(MainRecommender mainRecommender, ArrayList<Shop> shops, ArrayList<Shop> allShops) {
        this.mainRecommender = mainRecommender;
        this.shops = shops;
        this.allShops = allShops;
        this.path = new ArrayList();
        this.totalPathDistance = -1.0;
    }

    private ArrayList<Shop> createPath() {
        TSP tsp = new TSP(this.mainRecommender, this.shops);
        return tsp.getShopOrder();
    }

    public ArrayList<Shop> getPath() {
        if (this.path.isEmpty()) {
            return this.createPath();
        }
        return this.path;
    }

    private double[] determinePathDistances() {
        this.pathDistances = new double[this.shops.size()];
        if (this.path.isEmpty()) {
            this.path = this.createPath();
        }
        for (int i = 0; i < this.path.size(); ++i) {
            int index;
            if (i == this.path.size() - 1) {
                int[] shopDistance = this.path.get(i).getDistances();
                index = this.allShops.indexOf(this.path.get(0));
                this.pathDistances[i] = shopDistance[index];
                continue;
            }
            int[] shopDistances = this.path.get(i).getDistances();
            index = this.allShops.indexOf(this.path.get(i + 1));
            this.pathDistances[i] = shopDistances[index];
        }
        return this.pathDistances;
    }

    public double[] getPathDistances() {
        if (this.pathDistances == null) {
            return this.determinePathDistances();
        }
        return this.pathDistances;
    }

    private double determineTotalPathDistance() {
        if (this.pathDistances == null) {
            this.determinePathDistances();
        }
        this.totalPathDistance = 0.0;
        for (double distance : this.pathDistances) {
            this.totalPathDistance += distance;
        }
        return this.totalPathDistance / 60.0;
    }

    public double getTotalPathDistance() {
        if (this.totalPathDistance == -1.0) {
            return this.determineTotalPathDistance();
        }
        return this.totalPathDistance;
    }
}

