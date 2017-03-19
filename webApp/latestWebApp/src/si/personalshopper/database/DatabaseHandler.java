/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import si.personalshopper.data.Shop;
import si.personalshopper.data.User;
import si.personalshopper.database.PersonaTable;
import si.personalshopper.database.ShopTable;
import si.personalshopper.database.TagTable;
import si.personalshopper.database.UserTable;

public class DatabaseHandler {
    private static final String DATABASE_NAME = "PeSh";
    private static final String TABLE_USER = "users";
    private UserTable userTable;
    private static final String TABLE_PERSONA = "personas";
    private PersonaTable personaTable;
    private static final String TABLE_SHOP = "shops";
    private ShopTable shopTable;
    private static final String TABLE_TAG = "tags";
    private TagTable tagTable;
    private static final String keywordsShops = "resources/DATA_shops.csv";
    private static final String tags = "resources/DATA_tags.csv";
    private static final String tagDataPersonas = "resources/DATA_personas.csv";
    private ArrayList<Shop> allShops;
    private User user;
    private String sID;
    private Connection c = null;
    private Statement stmt = null;
    private String recommender;

    public DatabaseHandler(String recommender, String sID) {
        this.recommender = recommender;
        this.sID = sID;
        if (!DatabaseHandler.doesDatabaseExist(sID, "PeSh" + recommender)) {
            System.out.println("PeSh" + recommender + ", It does not exist");
            this.openDatabase(sID);
            this.user = new User();
            try {
                System.out.println("Start creating tables");
                this.createTables(this.c);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("PeSh" + recommender + ", It does exist");
            this.openDatabase(sID);
            this.shopTable = new ShopTable(this);
            this.tagTable = new TagTable(this);
            this.allShops = (ArrayList)this.shopTable.getAll();
            this.personaTable = new PersonaTable(this, this.allShops);
            this.userTable = new UserTable(this, this.personaTable, this.tagTable, this.allShops);
        }
    }

    public void closeConnection() {
        try {
            this.c.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openDatabase(String sID) {
        try {
            Class.forName("org.sqlite.JDBC");
            String path = new File(".").getCanonicalPath();
            new File(path + "/databases/" + sID).mkdirs();
            this.c = DriverManager.getConnection("jdbc:sqlite:databases/" + sID + "/" + "PeSh" + this.recommender + ".db");
            this.c.setAutoCommit(false);
            System.out.println("Opened database successfully");
        }
        catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public Statement getStatement() {
        return this.stmt;
    }

    public Connection getConnection() {
        return this.c;
    }

    private static boolean doesDatabaseExist(String sID, String dbName) {
        File dbFile = new File("./databases/" + sID + "/" + dbName + ".db");
        return dbFile.exists();
    }

    public void createTables(Connection c) throws SQLException {
        this.stmt = c.createStatement();
        this.shopTable = new ShopTable(this);
        this.stmt.executeUpdate(this.shopTable.createTable());
        this.tagTable = new TagTable(this);
        this.stmt.executeUpdate(this.tagTable.createTable());
        this.readCSVFile("tags", "resources/DATA_tags.csv", this.tagTable.getColumnNames());
        this.readCSVFile("shops", "resources/DATA_shops.csv", this.shopTable.getColumnNames());
        this.allShops = (ArrayList)this.shopTable.getAll();
        this.personaTable = new PersonaTable(this, this.allShops);
        this.stmt.executeUpdate(this.personaTable.createTable());
        this.readCSVFile("personas", "resources/DATA_personas.csv", this.personaTable.getColumnNames());
        this.userTable = new UserTable(this, this.personaTable, this.tagTable, this.allShops);
        this.stmt.executeUpdate(this.userTable.createTable());
        this.stmt.close();
        System.out.println("Tables created successfully");
    }

    public ArrayList<Shop> getAllShops() {
        return this.allShops;
    }

    public ShopTable getShopTable() {
        return this.shopTable;
    }

    public UserTable getUserTable() {
        return this.userTable;
    }

    public TagTable getTagTable() {
        return this.tagTable;
    }

    public PersonaTable getPersonaTable() {
        return this.personaTable;
    }

    private void readCSVFile(String table, String filename, String[] columnNames) throws SQLException {
        this.stmt = this.c.createStatement();
        String columnString = "";
        for (String column : columnNames) {
            columnString = columnString + column + ",";
        }
        columnString = columnString.substring(0, columnString.length() - 1);
        BufferedReader br = null;
        try {
            FileReader file = new FileReader(filename);
            br = new BufferedReader(file);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            String line;
            String insertString = "INSERT INTO " + table + " (" + columnString + ") VALUES (";
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] columns = line.split("\\^");
                String sql = insertString;
                for (String column2 : columns) {
                    sql = sql + "'" + column2.trim() + "',";
                }
                sql = sql.substring(0, sql.length() - 1);
                sql = sql + ");";
                this.stmt.executeUpdate(sql);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.stmt.close();
    }

    public void onUpgrade(Connection c, Statement stmt) {
        try {
            stmt.executeUpdate("DROP TABLE IF EXISTS users");
            stmt.executeUpdate("DROP TABLE IF EXISTS personas");
            stmt.executeUpdate("DROP TABLE IF EXISTS shops");
            stmt.executeUpdate("DROP TABLE IF EXISTS tags");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            this.createTables(c);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

