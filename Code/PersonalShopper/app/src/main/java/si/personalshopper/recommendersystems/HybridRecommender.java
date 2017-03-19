package si.personalshopper.recommendersystems;

import java.util.ArrayList;

import si.personalshopper.data.Ratings;
import si.personalshopper.data.Shop;
import si.personalshopper.data.SimilarityShops;
import si.personalshopper.data.SimilarityTags;
import si.personalshopper.data.SimilarityUsers;
import si.personalshopper.data.User;
import si.personalshopper.database.DatabaseHandler;
import si.personalshopper.global.GlobalClass;

/**
 * Created by Senna on 13-4-2016.
 */
public class HybridRecommender {

    private User user;
    private SimilarityShops similaritiesShops;
    private SimilarityUsers similaritiesUsers;
    private SimilarityTags similaritiesTags;

    /* Hybrid Recommender
        Initialize the hybrid recommender:
        user - the current user
        allShops - List of all shops in database
        similaritiesShops - List of similarities of shops
        similaritiesUsers - List of similarities of users
        similaritiesTags - List of similarities of tags
     */
    public HybridRecommender(GlobalClass globalClass, DatabaseHandler handler, ArrayList<Shop> allShops, ArrayList<User> allUsers, double[][] userSimilarities, double[][] shopSimilarities, User user) {
        this.user = user;
        this.similaritiesShops = new SimilarityShops(allShops, shopSimilarities);
        this.similaritiesUsers = new SimilarityUsers(globalClass, userSimilarities);
        this.similaritiesTags = new SimilarityTags(handler);
    }

    public ArrayList<Shop> recommend(int n) {
        ArrayList<Shop> contentBased = contentBasedRecommendations(n);
        ArrayList<Shop> collaborativeFiltering = collaborativeFiltering(n);
        return combineRecommendations(contentBased, collaborativeFiltering, n);
    }

    /* Content Based Recommendations (based on similarities of items)

     */
    public ArrayList<Shop> contentBasedRecommendations(int n) {
        return user.getRatingList().getTopNShops(n);
    }

    /* Collaborative Filtering Recommendations (based on similarities of users)

     */
    public ArrayList<Shop> collaborativeFiltering(int n) {
        int n_user = similaritiesUsers.getN(n);
        ArrayList<User> closestNUsers = similaritiesUsers.getNClosestUsers(user, n_user);
        // to calculate topNShops, add ratingList of all N closest Users and pick shops of N highest values
        Ratings newRatingList = closestNUsers.get(0).getRatingList();
        // Start from 1 because 0 is the initialized value
        for (int u = 1 ; u < n_user ; u++) {
            newRatingList.add(closestNUsers.get(u).getRatingList());
        }
        return newRatingList.getTopNShops(n);
    }

    /* combineRecommendations
        Add recommendations from both recommenders
     */
    public ArrayList<Shop> combineRecommendations(ArrayList<Shop> contentBased, ArrayList<Shop> collaborativeFiltering, int n) {
        ArrayList<Shop> recommendedShops = new ArrayList<>();
        for (int r = 0 ; r < n ; r++) {
            // Make sure the amount of shops is of size n
            if (recommendedShops.size() < n) {
                // Only add shops that are not in the list yet
                if (!recommendedShops.contains(contentBased.get(r))) {
                    recommendedShops.add(contentBased.get(r));
                }
                if (!recommendedShops.contains(collaborativeFiltering.get(r))) {
                    recommendedShops.add(collaborativeFiltering.get(r));
                }
            }
        }
        return recommendedShops;
    }

}
