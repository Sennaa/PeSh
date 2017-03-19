package si.personalshopper.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import si.personalshopper.data.Shop;
import si.personalshopper.data.Tag;

/**
 * Created by Senna on 12-5-2016.
 */
public class ShopTable implements Table {

    // Table Name
    private static final String TABLE_SHOP = "shops";
    // Table Columns
    private static final String KEY_SHOP_NAME = "name";
    private static final String KEY_SHOP_ADDRESS = "address";
    private static final String KEY_SHOP_TIME = "time";
    private static final String KEY_SHOP_TAG = "tags";
    private static final String KEY_SHOP_SIMILARITIES = "shopsimilarities";

    private DatabaseHandler handler;

    public ShopTable(DatabaseHandler handler) {
        this.handler = handler;
    }

    public String[] getColumnNames() {
        return new String[] {KEY_SHOP_NAME, KEY_SHOP_ADDRESS, KEY_SHOP_TIME, KEY_SHOP_TAG, KEY_SHOP_SIMILARITIES};
    }

    public String createTable() {
        String CREATE_SHOP_TABLE = "CREATE TABLE " + TABLE_SHOP + "(" +
                KEY_SHOP_NAME + " TEXT, " + KEY_SHOP_ADDRESS + " TEXT," +
                KEY_SHOP_TIME + " INTEGER," + KEY_SHOP_TAG + " TEXT," +
                KEY_SHOP_SIMILARITIES + " TEXT" + ")";
        return CREATE_SHOP_TABLE;
    }

    @Override
    public void add(Object o) {
        Shop shop = (Shop) o;
        SQLiteDatabase db = handler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SHOP_NAME, shop.getName());
        values.put(KEY_SHOP_ADDRESS, shop.getAddress());
        values.put(KEY_SHOP_TIME, shop.getTime());
        values.put(KEY_SHOP_TAG, shop.getTagsString());
        values.put(KEY_SHOP_SIMILARITIES, shop.getSimilaritiesString());

        db.insert(TABLE_SHOP, null, values);
        db.close();
    }

    @Override
    public Object getIndex(int id) {
        return null;
    }

    @Override
    public Object get(String shopName) {
        SQLiteDatabase db = handler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SHOP, new String[] { KEY_SHOP_NAME, KEY_SHOP_ADDRESS, KEY_SHOP_TIME, KEY_SHOP_TAG, KEY_SHOP_SIMILARITIES },
                KEY_SHOP_NAME + " =?", new String[] {shopName}, null, null, null);
        String shopAddress = cursor.getString(1);
        int shopTime = cursor.getInt(2);
        String shopTagsString = cursor.getString(3);
        ArrayList<String> shopTags = makeShopTagsFromString(shopTagsString);
        String shopSimilaritiesString = cursor.getString(4);
        double[] shopSimilarities = makeShopSimilaritiesFromString(shopSimilaritiesString);
        Shop shop = new Shop(shopName, shopAddress, shopTags, shopTime, shopSimilarities);
        cursor.close();
        return shop;
    }

    private ArrayList<String> makeShopTagsFromString(String shopTagsString) {
        String[] shopTagsSplit = shopTagsString.split(";");
        ArrayList<String> shopTags = new ArrayList<>();
        for (int index = 0 ; index < shopTagsSplit.length ; index++) {
            String tag = shopTagsSplit[index];
            if (tag.equals("0")) {
                // Do nothing, don't add
            }
            else if (tag.equals("1")) {
                // Add String (name) of tag
                // Retrieve names of tags
                ArrayList<Tag> allTags = (ArrayList<Tag>) handler.getTagTable().getAll();
                shopTags.add(allTags.get(index).getTag());
            }
            else {
                // Should not come here
                Log.d("ERROR", "WHAT IS WRONG NOW");
            }
        }
        return shopTags;
    }

    private double[] makeShopSimilaritiesFromString(String shopSimilaritiesString) {
        String[] shopSimilaritiesSplit = shopSimilaritiesString.split(";");
        double[] shopSimilarities = new double[shopSimilaritiesSplit.length];
        for (int sim = 0 ; sim < shopSimilaritiesSplit.length ; sim++) {
            shopSimilarities[sim] = Double.valueOf(shopSimilaritiesSplit[sim]);
        }
        return shopSimilarities;
    }

    @Override
    public List getAll() {
        List<Shop> shopList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_SHOP;

        SQLiteDatabase db = handler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Shop shop;

        if (cursor.moveToFirst()) {
            do {
                String shopName = cursor.getString(0);
                String shopAddress = cursor.getString(1);
                int shopTime = cursor.getInt(2);
                String shopTagsString = cursor.getString(3);
                ArrayList<String> shopTags = makeShopTagsFromString(shopTagsString);
                String shopSimilaritiesString = cursor.getString(4);
                double[] shopSimilarities = makeShopSimilaritiesFromString(shopSimilaritiesString);
                shop = new Shop(shopName, shopAddress, shopTags, shopTime, shopSimilarities);

                shopList.add(shop);
                System.gc();
            }
            while (cursor.moveToNext());
            cursor.close();
        }

        return shopList;
    }

    public double[][] getShopSimilarities() {
        int size = getSize();
        double[][] similarities = new double[size][size];

        String selectQuery = "SELECT * FROM " + TABLE_SHOP;

        SQLiteDatabase db = handler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int index = 0;
        if (cursor.moveToFirst()) {
            do {
                String shopSimilaritiesString = cursor.getString(4);
                double[] shopSimilarities = makeShopSimilaritiesFromString(shopSimilaritiesString);
                similarities[index] = shopSimilarities;
                index += 1;
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return similarities;
    }

    public List getAll(SQLiteDatabase db) {
        List<Shop> shopList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_SHOP;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                // TODO: this takes a lot of time, why?
                String shopName = cursor.getString(0);
                String shopAddress = cursor.getString(1);
                int shopTime = cursor.getInt(2);
                String shopTagsString = cursor.getString(3);
                ArrayList<String> shopTags = makeShopTagsFromString(shopTagsString);
                String shopSimilaritiesString = cursor.getString(4);
                double[] shopSimilarities = makeShopSimilaritiesFromString(shopSimilaritiesString);

                shopList.add(new Shop(shopName, shopAddress, shopTags, shopTime, shopSimilarities));
            }
            while (cursor.moveToNext());
            cursor.close();
        }

        return shopList;
    }

    @Override
    public int getSize() {
        String countQuery = "SELECT * FROM " + TABLE_SHOP;
        SQLiteDatabase db = handler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    @Override
    public int update(Object o) {
        Shop shop = (Shop) o;

        SQLiteDatabase db = handler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SHOP_NAME, shop.getName());
        values.put(KEY_SHOP_ADDRESS, shop.getAddress());
        values.put(KEY_SHOP_TIME, shop.getTime());
        values.put(KEY_SHOP_TAG, shop.getTagsString());
        values.put(KEY_SHOP_SIMILARITIES, shop.getSimilaritiesString());

        return db.update(TABLE_SHOP, values, KEY_SHOP_NAME + " = ?",
                new String[] { shop.getName() });
    }

    @Override
    public void delete(Object o) {
        Shop shop = (Shop) o;
        SQLiteDatabase db = handler.getWritableDatabase();
        db.delete(TABLE_SHOP, KEY_SHOP_NAME + " = ?",
                new String[] {shop.getName()});
        db.close();
    }
}
