/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.recommendersystems;

import java.util.ArrayList;
import java.util.Collection;

import si.personalshopper.Main;
import si.personalshopper.MainRecommender;
import si.personalshopper.data.Path;
import si.personalshopper.data.Ratings;
import si.personalshopper.data.Shop;
import si.personalshopper.data.SimilarityShops;
import si.personalshopper.data.SimilarityTags;
import si.personalshopper.data.User;
import si.personalshopper.data.WeightedGraph;
import si.personalshopper.database.DatabaseHandler;

public class CompositeRecommender {
    private User user;
    private SimilarityShops similaritiesShops;
    private SimilarityTags similaritiesTags;
    private ArrayList<Shop> allShops;
    private MainRecommender mainRecommender;

    public CompositeRecommender(MainRecommender mainRecommender, DatabaseHandler handler, ArrayList<Shop> allShops, double[][] shopSimilarities, User user) {
        this.user = user;
        this.mainRecommender = mainRecommender;
        this.similaritiesShops = new SimilarityShops(allShops, shopSimilarities);
        this.similaritiesTags = new SimilarityTags(handler);
        this.allShops = allShops;
    }

    public ArrayList<ArrayList<Shop>> ProduceAndChoose(ArrayList<Shop> shops, double beta, int k, double gamma, double mu) {
        int c = 5;
        ArrayList<ArrayList<Shop>> cand = this.produceBundles(shops, beta, mu, c);
        WeightedGraph G = this.buildBundleGraph(cand);
        return this.chooseBundles(k, gamma, G);
    }

    public ArrayList<ArrayList<Shop>> ClusterAndPick(ArrayList<Shop> shops, String alpha, double beta, int k) {
        ArrayList<ArrayList<Shop>> S = new ArrayList<ArrayList<Shop>>();
        S.add(this.bestBundle(shops, beta));
        return S;
    }

    public ArrayList<ArrayList<Shop>> produceBundles(ArrayList<Shop> I, double beta, double mu, int c) {
        ArrayList<ArrayList<Shop>> cand = new ArrayList<ArrayList<Shop>>();
        ArrayList<Shop> pivots = new ArrayList<Shop>(I);
        while (!pivots.isEmpty() && cand.size() < c) {
            ArrayList<Shop> tempAllShops = new ArrayList<Shop>(this.allShops);
            tempAllShops.removeAll(new ArrayList<Shop>(pivots));
            Shop w = this.user.getRatingList().getHighestRatedShop(new ArrayList<Shop>(tempAllShops));
            I.remove(w);
            ArrayList<Shop> S = this.pickBundle(w, I, beta);
            if (this.score(S) >= mu) {
                I.removeAll(S);
                pivots.removeAll(S);
                cand.add(S);
                continue;
            }
            pivots.remove(w);
        }
        return cand;
    }

    public WeightedGraph buildBundleGraph(ArrayList<ArrayList<Shop>> candidates) {
        WeightedGraph wg = new WeightedGraph(candidates, this.similaritiesShops);
        return wg;
    }

    public ArrayList<ArrayList<Shop>> chooseBundles(int k, double gamma, WeightedGraph G) {
        ArrayList<ArrayList<Shop>> S = G.getBundles();
        while (S.size() > k) {
            ArrayList<Shop> u = S.get(0);
            double lowestVal = 1.0E7;
            for (ArrayList<Shop> tempU : S) {
                double tempVal = 0.0;
                for (ArrayList<Shop> v : S) {
                    if (tempU.equals(v)) continue;
                    tempVal += this.w(tempU, v, k, gamma, G);
                }
                if (tempVal >= lowestVal) continue;
                lowestVal = tempVal;
                u = tempU;
            }
            S.remove(u);
        }
        return S;
    }

    private double w(ArrayList<Shop> u, ArrayList<Shop> v, int k, double gamma, WeightedGraph G) {
        double weight = 0.0;
        if (k == 1) {
            weight = this.w(u);
        } else {
            double temp1_1 = gamma / 2.0 * (double)(k - 1);
            double temp1_2 = this.w(u) + this.w(v);
            double temp1 = temp1_1 * temp1_2;
            double temp2_1 = 1.0 - gamma;
            double temp2_2 = G.getInterbundleDistance(u, v);
            double temp2 = temp2_1 * temp2_2;
            weight = temp1 + temp2;
        }
        return weight;
    }

    private double w(ArrayList<Shop> S) {
        return this.similaritiesShops.getSumSimValue(S);
    }

    public ArrayList<ArrayList<Shop>> clusterShops(ArrayList<Shop> shops, int k) {
        return this.similaritiesShops.clusterShops(shops, this.similaritiesTags, k);
    }

    public ArrayList<Shop> bestBundle(ArrayList<Shop> C, double beta) {
        ArrayList<Shop> best = new ArrayList<Shop>();
        for (Shop shop : C) {
            ArrayList<Shop> s = this.pickBundle(shop, C, beta);
            if (this.score(s) <= this.score(best)) continue;
            best = s;
        }
        return best;
    }

    public ArrayList<Shop> pickBundle(Shop w, ArrayList<Shop> I, double beta) {
        ArrayList<Shop> s = new ArrayList<Shop>();
        s.add(w);
        ArrayList<ArrayList<String>> covered = new ArrayList<ArrayList<String>>();
        covered.add(w.getTags());
        ArrayList<Shop> active = new ArrayList<Shop>(I);
        active.removeAll(s);
        boolean finish = false;
        while (!finish && active.size() != 0) {
            Shop i = this.similaritiesShops.getMostSimilarShopFromList(w, active);
            if (this.similarityTreshold(i.getTags(), covered)) {
                try {
                    ArrayList<Shop> temp = new ArrayList<Shop>(s);
                    temp.add(i);
                    if (this.f(temp, beta)) {
                        s = temp;
                        covered.add(i.getTags());
                    } else {
                        finish = true;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            active.remove(i);
        }
        return s;
    }

    public boolean similarityTreshold(ArrayList<String> tags, ArrayList<ArrayList<String>> covered) {
        for (ArrayList<String> coveredTags : covered) {
            double total = 0.0;
            for (int tag = 0; tag < tags.size(); ++tag) {
                if (!coveredTags.contains(tags.get(tag))) continue;
                total += 1.0;
            }
            if (total / (double)coveredTags.size() <= 0.75) continue;
            return false;
        }
        return true;
    }

    public boolean f(ArrayList<Shop> bundle, double beta) {
        Path path = new Path(mainRecommender, bundle, this.allShops);
        double pathTime = path.getTotalPathDistance();
        double shopTime = 0.0;
        for (Shop shop : bundle) {
            shopTime += (double)shop.getTime();
        }
        double cost = pathTime + shopTime;
        if (cost > beta) {
            return false;
        }
        return true;
    }

    public double score(ArrayList<Shop> b) {
        double score = this.user.getRatingList().getSummedRatings(b);
        return score;
    }
}

