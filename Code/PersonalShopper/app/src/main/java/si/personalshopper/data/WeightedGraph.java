package si.personalshopper.data;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Senna on 22-4-2016.
 */
public class WeightedGraph {

    private ArrayList<ArrayList<Shop>> bundles; // Vertices
    private ArrayList<Edge> edges; // Edges

    private SimilarityShops similaritiesShops;

    public WeightedGraph(ArrayList<ArrayList<Shop>> cand, SimilarityShops similaritiesShops) {
        this.bundles = cand;
        this.similaritiesShops = similaritiesShops;
        this.edges = createEdges();
    }

    /* createEdges
        Edges are interbundle distances
     */
    public ArrayList<Edge> createEdges() {
        // Initialize edges
        ArrayList<Edge> allEdges = new ArrayList<>();
        // Go twice through all bundles to calculate their interbundle distances
        for (ArrayList<Shop> firstBundle : bundles) {
            for (ArrayList<Shop> secondBundle : bundles) {
                // Do not calculate distance of a bundle to itself
                if (!firstBundle.equals(secondBundle)) {
                    Edge edge = new Edge(firstBundle, secondBundle, psi(firstBundle,secondBundle));
                    allEdges.add(edge);
                }
            }
        }
        return allEdges;
    }

    // Interbundle distances
    // ∀(Si,Sj) ∈ E : ψ(Si,Sj) = 1−max u∈Si,v∈Sj s(u,v)
    private double psi(ArrayList<Shop> u, ArrayList<Shop> v) {
        return 1.0 - similaritiesShops.getMaxSimValueShopPair(u,v);
    }

    public double getInterbundleDistance(ArrayList<Shop> first, ArrayList<Shop> second) {
        // Edges from bundles to themselves are not calculated and should not be taken into account, thus return 0 // TODO: check if that is right
        if (first.equals(second)) {
            return 0;
        }
        Edge edge = getBundleEdge(first, second);
        return edge.getDistance();
    }

    /* getBundleEdge
        return edge between two bundles
     */
    public Edge getBundleEdge(ArrayList<Shop> first, ArrayList<Shop> second) {
        for (Edge edge : edges) {
            ArrayList<Shop> firstShop = edge.getFirstBundle();
            ArrayList<Shop> secondShop = edge.getSecondBundle();
            if (firstShop.equals(first) && secondShop.equals(second)) {
                Log.d("Equals", "True");
                return edge;
            }
            if (secondShop.equals(first) && firstShop.equals(second)) {
                Log.d("Equals", "True");
                return edge;
            }
        }
        // If the edge has not been found, return null
        Log.d("Equals", "False");
        return null;
    }

    public ArrayList<ArrayList<Shop>> getBundles() {
        return bundles;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

}
