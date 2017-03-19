/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.database;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import si.personalshopper.data.Shop;
import si.personalshopper.data.Tag;
import si.personalshopper.database.DatabaseHandler;
import si.personalshopper.database.Table;
import si.personalshopper.database.TagTable;

public class ShopTable
implements Table {
    private static final String TABLE_SHOP = "shops";
    private static final String KEY_SHOP_NAME = "name";
    private static final String KEY_SHOP_ADDRESS = "address";
    private static final String KEY_SHOP_TIME = "time";
    private static final String KEY_SHOP_TAG = "tags";
    private static final String KEY_SHOP_SIMILARITIES = "shopsimilarities";
    private static final String KEY_SHOP_LAT = "lat";
    private static final String KEY_SHOP_LNG = "lng";
    private static final String KEY_SHOP_DIST = "distance";
    private DatabaseHandler handler;
    private Connection c;
    private Statement stmt;

    public ShopTable(DatabaseHandler handler) {
        this.handler = handler;
        this.c = handler.getConnection();
        this.stmt = handler.getStatement();
    }

    public String[] getColumnNames() {
        return new String[]{"name", "address", "time", "tags", "shopsimilarities", "lat", "lng", "distance"};
    }

    public String createTable() {
        String CREATE_SHOP_TABLE = "CREATE TABLE shops(name TEXT, address TEXT,time INTEGER,tags TEXT,shopsimilarities TEXT,lat DOUBLE,lng DOUBLE,distance INTEGER)";
        return CREATE_SHOP_TABLE;
    }

    public void insert(Object o) {
        Shop shop = (Shop)o;
        String columnString = "";
        for (String column : this.getColumnNames()) {
            columnString = columnString + column + ",";
        }
        columnString = columnString.substring(0, columnString.length() - 1);
        String sql = "INSERT INTO shops (" + columnString + ") VALUES (";
        sql = sql + "'" + shop.getName() + "',";
        sql = sql + "'" + shop.getAddress() + "',";
        sql = sql + shop.getTime() + ",";
        sql = sql + "'" + shop.getTagsString() + "',";
        sql = sql + "'" + shop.getSimilaritiesString() + "',";
        sql = sql + shop.getLat() + ",";
        sql = sql + shop.getLng() + ",";
        sql = sql + "'" + shop.getDistancesString() + "'";
        sql = sql + ");";
        try {
            this.c.setAutoCommit(false);
            this.stmt.executeUpdate(sql);
            this.c.commit();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Object getIndex(int id) {
        return null;
    }

    public Object get(String shopName) {
        try {
            System.out.println("Get a shop: " + new File(".").getCanonicalPath());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Shop shop = null;
        try {
            this.stmt = this.c.createStatement();
            ResultSet rs = this.stmt.executeQuery("SELECT * FROM shops WHERE name=" + shopName);
            while (rs.next()) {
                String shopAddress = rs.getString("address");
                int shopTime = rs.getInt("time");
                String shopTagsString = rs.getString("tags");
                ArrayList<String> shopTags = this.makeShopTagsFromString(shopTagsString);
                String shopSimilaritiesString = rs.getString("shopsimilarities");
                double[] shopSimilarities = this.makeShopSimilaritiesFromString(shopSimilaritiesString);
                double lat = rs.getDouble("lat");
                double lng = rs.getDouble("lng");
                String distancesString = rs.getString("distance");
                int[] distances = this.getDistancesFromString(distancesString);
                shop = new Shop(shopName, shopAddress, shopTags, shopTime, shopSimilarities, lat, lng, distances);
            }
            rs.close();
            this.stmt.close();
        }
        catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return shop;
    }

    private int[] getDistancesFromString(String distancesString) {
        String[] distancesSplit = distancesString.split(";");
        int[] distances = new int[distancesSplit.length];
        for (int dist = 0; dist < distancesSplit.length; ++dist) {
            distances[dist] = Integer.valueOf(distancesSplit[dist]);
        }
        return distances;
    }

    private ArrayList<String> makeShopTagsFromString(String shopTagsString) {
        String[] shopTagsSplit = shopTagsString.split(";");
        ArrayList<String> shopTags = new ArrayList<String>();
        for (int index = 0; index < shopTagsSplit.length; ++index) {
            String tag = shopTagsSplit[index];
            if (tag.equals("0") || !tag.equals("1")) continue;
            ArrayList allTags = (ArrayList)this.handler.getTagTable().getAll();
            shopTags.add(((Tag)allTags.get(index)).getTag());
        }
        return shopTags;
    }

    private double[] makeShopSimilaritiesFromString(String shopSimilaritiesString) {
        String[] shopSimilaritiesSplit = shopSimilaritiesString.split(";");
        double[] shopSimilarities = new double[shopSimilaritiesSplit.length];
        for (int sim = 0; sim < shopSimilaritiesSplit.length; ++sim) {
            shopSimilarities[sim] = Double.valueOf(shopSimilaritiesSplit[sim]);
        }
        return shopSimilarities;
    }

    public List getAll() {
        try {
            System.out.println("Get all shops: " + new File(".").getCanonicalPath());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<Shop> shopList = new ArrayList<Shop>();
        try {
            this.stmt = this.c.createStatement();
            ResultSet rs = this.stmt.executeQuery("SELECT * FROM shops");
            while (rs.next()) {
                String shopName = rs.getString("name");
                String shopAddress = rs.getString("address");
                int shopTime = rs.getInt("time");
                String shopTagsString = rs.getString("tags");
                ArrayList<String> shopTags = this.makeShopTagsFromString(shopTagsString);
                String shopSimilaritiesString = rs.getString("shopsimilarities");
                double[] shopSimilarities = this.makeShopSimilaritiesFromString(shopSimilaritiesString);
                double lat = rs.getDouble("lat");
                double lng = rs.getDouble("lng");
                String distancesString = rs.getString("distance");
                int[] distances = this.getDistancesFromString(distancesString);
                shopList.add(new Shop(shopName, shopAddress, shopTags, shopTime, shopSimilarities, lat, lng, distances));
            }
            rs.close();
            this.stmt.close();
        }
        catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return shopList;
    }

    public double[][] getShopSimilarities() {
        int size = this.getSize();
        double[][] similarities = new double[size][size];
        int index = 0;
        try {
            this.stmt = this.c.createStatement();
            ResultSet rs = this.stmt.executeQuery("SELECT * FROM shops");
            while (rs.next()) {
                String shopSimilaritiesString = rs.getString("shopsimilarities");
                double[] shopSimilarities = this.makeShopSimilaritiesFromString(shopSimilaritiesString);
                similarities[index] = shopSimilarities;
                ++index;
            }
            rs.close();
            this.stmt.close();
        }
        catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return similarities;
    }

    @Override
    public int getSize() {
        int size = 0;
        try {
            this.stmt = this.c.createStatement();
            ResultSet rs = this.stmt.executeQuery("SELECT * FROM shops");
            while (rs.next()) {
                ++size;
            }
            rs.close();
            this.stmt.close();
        }
        catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return size;
    }

    public void update(Object o) {
        Shop shop = (Shop)o;
        try {
            this.stmt = this.c.createStatement();
            String shopName = shop.getName();
            String shopAddress = shop.getAddress();
            int shopTime = shop.getTime();
            String shopTags = shop.getTagsString();
            String shopSimilarities = shop.getSimilaritiesString();
            String sql = "UPDATE shops set address = '" + shopAddress + "'," + "time" + " = " + shopTime + "," + "tags" + " = '" + shopTags + "'," + "shopsimilarities" + " = '" + shopSimilarities + "' WHERE " + "name" + " = " + shopName + ";";
            this.stmt.executeUpdate(sql);
            this.c.commit();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Object o) {
        Shop shop = (Shop)o;
        try {
            this.stmt = this.c.createStatement();
            String sql = "DELETE from shops WHERE name = " + shop.getName() + ";";
            this.stmt.executeUpdate(sql);
            this.c.commit();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

