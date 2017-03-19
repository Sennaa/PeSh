package si.personalshopper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import si.personalshopper.R;
import si.personalshopper.data.Shop;
import si.personalshopper.data.User;
import si.personalshopper.database.DatabaseHandler;
import si.personalshopper.global.GlobalClass;
import si.personalshopper.recommendersystems.CompositeRecommender;
import si.personalshopper.recommendersystems.HybridRecommender;
import si.personalshopper.routecalculation.RouteCalculator;

public class RouteCalculationActivity extends AppCompatActivity {

    private boolean calculated = false;

    private GlobalClass globalClass;

    private int k = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_calculation);

        globalClass = (GlobalClass) getApplicationContext();

        calculated = calculateRoute();
        // If not calculated, wait 0.1 sec.
        while(!calculated) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // If calculated, start RouteActivity
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    // Return true if route is calculated
    public boolean calculateRoute() {
        // First, determine which shops are recommended
        ArrayList<Shop> recommendedShops = determineRecommendedShops();
        // Second, use GraphHopper to calculate the optimal route
        calculateOptimalRoute(recommendedShops);
        return true;
    }

    /*
    Determines the list of shops that fits the user best according to the recommender systems.
     */
    private ArrayList<Shop> determineRecommendedShops() {
        // Either try Composite Recommender or Hybrid Recommender
        // 50% of the time Composite Recommender, 50% of the time Hybrid Recommender
        double randomNumber = Math.random();

        ArrayList<Shop> recommendedShops;

        DatabaseHandler handler = globalClass.getHandler();
        User currentUser = globalClass.getUser();
        ArrayList<Shop> allShops = (ArrayList<Shop>) handler.getShopTable().getAll();

        if (randomNumber < 1) { // if (randomNumber < 0.333) {

            // Here, do Composite Recommendation (Algorithm 1 - Produce and Choose)
            CompositeRecommender compositeRecommender = new CompositeRecommender(globalClass, handler, new ArrayList<>(allShops), (ArrayList<User>) handler.getUserTable().getAll(), handler.getUserTable().getUserSimilarities(), handler.getShopTable().getShopSimilarities(), currentUser);

            double gamma = 1.0;
            // Set mu (minimum bundle score) as 50% of the maximum score (thus depending on the time budget)
            // --> mu = 0.5 * timeBudget / shopTime
            double shopTime = 10; // Every shop is initialized with a shopTime of 10 minutes
            double mu = 0.5 * currentUser.getTimeBudget() / shopTime;
            ArrayList<ArrayList<Shop>> bundles = compositeRecommender.ProduceAndChoose(new ArrayList<>(allShops), currentUser.getTimeBudget(), k, gamma, mu);
            // Get first bundle (k = 1)
            recommendedShops = bundles.get(0);
        }
        else if (randomNumber < 0) { //else if (randomNumber >= 0.333 && randomNumber < 0.666) {
            // Here do Composite Recommendation (Algorithm 2 - Cluster and Pick)
            CompositeRecommender compositeRecommender = new CompositeRecommender(globalClass, handler, new ArrayList<>(allShops), (ArrayList<User>) handler.getUserTable().getAll(), handler.getUserTable().getUserSimilarities(), handler.getShopTable().getShopSimilarities(), currentUser);

            ArrayList<ArrayList<Shop>> bundles = compositeRecommender.ClusterAndPick(new ArrayList<>(allShops), "", currentUser.getTimeBudget(), k);
            // Get first bundle (k = 1)
            recommendedShops = bundles.get(0);
        }
        else {
            // Here, do Hybrid Recommendation
            HybridRecommender hybridRecommender = new HybridRecommender(globalClass, handler, new ArrayList<>(allShops), (ArrayList<User>) handler.getUserTable().getAll(), handler.getUserTable().getUserSimilarities(), handler.getShopTable().getShopSimilarities(), currentUser);

            recommendedShops = hybridRecommender.recommend(10);
        }
        return recommendedShops;
    }

    /*
    Calculates the optimal route along the recommended shops (TSP) - Using GraphHopper
     */
    private void calculateOptimalRoute(ArrayList<Shop> shops) {
        // Make new Thread (otherwise the app could be blocked)
        Thread t = new Thread(new RouteCalculator(shops));
        // Start new thread, do calculations in RouteCalculator.
        t.start();
        // Wait until the new Thread is finished (and thus the route is calculated).
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_route_calculation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
