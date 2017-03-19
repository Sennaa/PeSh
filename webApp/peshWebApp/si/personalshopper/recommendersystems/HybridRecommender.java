/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.recommendersystems;

import java.util.ArrayList;
import si.personalshopper.data.Ratings;
import si.personalshopper.data.Shop;
import si.personalshopper.data.SimilarityShops;
import si.personalshopper.data.SimilarityTags;
import si.personalshopper.data.User;
import si.personalshopper.database.DatabaseHandler;

public class HybridRecommender {
    private User user;
    private SimilarityShops similaritiesShops;
    private SimilarityTags similaritiesTags;
    private ArrayList<Shop> allShops;

    public HybridRecommender(DatabaseHandler handler, ArrayList<Shop> allShops, double[][] shopSimilarities, User user) {
        this.user = user;
        this.allShops = allShops;
        this.similaritiesShops = new SimilarityShops(allShops, shopSimilarities);
        this.similaritiesTags = new SimilarityTags(handler);
    }

    public ArrayList<Shop> recommend(int n) {
        ArrayList<Shop> contentBased = this.contentBasedRecommendations(n);
        return contentBased;
    }

    public ArrayList<Shop> contentBasedRecommendations(int n) {
        return this.user.getRatingList().getTopNShops(n);
    }
}

