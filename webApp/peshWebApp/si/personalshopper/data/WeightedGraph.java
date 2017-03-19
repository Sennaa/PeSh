/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.data;

import java.util.ArrayList;
import si.personalshopper.data.Edge;
import si.personalshopper.data.Shop;
import si.personalshopper.data.SimilarityShops;

public class WeightedGraph {
    private ArrayList<ArrayList<Shop>> bundles;
    private ArrayList<Edge> edges;
    private SimilarityShops similaritiesShops;

    public WeightedGraph(ArrayList<ArrayList<Shop>> cand, SimilarityShops similaritiesShops) {
        this.bundles = cand;
        this.similaritiesShops = similaritiesShops;
        this.edges = this.createEdges();
    }

    public ArrayList<Edge> createEdges() {
        ArrayList<Edge> allEdges = new ArrayList<Edge>();
        for (ArrayList<Shop> firstBundle : this.bundles) {
            for (ArrayList<Shop> secondBundle : this.bundles) {
                if (firstBundle.equals(secondBundle)) continue;
                Edge edge = new Edge(firstBundle, secondBundle, this.psi(firstBundle, secondBundle));
                allEdges.add(edge);
            }
        }
        return allEdges;
    }

    private double psi(ArrayList<Shop> u, ArrayList<Shop> v) {
        return 1.0 - this.similaritiesShops.getMaxSimValueShopPair(u, v);
    }

    public double getInterbundleDistance(ArrayList<Shop> first, ArrayList<Shop> second) {
        if (first.equals(second)) {
            return 0.0;
        }
        Edge edge = this.getBundleEdge(first, second);
        return edge.getDistance();
    }

    public Edge getBundleEdge(ArrayList<Shop> first, ArrayList<Shop> second) {
        for (Edge edge : this.edges) {
            ArrayList<Shop> firstShop = edge.getFirstBundle();
            ArrayList<Shop> secondShop = edge.getSecondBundle();
            if (firstShop.equals(first) && secondShop.equals(second)) {
                return edge;
            }
            if (!secondShop.equals(first) || !firstShop.equals(second)) continue;
            return edge;
        }
        return null;
    }

    public ArrayList<ArrayList<Shop>> getBundles() {
        return this.bundles;
    }

    public ArrayList<Edge> getEdges() {
        return this.edges;
    }
}

