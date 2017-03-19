package si.personalshopper.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import si.personalshopper.data.Persona;
import si.personalshopper.data.Shop;
import si.personalshopper.data.Tag;
import si.personalshopper.data.User;

/**
 * Created by Senna on 11-5-2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PeSh";
    private static final int DATABASE_VERSION = 107;

    /* TABLES */
    // Table 1 //
    // Table 1 Name
    private static final String TABLE_USER = "users";
    private UserTable userTable;
    // Table 2 //
    // Table 2 Name
    private static final String TABLE_PERSONA = "personas";
    private PersonaTable personaTable;
    // Table 3 //
    // Table 3 Name
    private static final String TABLE_SHOP = "shops";
    private ShopTable shopTable;
    // Table 4 //
    // Table 4 Name
    private static final String TABLE_TAG = "tags";
    private TagTable tagTable;
    /* FILES */
    // KeywordsShops.csv
    private static final String keywordsShops = "DATA_shops.csv";
    private static final int keywordsShopsColumnSize = 5;

    // Tags.csv
    private static final String tags = "DATA_tags.csv";
    private static final int tagsColumnSize = 2;

    // TagDataPersonas.csv
    private static final String tagDataPersonas = "DATA_personas.csv";
    private static final int tagDataPersonasColumnSize = 7;

    private Context context;
    private ArrayList<Shop> allShops;

    private User user;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.user = new User();
        SQLiteDatabase database = getWritableDatabase();
        if(doesDatabaseExist(context,DATABASE_NAME)) {
            // if tables are already created, do this:
            shopTable = new ShopTable(this);
            tagTable = new TagTable(this);
            // TODO: this function takes a lot of time, why?
            allShops = (ArrayList<Shop>) shopTable.getAll(database);
            personaTable = new PersonaTable(this, allShops);
            userTable = new UserTable(this, personaTable, tagTable, allShops);
        }
    }

    /**
     * Check if the database exist and can be read.
     *
     * @return true if it exists and can be read, false if it doesn't
     */
    private static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        shopTable = new ShopTable(this);
        db.execSQL(shopTable.createTable());
        tagTable = new TagTable(this);
        db.execSQL(tagTable.createTable());
        allShops = (ArrayList<Shop>) shopTable.getAll(db);
        userTable = new UserTable(this, personaTable, tagTable, allShops);
        db.execSQL(userTable.createTable());
        personaTable = new PersonaTable(this, allShops);
        db.execSQL(personaTable.createTable());
        // Read csv files into tables
        readCSVFiles(db);
    }

    public ShopTable getShopTable() { return shopTable; }

    public UserTable getUserTable() { return userTable; }

    public TagTable getTagTable() { return tagTable; }

    public PersonaTable getPersonaTable() { return personaTable; }

    public User getUser(String sID) {
        if (user.isEmpty()) {
            user = userTable.get(sID);
            return user;
        }
        else {
            return user;
        }
    }

    public void readCSVFiles(SQLiteDatabase db) {
        // KeywordsShops.csv
        readCSVFile(TABLE_SHOP, keywordsShops, keywordsShopsColumnSize, shopTable.getColumnNames(), db);
        // Tags.csv
        readCSVFile(TABLE_TAG, tags, tagsColumnSize, tagTable.getColumnNames(), db);
        // TagDataPersonas.csv
        readCSVFile(TABLE_PERSONA, tagDataPersonas, tagDataPersonasColumnSize, personaTable.getColumnNames(), db);
    }

    private void readCSVFile(String table, String filename, int columnSize, String[] columnNames, SQLiteDatabase db) {
        InputStream inputStream = getFile(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        db.beginTransaction();
        try {
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] columns = line.split("\\^");

                ContentValues cv = new ContentValues();
                for (int i = 0 ; i < columns.length ; i++) {
                    cv.put(columnNames[i], columns[i].trim());
                }
                // if there are empty cells
                if (columnSize > columns.length) {
                    for (int i = columns.length ; i < columnSize ; i++) {
                        cv.put(columnNames[i], " ");
                    }
                }
                db.insert(table, null, cv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private InputStream getFile(String filename) {
        AssetManager manager = context.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = manager.open(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSONA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
        // Create tables again
        onCreate(db);
    }

}
