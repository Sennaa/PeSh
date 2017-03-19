/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.activities;

import si.personalshopper.MainRecommender;
import si.personalshopper.data.User;
import si.personalshopper.database.DatabaseHandler;
import si.personalshopper.global.GlobalClass;

public class MainActivity {
    private DatabaseHandler databaseHandler;
    private MainRecommender mainRecommender;

    public MainActivity(MainRecommender mainRecommender, String recommender, String sID) {
        this.mainRecommender = mainRecommender;
        this.databaseHandler = new DatabaseHandler(recommender, sID);
        this.setGlobals(sID);
    }

    public DatabaseHandler getDatabaseHandler() {
        return this.databaseHandler;
    }

    public void setGlobals(String sID) {
        GlobalClass globalClass = mainRecommender.getGlobalClass();
        globalClass.setHandler(this.databaseHandler);
        globalClass.setAllShops(this.databaseHandler.getAllShops());
        globalClass.setUser(new User(this.databaseHandler.getAllShops(), sID));
        mainRecommender.setGlobalClass(globalClass);
    }
}

