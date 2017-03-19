/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.activities;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import si.personalshopper.MainRecommender;
import si.personalshopper.data.Path;
import si.personalshopper.data.Ratings;
import si.personalshopper.data.Shop;
import si.personalshopper.data.Tag;
import si.personalshopper.data.TagValue;
import si.personalshopper.data.User;
import si.personalshopper.database.DatabaseHandler;
import si.personalshopper.database.ShopTable;
import si.personalshopper.database.TagTable;
import si.personalshopper.global.GlobalClass;
import si.personalshopper.recommendersystems.HybridRecommender;

public class RecommendationActivity {
    private ArrayList<Shop> recommendedShops = new ArrayList();
    private ArrayList<String> posFeedback = new ArrayList();
    private ArrayList<String> negFeedback = new ArrayList();
    private MainRecommender mainRecommender;
    private int routeTime;
    private int shopSize;

    public RecommendationActivity(String recommender, MainRecommender mainRecommender, int shopSize) {
        this.shopSize = shopSize;
        this.mainRecommender = mainRecommender;
        if (recommender.equals("composite")) {
            System.out.println("Calculating composite recommendation");
            this.addCompositeShops();
        } else if (recommender.equals("contentbased")) {
            System.out.println("Calculating content-based recommendation");
            this.contentBasedOrder();
            this.addContentbasedShops();
        } else if (recommender.equals("random")) {
            System.out.println("Calculating random recommendation");
            this.addRandomShops();
        }
    }

    public void contentBasedOrder() {
        ArrayList<Shop> hybridShopOrder = this.calculateHybridRoute(this.shopSize);
        MainRecommender.globalClass.setRightHybridShopOrder(hybridShopOrder);
    }

    public ArrayList<Shop> calculateHybridRoute(int size) {
        ArrayList<Shop> recommendedShops = this.determineHybridRecommendedShops(size);
        return recommendedShops;
    }

    private ArrayList<Shop> determineHybridRecommendedShops(int size) {
        DatabaseHandler handler = MainRecommender.globalClass.getHandler();
        User currentUser = MainRecommender.globalClass.getUser();
        ArrayList<Shop> allShops = handler.getAllShops();
        HybridRecommender hybridRecommender = new HybridRecommender(handler, new ArrayList<Shop>(allShops), handler.getShopTable().getShopSimilarities(), currentUser);
        ArrayList<Shop> hybridRecommendedShops = hybridRecommender.recommend(size);
        return hybridRecommendedShops;
    }

    private void addRandomShops() {
        this.posFeedback = new ArrayList();
        this.negFeedback = new ArrayList();
        System.out.println("Composite Shop Size: " + this.shopSize);
        ArrayList<Shop> randomShops = this.getRandomShops(this.shopSize);
        this.recommendedShops = new ArrayList<Shop>(randomShops);
        this.addShopInfo(this.shopSize, randomShops);
    }

    private ArrayList<Shop> getRandomShops(int shopSize) {
        int i;
        ArrayList<Shop> randomShops = new ArrayList<Shop>();
        ArrayList<Shop> allShops = MainRecommender.globalClass.getAllShops();
        int nShops = allShops.size();
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (i = 0; i < nShops; ++i) {
            indices.add(new Integer(i));
        }
        Collections.shuffle(indices);
        for (i = 0; i < shopSize; ++i) {
            randomShops.add(allShops.get((Integer)indices.get(i)));
        }
        return randomShops;
    }

    private void addCompositeShops() {
        this.posFeedback = new ArrayList();
        this.negFeedback = new ArrayList();
        ArrayList<Shop> compositeShopOrder = MainRecommender.globalClass.getRightCompositeShopOrder();
        this.recommendedShops = new ArrayList<Shop>(compositeShopOrder);
        this.addShopInfo(this.shopSize, compositeShopOrder);
    }

    public void addContentbasedShops() {
        this.posFeedback = new ArrayList();
        this.negFeedback = new ArrayList();
        System.out.println("Composite Shop Size: " + this.shopSize);
        ArrayList<Shop> contentbasedShopOrder = MainRecommender.globalClass.getRightHybridShopOrder();
        this.recommendedShops = new ArrayList<Shop>(contentbasedShopOrder);
        this.addShopInfo(this.shopSize, contentbasedShopOrder);
    }

    public int getRouteTime() {
        return this.routeTime;
    }

    public String getHTML(String position) {
        int shopTime = 0;
        for (Shop shop : this.recommendedShops) {
            shopTime += shop.getTime();
        }
        this.routeTime = (int)new Path(this.recommendedShops, MainRecommender.globalClass.getAllShops()).getTotalPathDistance() + shopTime;
        String shopTable = "";
        for (Shop shop2 : this.recommendedShops) {
            shopTable = shopTable + "\t\t\t<li class=\"list-group-item\">" + shop2.getName() + "</li>\n";
        }
        String htmlString = "<th width=\"250\">\n\t\t<div class=\"" + position + " panel panel-default\">\n\t\t <!-- Default panel contents -->\n\t\t <div id=\"" + position + "\" class=\"panel-heading\" style=\"background-color: #3399ff\">Routeduur: " + Integer.toString(this.routeTime) + " minuten</div>\n\n\t\t  <!-- List group -->\n\t\t <ul class=\"list-group\">\n" + shopTable + "\t\t </ul>\n\t\t</div>\n\t  </th>";
        return htmlString;
    }

    private void addShopInfo(int shopSize, ArrayList<Shop> shopOrder) {
        for (int index = 0; index < shopSize; ++index) {
            String shopname = shopOrder.get(index).getName();
            this.negFeedback.add(shopname);
        }
    }

    public void addFeedback(String shopname) {
        if (this.posFeedback.contains(shopname)) {
            this.posFeedback.remove(shopname);
            if (!this.negFeedback.contains(shopname)) {
                this.negFeedback.add(shopname);
            }
        } else if (this.negFeedback.contains(shopname)) {
            this.negFeedback.remove(shopname);
            if (!this.posFeedback.contains(shopname)) {
                this.posFeedback.add(shopname);
            }
        } else {
            this.posFeedback.add(shopname);
        }
    }

    public void confirmRecommendation() {
        User currentUser = MainRecommender.globalClass.getUser();
        ArrayList<TagValue> oldTags = currentUser.getTags();
        ArrayList allTags = (ArrayList)MainRecommender.globalClass.getHandler().getTagTable().getAll();
        ArrayList<String> allTagNames = this.getAllTagNames(allTags);
        ArrayList<Shop> allShops = MainRecommender.globalClass.getAllShops();
        ArrayList<String> allShopNames = this.getAllShopNames(allShops);
        ArrayList<TagValue> newTags = this.createTagValues(oldTags, allTagNames, allShopNames, allShops);
        Ratings ratings = new Ratings(allShops);
        ratings = ratings.computeRatings(newTags);
        currentUser.setRatingList(ratings);
        MainRecommender.globalClass.setUser(currentUser);
    }

    private ArrayList<String> getAllShopNames(ArrayList<Shop> allShops) {
        ArrayList<String> allShopNames = new ArrayList<String>();
        for (Shop shop : allShops) {
            allShopNames.add(shop.getName());
        }
        return allShopNames;
    }

    private ArrayList<String> getAllTagNames(ArrayList<Tag> allTags) {
        ArrayList<String> allTagNames = new ArrayList<String>();
        for (Tag tag : allTags) {
            allTagNames.add(tag.getTag());
        }
        return allTagNames;
    }

    private ArrayList<TagValue> createTagValues(ArrayList<TagValue> tags, ArrayList<String> allTagNames, ArrayList<String> allShopNames, ArrayList<Shop> allShops) {
        double alpha = 0.5;
        tags = this.createNegTagValues(allShopNames, allShops, allTagNames, tags, alpha);
        tags = this.createPosTagValues(allShopNames, allShops, allTagNames, tags, alpha);
        for (int i = 0; i < tags.size(); ++i) {
            TagValue tagValue = tags.get(i);
            if (tagValue.getRating() > 1.0) {
                tags.remove(i);
                tags.add(i, new TagValue(tagValue.getTag(), 1.0));
                continue;
            }
            if (tagValue.getRating() >= -1.0) continue;
            tags.remove(i);
            tags.add(i, new TagValue(tagValue.getTag(), -1.0));
        }
        return tags;
    }

    private ArrayList<TagValue> createPosTagValues(ArrayList<String> allShopNames, ArrayList<Shop> allShops, ArrayList<String> allTagNames, ArrayList<TagValue> tags, double alpha) {
        for (String shopName : this.posFeedback) {
            int index = allShopNames.indexOf(shopName);
            ArrayList<String> shopTags = allShops.get(index).getTags();
            int shopTagLength = shopTags.size();
            for (String tagName : shopTags) {
                int tagIndex = allTagNames.indexOf(tagName);
                double rating = tags.get(tagIndex).getRating();
                TagValue old = tags.remove(tagIndex);
                tags.add(tagIndex, new TagValue(old.getTag(), rating + alpha / (double)shopTagLength));
            }
        }
        return tags;
    }

    private ArrayList<TagValue> createNegTagValues(ArrayList<String> allShopNames, ArrayList<Shop> allShops, ArrayList<String> allTagNames, ArrayList<TagValue> tags, double alpha) {
        for (String shopName : this.negFeedback) {
            int index = allShopNames.indexOf(shopName);
            ArrayList<String> shopTags = allShops.get(index).getTags();
            int shopTagLength = shopTags.size();
            for (String tagName : shopTags) {
                int tagIndex = allTagNames.indexOf(tagName);
                double rating = tags.get(tagIndex).getRating();
                TagValue old = tags.remove(tagIndex);
                tags.add(tagIndex, new TagValue(old.getTag(), rating - alpha / (double)shopTagLength));
            }
        }
        return tags;
    }

    public ArrayList<String> getRecommendedShops() {
        ArrayList<String> recommendedShopsNames = new ArrayList<String>();
        for (Shop shop : this.recommendedShops) {
            recommendedShopsNames.add(shop.getName());
        }
        return recommendedShopsNames;
    }
}

