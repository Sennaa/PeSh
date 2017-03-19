package si.personalshopper.data;

import java.util.ArrayList;

/**
 * Created by Senna on 28-4-2016.
 */
public class Edge {

    private ArrayList<Shop> first;
    private ArrayList<Shop> second;
    private double distance;

    public Edge(ArrayList<Shop> first, ArrayList<Shop> second, double distance) {
        this.first = first;
        this.second = second;
        this.distance = distance;
    }

    public ArrayList<Shop> getFirstBundle() {
        return first;
    }

    public ArrayList<Shop> getSecondBundle() {
        return second;
    }

    public double getDistance() {
        return distance;
    }

}
