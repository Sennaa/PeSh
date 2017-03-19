package si.personalshopper.routecalculation;


import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import si.personalshopper.data.Shop;

/**
 * Created by Senna on 23-6-2016.
 */
public class RouteCalculator implements Runnable {

    ArrayList<Shop> shops;

    public RouteCalculator(ArrayList<Shop> shops) {
        this.shops = shops;
        Log.d("Shops", "shops");
    }

    @Override
    public void run() {
        // Do route calculations here

    }
}
