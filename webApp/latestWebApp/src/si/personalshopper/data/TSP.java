/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.data;

import java.util.ArrayList;
import si.personalshopper.MainRecommender;
import si.personalshopper.data.Shop;
import si.personalshopper.database.DatabaseHandler;
import si.personalshopper.global.GlobalClass;

public class TSP {
    private DatabaseHandler handler;
    private int[][] distMatrix;
    private ArrayList<Shop> shopOrder = new ArrayList();
    private MainRecommender mainRecommender;

    public TSP(MainRecommender mainRecommender, ArrayList<Shop> shops) {
        this.mainRecommender = mainRecommender;
        handler = mainRecommender.getGlobalClass().getHandler();
        this.distMatrix = this.createDistanceMatrix(shops);
        this.determineShopOrder(shops);
    }

    public int[][] createDistanceMatrix(ArrayList<Shop> shops) {
        int[][] distMatrix = new int[shops.size()][shops.size()];
        ArrayList<Shop> allShops = mainRecommender.getGlobalClass().getAllShops();
        int[] indices = new int[shops.size()];
        for (int index = 0; index < shops.size(); ++index) {
            indices[index] = allShops.indexOf(shops.get(index));
        }
        for (int i = 0; i < indices.length; ++i) {
            for (int j = 0; j < indices.length; ++j) {
                int[] distances;
                if (j == i) {
                    distMatrix[i][j] = 0;
                    distMatrix[j][i] = 0;
                    continue;
                }
                if (indices[j] > indices[i]) {
                    distances = this.handler.getAllShops().get(i).getDistances();
                    distMatrix[i][j] = distances[j];
                    distMatrix[j][i] = distances[j];
                    continue;
                }
                distances = this.handler.getAllShops().get(j).getDistances();
                distMatrix[i][j] = distances[i];
                distMatrix[j][i] = distances[i];
            }
        }
        return distMatrix;
    }

    public void determineShopOrder(ArrayList<Shop> oldShopsOrder) {
        this.shopOrder.add(oldShopsOrder.get(0));
        ArrayList<Integer> visited = new ArrayList<Integer>();
        visited.add(new Integer(0));
        for (int i = 1; i < oldShopsOrder.size(); ++i) {
            while (visited.size() < i + 1) {
                Shop previousShop = this.shopOrder.get(i - 1);
                int indexPreviousShop = (Integer)visited.get(i - 1);
                int[] distances = this.distMatrix[indexPreviousShop];
                for (Integer integer : visited) {
                    distances[integer.intValue()] = 100000;
                }
                int nearestShopDistance = 100000;
                int nearestShopIndex = -1;
                for (int j = 0; j < distances.length; ++j) {
                    if (distances[j] >= nearestShopDistance) continue;
                    nearestShopDistance = distances[j];
                    nearestShopIndex = j;
                }
                visited.add(new Integer(nearestShopIndex));
                this.shopOrder.add(oldShopsOrder.get(nearestShopIndex));
            }
        }
    }

    public ArrayList<Shop> getShopOrder() {
        return this.shopOrder;
    }
}

