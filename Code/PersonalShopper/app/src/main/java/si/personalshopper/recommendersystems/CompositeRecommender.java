package si.personalshopper.recommendersystems;

import android.util.Log;

import java.util.ArrayList;

import si.personalshopper.data.Path;
import si.personalshopper.data.Shop;
import si.personalshopper.data.SimilarityShops;
import si.personalshopper.data.SimilarityTags;
import si.personalshopper.data.SimilarityUsers;
import si.personalshopper.data.User;
import si.personalshopper.data.WeightedGraph;
import si.personalshopper.database.DatabaseHandler;
import si.personalshopper.global.GlobalClass;

/**
 * Created by Senna on 13-4-2016.
 */
public class CompositeRecommender {

    private User user;
    private SimilarityShops similaritiesShops;
    private SimilarityUsers similaritiesUsers;
    private SimilarityTags similaritiesTags;
    private ArrayList<Shop> allShops;

    /* Composite Recommender
        Initialize the composite recommender:
        user - the current user
        allShops - List of all shops in database
        similaritiesShops - List of similarities of shops
        similaritiesUsers - List of similarities of users
        similaritiesTags - List of similarities of tags
     */
    public CompositeRecommender(GlobalClass globalClass, DatabaseHandler handler, ArrayList<Shop> allShops, ArrayList<User> allUsers, double[][] userSimilarities, double[][] shopSimilarities, User user) {
        this.user = user;
        this.similaritiesShops = new SimilarityShops(allShops, shopSimilarities);
        this.similaritiesUsers = new SimilarityUsers(globalClass, userSimilarities);
        this.similaritiesTags = new SimilarityTags(handler);
        this.allShops = allShops;
    }

    /* Approximation Algorithm 1 - Produce and Choose - as proposed by Amer-Yahia et al.
        Input: shops, alpha, f, beta, k, gamma, mu, c
        Output: A set S of k valid bundles.
     */
    public ArrayList<ArrayList<Shop>> ProduceAndChoose(ArrayList<Shop> shops, double beta, int k, double gamma, double mu) {
        // Cand <-- produce_bundles(I,alpha,f,beta);
        int c = 5; // Because we use BOBO-5
        ArrayList<ArrayList<Shop>> cand = produceBundles(shops, beta, mu, c);
        // G <-- build_bundle_graph(Cand);
        WeightedGraph G = buildBundleGraph(cand);
        // return ChooseBundles(k, gamma, G);
        return chooseBundles(k, gamma, G);
    }

    /* Approximation Algorithm 2 - Cluster and Pick - as proposed by Amer-Yahia et al.
        Input: shops, alpha, f, beta, k
        Output: A set S of k valid bundles
     */
    public ArrayList<ArrayList<Shop>> ClusterAndPick(ArrayList<Shop> shops, String alpha, double beta, int k) {
        // clusters <-- clustering(I,k)
        ArrayList<ArrayList<Shop>> clusters = clusterShops(shops, k);
        // S <-- empty_set
        ArrayList<ArrayList<Shop>> S = new ArrayList<>();
        // for each cluster < clusters do:
        for (ArrayList<Shop> cluster: clusters) {
            // S <-- S U bestBundle(cluster,alpha,f,beta);
            S.add(bestBundle(cluster, beta));
        }
        // return S
        return S;
    }

    /* produceBundles: BOBO-5
        Input: I, alpha, beta, minimum bundle score mu, number of bundles c
        Output: a set of c valid candidate bundles
     */
    public ArrayList<ArrayList<Shop>> produceBundles(ArrayList<Shop> I, double beta, double mu, int c) {
        // Cand <-- empty set
        ArrayList<ArrayList<Shop>> cand = new ArrayList<>();
        // Pivots <-- I
        ArrayList<Shop> pivots = new ArrayList<>(I);
        // while Pivots != empty set and |Cand| < c do:
        while (!pivots.isEmpty() && cand.size() < c) {
            // w <-- pick an element from Pivots
            // PICKING METHOD: choose the pivot with the highest rating of the user (without already chosen pivots) TODO: is this good?
            Log.d("Get Rating List", user.getRatingList().getRatingsString());
            ArrayList<Shop> tempAllShops = new ArrayList<>(allShops);
            tempAllShops.removeAll(new ArrayList<>(pivots));
            // TODO: It doesn't work
            Shop w = user.getRatingList().getHighestRatedShop(new ArrayList<>(tempAllShops));
            // I <-- I \ {w}
            I.remove(w);
            // S <-- pickBundle(w,I,alpha,f,beta)
            ArrayList<Shop> S = pickBundle(w, I, beta);
            // if score(S) >= mu then
            if (score(S) >= mu) {
                // I <-- I \ S
                I.removeAll(S);
                // Pivots <-- Pivots \ S
                pivots.removeAll(S);
                // Cand <-- Cand U {S}
                cand.add(S);
            }
            // else
            else {
                // Pivots <-- Pivots \ {w}
                pivots.remove(w);
            }
        }
        // return Cand
        return cand;
    }

    /* buildBundleGraph

     */
    public WeightedGraph buildBundleGraph(ArrayList<ArrayList<Shop>> candidates) {
        WeightedGraph wg = new WeightedGraph(candidates, similaritiesShops);
        return wg;
    }

    /* chooseBundles
        Input: k, gamma and the bundle weighted graph G = (V,E)
        Output: A set S of k valid bundles.
     */
    public ArrayList<ArrayList<Shop>> chooseBundles(int k, double gamma, WeightedGraph G) {
        // Define w(u,v) = gamma/(2(k-1)) * ( w(u) + w(v) ) + (1-gamma) * psi(u,v)
        // see method w
        // S <-- V
        ArrayList<ArrayList<Shop>> S = G.getBundles();
        // while S > k do (interpreted as 'while S.size > k do')
        while (S.size() > k) {
            // u <-- argmin[u<S] SUM_(v<S) w(u,v);
            ArrayList<Shop> u = S.get(0); // initialize u as first shop in S
            double lowestVal = 10000000.0; // initialize as high value (every shop should be lower than this value)
            for (ArrayList<Shop> tempU : S) {
                double tempVal = 0.0; // initialization of value for shop u
                for (ArrayList<Shop> v : S) {
                    if (!tempU.equals(v)) {
                        tempVal += w(tempU, v, k, gamma, G);
                    }
                }
                if (tempVal < lowestVal) {
                    lowestVal = tempVal;
                    u = tempU;
                }
            }
            // Remove u from S
            S.remove(u);
        }
        // return S
        return S;
    }

    // Define w(u,v) = (gamma/(2(k-1))) * ( w(u) + w(v) ) + (1-gamma) * psi(u,v)
    private double w(ArrayList<Shop> u, ArrayList<Shop> v, int k, double gamma, WeightedGraph G) {
        double weight = 0.0;
        if (k == 1) {
            // If k == 1, the interbundle distance doesn't matter, you only want the bundle with
            // the highest quality.
            weight = w(u) + w(v);
        }
        else {
            double temp1_1 = (gamma / 2 * (k - 1));
            double temp1_2 = (w(u) + w(v));
            double temp1 = temp1_1 * temp1_2;
            double temp2_1 = (1 - gamma);
            double temp2_2 = (G.getInterbundleDistance(u, v));
            double temp2 = temp2_1 * temp2_2;
            weight = temp1 + temp2;
        }
        return weight;
    }

    // Quality of a bundle, i.e., cohesion
    // ∀S ∈ V : ω(S) =∑u,v∈S s(u,v)
    private double w(ArrayList<Shop> S) {
        return similaritiesShops.getSumSimValue(S);
    }

    /* clusterShops

     */
    public ArrayList<ArrayList<Shop>> clusterShops(ArrayList<Shop> shops, int k) {
        return similaritiesShops.clusterShops(shops, similaritiesTags, k);
    }

    /* bestBundle
        Input: cluster of shops C, alpha, f, beta
        Output: one valid bundle
     */
    public ArrayList<Shop> bestBundle(ArrayList<Shop> C, double beta) {
        // best <-- 0
        ArrayList<Shop> best = new ArrayList<>();
        // for each w < C do
        for (Shop shop: C) {
            // s <-- pickBundle(w,C,alpha,f,beta)
            ArrayList<Shop> s = pickBundle(shop,C,beta);
            // if score(s) > best then
            // Interpreted as: if score(s) > score(best) then
            if (score(s) > score(best)) {
                // best <-- s
                best = s;
            }
        }
        return best;
    }

    /* pickBundle
        Input: pivot w, set of items I, parameters alpha, beta
        Output: Bundle
     */
    public ArrayList<Shop> pickBundle(Shop w, ArrayList<Shop> I, double beta) {
        // s = {w};
        ArrayList<Shop> s = new ArrayList<>();
        s.add(w);
        // covered = {w.C};
        ArrayList<ArrayList<String>> covered = new ArrayList<>();
        // TODO: see how Shop.getTags() works here
        covered.add(w.getTags());
        // active <-- I \ {w};
        ArrayList<Shop> active = new ArrayList<>(I);
        active.removeAll(s);
        // finish = false
        boolean finish = false;
        // while not finish AND THERE IS A SHOP AVAILABLE do
        while (!finish && active.size() != 0) {
            // i <-- argmax[i<active] s(i,w)
            // TODO: this takes a lot of time, why?
            Shop i = similaritiesShops.getMostSimilarShopFromList(w, active);
            // if i.alpha !< covered then
            // if (!covered.contains(i.getTags())) { // Changed to:
            if (similarityTreshold(i.getTags(), covered)) {
                try {
                    // if f(s U {i} <= beta) then
                    ArrayList<Shop> temp = new ArrayList<>(s);
                    temp.add(i);
                    if (f(temp, beta)) {
                        // s <-- s + i;
                        s = temp;
                        // covered <-- covered U {i.alpha}
                        covered.add(i.getTags());
                    }
                    // else
                    else {
                        // finish = true
                        finish = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // active <-- active \ {i}
            active.remove(i);
        }
        // return s
        return s;
    }

    /* similarityThreshold
        Calculates whether the tags of a shop are less than 75% similar to all other shops of the covered shops
        This is instead of the check whether alpha does not yet occur in the bundle.
     */
    public boolean similarityTreshold(ArrayList<String> tags, ArrayList<ArrayList<String>> covered) {
        for (ArrayList<String> coveredTags : covered) {
            double total = 0.0;
            for (int tag = 0 ; tag < tags.size() ; tag++) {
                if (coveredTags.contains(tags.get(tag))) {
                    total += 1.0;
                }
            }
            if ((total / coveredTags.size()) > 0.75) {
                return false;
            }
        }
        return true;
    }

    /* f

     */
    public boolean f(ArrayList<Shop> bundle, double beta) {
        double pathTime = (new Path(bundle, allShops)).getTotalPathDistance();
        double shopTime = 0.0;
        for (Shop shop : bundle) {
            shopTime += shop.getTime();
        }
        double cost = pathTime + shopTime;
        if (cost > beta) {
            return false;
        }
        else {
            return true;
        }
    }

    /* score
        Input: Bundle b
        Output: Score of bundle b (double)
     */
    public double score(ArrayList<Shop> b) {
        double score = user.getRatingList().getSummedRatings(b);
        return score;
    }

}