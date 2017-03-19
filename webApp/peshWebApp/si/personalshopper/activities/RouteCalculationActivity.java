/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.activities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import si.personalshopper.MainRecommender;
import si.personalshopper.data.Ratings;
import si.personalshopper.data.Shop;
import si.personalshopper.data.User;
import si.personalshopper.database.DatabaseHandler;
import si.personalshopper.database.ShopTable;
import si.personalshopper.global.GlobalClass;
import si.personalshopper.recommendersystems.CompositeRecommender;

public class RouteCalculationActivity {
    private int k = 1;
    private MainRecommender mainRecommender;

    public RouteCalculationActivity(String recommender, MainRecommender mainRecommender) {
        this.mainRecommender = mainRecommender;
        this.compositeOrder();
    }

    public void compositeOrder() {
        ArrayList<Shop> compositeShopOrder = this.calculateCompositeRoute();
        MainRecommender.globalClass.setRightCompositeShopOrder(compositeShopOrder);
    }

    public int getShopSize() {
        return MainRecommender.globalClass.getCompositeShopSize();
    }

    public ArrayList<Shop> calculateCompositeRoute() {
        ArrayList<Shop> recommendedShops = this.determineCompositeRecommendedShops();
        return recommendedShops;
    }

    private ArrayList<Shop> determineCompositeRecommendedShops() {
        DatabaseHandler handler = MainRecommender.globalClass.getHandler();
        User currentUser = MainRecommender.globalClass.getUser();
        ArrayList<Shop> allShops = handler.getAllShops();
        CompositeRecommender compositeRecommender = new CompositeRecommender(handler, new ArrayList<Shop>(allShops), handler.getShopTable().getShopSimilarities(), currentUser);
        double gamma = 1.0;
        double shopTime = 10.0;
        double average = currentUser.getRatingList().getSummedRatings(allShops) / (double)allShops.size();
        double stdev = currentUser.getRatingList().calculateStDev(average);
        ArrayList bundles = new ArrayList();
        double mu = (double)currentUser.getTimeBudget() / shopTime - 1.0;
        while (bundles.isEmpty()) {
            ArrayList<Shop> tempShopList = new ArrayList<Shop>(allShops);
            Collections.shuffle(tempShopList);
            bundles = compositeRecommender.ProduceAndChoose(tempShopList, currentUser.getTimeBudget(), this.k, gamma, mu);
            mu -= 1.0;
        }
        ArrayList compositeRecommendedShops = (ArrayList)bundles.get(0);
        return compositeRecommendedShops;
    }
}

