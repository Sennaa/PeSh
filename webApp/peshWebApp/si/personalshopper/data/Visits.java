/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.data;

import java.util.ArrayList;
import si.personalshopper.data.Shop;

public class Visits {
    private ArrayList<Shop> allShops;
    private boolean[] visited;

    public Visits(ArrayList<Shop> shops) {
        this.allShops = shops;
        this.visited = new boolean[shops.size()];
        for (int i = 0; i < this.visited.length; ++i) {
            this.visited[i] = false;
        }
    }

    public boolean[] getVisited() {
        return this.visited;
    }

    public String getVisitedString() {
        String visits = "";
        if (this.visited == null) {
            return visits;
        }
        for (boolean visit : this.visited) {
            visits = visit ? visits + "1;" : visits + "0;";
        }
        return visits;
    }

    public boolean isVisited(Shop shop) {
        int index = this.allShops.indexOf(shop);
        return this.visited[index];
    }

    public void addVisitedShop(Shop shop) {
        int index = this.allShops.indexOf(shop);
        this.visited[index] = true;
    }

    public void setVisited(String visits) {
        String[] visList = visits.split(";");
        if (!visits.equals("") && !visits.equals(" ")) {
            for (int index = 0; index < this.visited.length; ++index) {
                int value = Integer.parseInt(visList[index]);
                if (value == 1) {
                    this.visited[index] = true;
                    continue;
                }
                if (value != 0) continue;
                this.visited[index] = false;
            }
        }
    }

    public void removeVisitedShop(Shop shop) {
        int index = this.allShops.indexOf(shop);
        this.visited[index] = false;
    }
}

