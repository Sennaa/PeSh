/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.data;

import java.util.ArrayList;
import si.personalshopper.data.Shop;

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
        return this.first;
    }

    public ArrayList<Shop> getSecondBundle() {
        return this.second;
    }

    public double getDistance() {
        return this.distance;
    }
}

