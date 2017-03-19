package si.personalshopper.data;

import java.util.ArrayList;

/**
 * Created by Senna on 26-4-2016.
 */
public class Path {

    // Initializations constructor
    private ArrayList<Shop> shops;
    private ArrayList<Shop> allShops;
    private double[][] distances;

    // Initializations path
    private ArrayList<Shop> path;
    private double[] pathDistances;
    private double totalPathDistance;

    /* Path is the shortest path between recommended shops (S1,S2,..Sn). Starts with location of Shop 1, ends with location of Shop n.

     */
    public Path(ArrayList<Shop> shops, ArrayList<Shop> allShops) {
        this.shops = shops;
        this.allShops = allShops;
        /* TODO: read data of time distances between shops, of the following form
                       | ShopDist 1 | ShopDist 2 | ShopDist 3 | ShopDist 4 | etc.
                       --------------------------------------
            ShopDist 1 |
            ShopDist 2 |
            ShopDist 3 |
            ShopDist 4 |
            etc.       |
         */

        //distances = new double[allShops.size()][allShops.size()];

        // Path initializations
        path = new ArrayList<>();
        pathDistances = new double[shops.size()];
        totalPathDistance = -1.0;
    }

    // TODO: implement
    private ArrayList<Shop> createPath() {
        return null;
    }

    public ArrayList<Shop> getPath() {
        if (path.isEmpty()) {
            return createPath();
        }
        else {
            return path;
        }
    }

    // TODO: implement
    private double[] determinePathDistances() {
        return new double[0];
    }

    public double[] getPathDistances() {
        if (pathDistances.equals(new double[allShops.size()][allShops.size()])) {
            return determinePathDistances();
        }
        else {
            return pathDistances;
        }
    }

    // TODO: implement
    private double determineTotalPathDistance() {
        return 0.0;
    }

    public double getTotalPathDistance() {
        if (totalPathDistance != -1.0) {
            return determineTotalPathDistance();
        }
        else {
            return totalPathDistance;
        }
    }

}
