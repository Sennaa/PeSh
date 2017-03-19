/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper;

import java.util.ArrayList;
import si.personalshopper.activities.MainActivity;
import si.personalshopper.activities.PersonaSelectActivity;
import si.personalshopper.activities.RecommendationActivity;
import si.personalshopper.activities.RouteCalculationActivity;
import si.personalshopper.data.User;
import si.personalshopper.global.GlobalClass;

public class MainRecommender {
    private GlobalClass globalClass;
    private String recommender;
    private PersonaSelectActivity personaSelectActivity;
    private RecommendationActivity recommendationActivity;
    private RouteCalculationActivity routeCalculationActivity;
    private String personaName;

    public MainRecommender(String recommender) {
        globalClass = new GlobalClass();
        this.recommender = recommender;
    }

    public GlobalClass getGlobalClass() {
        return globalClass;
    }

    public void setGlobalClass(GlobalClass globalClass) {
        this.globalClass = globalClass;
    }

    public void startMain(String sID) {
        new MainActivity(this, this.recommender, sID);
        this.personaSelectActivity = new PersonaSelectActivity(this);
    }

    public String getPersonaHTML() {
        return this.personaSelectActivity.getHTML();
    }

    public void setPersona(String personaName, String sID) {
        this.personaName = personaName;
        this.personaSelectActivity.setPersona(personaName, sID);
    }

    public void calculateRoute() {
        this.routeCalculationActivity = new RouteCalculationActivity(this.recommender, this);
    }

    public String getRecommendationHTML(String position, int shopSize) {
        this.recommendationActivity = new RecommendationActivity(this.recommender, this, shopSize);
        return this.recommendationActivity.getHTML(position);
    }

    public int getShopSize() {
        return this.routeCalculationActivity.getShopSize();
    }

    public void addFeedback(String shopName) {
        this.recommendationActivity.addFeedback(shopName);
    }

    public void confirmRecommendation() {
        this.recommendationActivity.confirmRecommendation();
    }

    public void setTimeBudget(int timeBudget) {
        User user = globalClass.getUser();
        user.setTimeBudget(timeBudget);
        globalClass.setUser(user);
    }

    public int getRouteTime() {
        return this.recommendationActivity.getRouteTime();
    }

    public ArrayList<String> getRecommendedShops() {
        return this.recommendationActivity.getRecommendedShops();
    }
}
