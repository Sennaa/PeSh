/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.global;

import java.util.ArrayList;
import si.personalshopper.data.Shop;
import si.personalshopper.data.User;
import si.personalshopper.database.DatabaseHandler;
import si.personalshopper.database.UserTable;

public class GlobalClass {
    private User user;
    private DatabaseHandler handler;
    private ArrayList<Shop> allShops;
    private ArrayList<Shop> rightCompositeShopOrder;
    private ArrayList<Shop> rightHybridShopOrder;

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        if (this.handler.getUserTable().getSize() == 0) {
            this.handler.getUserTable().insert(user);
        } else {
            this.handler.getUserTable().update(user);
        }
    }

    public DatabaseHandler getHandler() {
        return this.handler;
    }

    public void setHandler(DatabaseHandler handler) {
        this.handler = handler;
    }

    public ArrayList<Shop> getAllShops() {
        return this.allShops;
    }

    public void setAllShops(ArrayList<Shop> allShops) {
        this.allShops = allShops;
    }

    public ArrayList<Shop> getRightCompositeShopOrder() {
        return this.rightCompositeShopOrder;
    }

    public void setRightCompositeShopOrder(ArrayList<Shop> rightShopOrder) {
        this.rightCompositeShopOrder = rightShopOrder;
    }

    public int getCompositeShopSize() {
        return this.rightCompositeShopOrder.size();
    }

    public ArrayList<Shop> getRightHybridShopOrder() {
        return this.rightHybridShopOrder;
    }

    public void setRightHybridShopOrder(ArrayList<Shop> rightShopOrder) {
        this.rightHybridShopOrder = rightShopOrder;
    }
}

