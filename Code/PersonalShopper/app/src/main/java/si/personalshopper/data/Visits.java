package si.personalshopper.data;

import java.util.ArrayList;

/**
 * Created by Senna on 20-4-2016.
 */
public class Visits {

    private ArrayList<Shop> allShops;
    private boolean[] visited;

    /* Visit
        Shop shop: Shopname
        boolean visited: whether shop is visited (True) or not (False)
     */
    public Visits(ArrayList<Shop> shops) {
        this.allShops = shops;
        this.visited = new boolean[shops.size()];
        // Initialization: no shops have been visited
        for (int i = 0 ; i < visited.length ; i++) {
            visited[i] = false;
        }
    }

    public boolean[] getVisited() {
        return visited;
    }

    public String getVisitedString() {
        String visits = "";
        if (visited == null) {
            return visits;
        }
        for (boolean visit : visited) {
            if (visit) {
                visits += "1;";
            }
            else {
                visits += "0;";
            }
        }
        return visits;
    }

    // Check if shop is visited
    public boolean isVisited(Shop shop) {
        int index = allShops.indexOf(shop);
        return visited[index];
    }

    // Mark a shop as visited
    public void addVisitedShop(Shop shop) {
        int index = allShops.indexOf(shop);
        visited[index] = true;
    }

    public void setVisited(String visits) {
        String[] visList = visits.split(";");
        if (visits.equals("") || visits.equals(" ")) {
            // Do nothing
        }
        else {
            for (int index = 0; index < visited.length; index++) {
                int value = Integer.parseInt(visList[index]);
                if (value == 1) {
                    visited[index] = true;
                } else if (value == 0) {
                    visited[index] = false;
                } else {
                    // SHOULD NOT COME HERE
                }
            }
        }
    }

    // Remove a shop from the visited list (e.g. when a mistake has been made)
    public void removeVisitedShop(Shop shop) {
        int index = allShops.indexOf(shop);
        visited[index] = false;
    }

}
