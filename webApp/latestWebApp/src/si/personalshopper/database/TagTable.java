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
import si.personalshopper.data.Tag;
import si.personalshopper.database.DatabaseHandler;
import si.personalshopper.database.Table;

public class TagTable
implements Table {
    private static final String TABLE_TAG = "tags";
    private static final String KEY_TAG_NAME = "name";
    private static final String KEY_TAG_SIMILARITIES = "tagsimilarities";
    private DatabaseHandler handler;
    private Connection c;
    private Statement stmt;

    public TagTable(DatabaseHandler handler) {
        this.handler = handler;
        this.c = handler.getConnection();
        this.stmt = handler.getStatement();
    }

    public String[] getColumnNames() {
        return new String[]{"name", "tagsimilarities"};
    }

    public String createTable() {
        String CREATE_TAG_TABLE = "CREATE TABLE tags(name TEXT,tagsimilarities TEXT)";
        return CREATE_TAG_TABLE;
    }

    public void insert(Object o) {
        Tag tag = (Tag)o;
        String columnString = "";
        for (String column : this.getColumnNames()) {
            columnString = columnString + column + ",";
        }
        columnString = columnString.substring(0, columnString.length() - 1);
        String sql = "INSERT INTO tags (" + columnString + ") VALUES (";
        sql = sql + "'" + tag.getTag() + "',";
        sql = sql + "'" + tag.getSimilaritiesString() + "'";
        sql = sql + ");";
        try {
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

    public Object get(String tagName) {
        Tag tag = null;
        try {
            this.stmt = this.c.createStatement();
            ResultSet rs = this.stmt.executeQuery("SELECT * FROM tags WHERE name=" + tagName);
            while (rs.next()) {
                String tagTag = rs.getString("name");
                String tagSimilaritiesString = rs.getString("tagsimilarities");
                double[] tagSimilarities = this.getTagSimilaritiesFromString(tagSimilaritiesString);
                tag = new Tag(tagTag, tagSimilarities);
            }
            rs.close();
            this.stmt.close();
        }
        catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return tag;
    }

    private double[] getTagSimilaritiesFromString(String simString) {
        String[] simSplit = simString.split(";");
        double[] similarities = new double[simSplit.length];
        for (int sim = 0; sim < simSplit.length; ++sim) {
            similarities[sim] = Double.valueOf(simSplit[sim]);
        }
        return similarities;
    }

    public List getAll() {
        ArrayList<Tag> tagList = new ArrayList<Tag>();
        try {
            this.stmt = this.c.createStatement();
            ResultSet rs = this.stmt.executeQuery("SELECT * FROM tags");
            while (rs.next()) {
                String tagTag = rs.getString("name");
                String tagSimilaritiesString = rs.getString("tagsimilarities");
                double[] tagSimilarities = this.getTagSimilaritiesFromString(tagSimilaritiesString);
                tagList.add(new Tag(tagTag, tagSimilarities));
            }
            rs.close();
            this.stmt.close();
        }
        catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return tagList;
    }

    private double[] getSimilaritiesFromString(String similaritiesString) {
        String[] simSplit = similaritiesString.split("; ");
        double[] similarities = new double[simSplit.length];
        for (int sim = 1; sim < simSplit.length; ++sim) {
            similarities[sim] = Double.valueOf(simSplit[sim]);
        }
        return similarities;
    }

    @Override
    public int getSize() {
        int size = 0;
        try {
            this.stmt = this.c.createStatement();
            ResultSet rs = this.stmt.executeQuery("SELECT * FROM tags");
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
        Tag tag = (Tag)o;
        try {
            this.stmt = this.c.createStatement();
            String tagName = tag.getTag();
            String tagSimilarities = tag.getSimilaritiesString();
            String sql = "UPDATE tags set name = '" + tagName + "'," + "tagsimilarities" + " = '" + tagSimilarities + "' WHERE " + "name" + " = " + tagName + ";";
            this.stmt.executeUpdate(sql);
            this.c.commit();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Object o) {
        Tag tag = (Tag)o;
        try {
            this.stmt = this.c.createStatement();
            String sql = "DELETE from tags WHERE name = '" + tag.getTag() + "';";
            this.stmt.executeUpdate(sql);
            this.c.commit();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

