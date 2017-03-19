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
import si.personalshopper.data.Persona;
import si.personalshopper.data.Ratings;
import si.personalshopper.data.Shop;
import si.personalshopper.data.Tag;
import si.personalshopper.data.User;
import si.personalshopper.data.Visits;
import si.personalshopper.database.DatabaseHandler;
import si.personalshopper.database.PersonaTable;
import si.personalshopper.database.Table;
import si.personalshopper.database.TagTable;

public class UserTable
implements Table {
    private static final String TABLE_USER = "users";
    private static final String KEY_USER_SESSION_ID = "session_id";
    private static final String KEY_USER_PERSONA_ID = "persona_id";
    private static final String KEY_USER_POSTAGS = "postags";
    private static final String KEY_USER_NEGTAGS = "negtags";
    private static final String KEY_USER_VISITEDLIST = "visitedlist";
    private static final String KEY_USER_RATINGLIST = "ratinglist";
    private static final String KEY_USER_TIMEBUDGET = "timebudget";
    private DatabaseHandler handler;
    private Connection c;
    private Statement stmt;
    private PersonaTable personaTable;
    private TagTable tagTable;
    ArrayList<Shop> allShops;

    public UserTable(DatabaseHandler handler, PersonaTable personaTable, TagTable tagTable, ArrayList<Shop> allShops) {
        this.handler = handler;
        this.c = handler.getConnection();
        this.stmt = handler.getStatement();
        this.personaTable = personaTable;
        this.tagTable = tagTable;
        this.allShops = allShops;
    }

    public String[] getColumnNames() {
        return new String[]{"session_id", "persona_id", "postags", "negtags", "visitedlist", "ratinglist", "timebudget"};
    }

    public String createTable() {
        String CREATE_USER_TABLE = "CREATE TABLE users(session_id TEXT,persona_id INTEGER,postags TEXT,negtags TEXT,visitedlist TEXT,ratinglist TEXT,timebudget INTEGER)";
        return CREATE_USER_TABLE;
    }

    public void insert(Object o) {
        User user = (User)o;
        String columnString = "";
        for (String column : this.getColumnNames()) {
            columnString = columnString + column + ",";
        }
        columnString = columnString.substring(0, columnString.length() - 1);
        String sql = "INSERT INTO users (" + columnString + ") VALUES (";
        sql = sql + "'" + user.getSessionID() + "',";
        sql = sql + user.getPersona().getID() + ",";
        sql = sql + "'" + user.getPosTagsString() + "',";
        sql = sql + "'" + user.getNegTagsString() + "',";
        sql = sql + "'" + user.getVisitedList().getVisitedString() + "',";
        sql = sql + "'" + user.getRatingList().getRatingsString() + "',";
        sql = sql + user.getTimeBudget();
        sql = sql + ");";
        try {
            this.stmt.executeUpdate(sql);
            this.c.commit();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getIndex(int id) {
        return null;
    }

    public User get(String sID) {
        try {
            System.out.println("Get a user: " + new File(".").getCanonicalPath());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        User user = new User(this.allShops, sID);
        try {
            this.stmt = this.c.createStatement();
            ResultSet rs = this.stmt.executeQuery("SELECT * FROM users WHERE session_id = '" + sID + "'");
            if (rs == null) {
                this.insert(user);
                return user;
            }
            while (rs.next()) {
                int userPersonaID = rs.getInt("persona_id");
                if (userPersonaID >= 0) {
                    user.setPersona((Persona)this.personaTable.getIndex(userPersonaID));
                }
                ArrayList<String> allTags = new ArrayList<String>();
                ArrayList<Tag> allTagsTemp = (ArrayList)this.tagTable.getAll();
                for (Tag tag : allTagsTemp) {
                    allTags.add(tag.getTag());
                }
                String userPosTags = rs.getString("postags");
                String userNegTags = rs.getString("negtags");
                String userVisited = rs.getString("visitedlist");
                String userRatings = rs.getString("ratinglist");
                int userTime = rs.getInt("timebudget");
                user.setTags(userPosTags, userNegTags, allTags);
                user.setVisitedList(userVisited);
                user.setRatingList(userRatings);
                user.setTimeBudget(userTime);
            }
            rs.close();
            this.stmt.close();
        }
        catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return user;
    }

    private double[] makeUserSimilaritiesFromString(String userSimilaritiesString) {
        String[] userSimilaritiesSplit = userSimilaritiesString.split(";");
        double[] userSimilarities = new double[userSimilaritiesSplit.length];
        for (int sim = 0; sim < userSimilaritiesSplit.length; ++sim) {
            userSimilarities[sim] = Double.parseDouble(userSimilaritiesSplit[sim]);
        }
        return userSimilarities;
    }

    public List getAll() {
        try {
            System.out.println("Get all users: " + new File(".").getCanonicalPath());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<User> userList = new ArrayList<User>();
        try {
            this.stmt = this.c.createStatement();
            ResultSet rs = this.stmt.executeQuery("SELECT * FROM users");
            if (rs == null) {
                return null;
            }
            while (rs.next()) {
                String sessionID = rs.getString("session_id");
                User user = new User(this.allShops, sessionID);
                int userPersonaID = rs.getInt("persona_id");
                if (userPersonaID >= 0) {
                    user.setPersona((Persona)this.personaTable.getIndex(userPersonaID));
                }
                ArrayList<String> allTags = new ArrayList<String>();
                ArrayList<Tag> allTagsTemp = (ArrayList)this.tagTable.getAll();
                for (Tag tag : allTagsTemp) {
                    allTags.add(tag.getTag());
                }
                String userPosTags = rs.getString("postags");
                String userNegTags = rs.getString("negtags");
                String userVisited = rs.getString("visitedlist");
                String userRatings = rs.getString("ratinglist");
                int userTime = rs.getInt("timebudget");
                user.setTags(userPosTags, userNegTags, allTags);
                user.setVisitedList(userVisited);
                user.setRatingList(userRatings);
                user.setTimeBudget(userTime);
                userList.add(user);
            }
            rs.close();
            this.stmt.close();
        }
        catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return userList;
    }

    @Override
    public int getSize() {
        int size = 0;
        try {
            this.stmt = this.c.createStatement();
            ResultSet rs = this.stmt.executeQuery("SELECT * FROM users");
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
        User user = (User)o;
        try {
            this.stmt = this.c.createStatement();
            String userSessionID = user.getSessionID();
            int userPersonaID = user.getPersona().getID();
            String userPosTags = user.getPosTagsString();
            String userNegTags = user.getNegTagsString();
            String userVisited = user.getVisitedList().getVisitedString();
            String userRatings = user.getRatingList().getRatingsString();
            int userTime = user.getTimeBudget();
            String sql = "UPDATE users set session_id = '" + userSessionID + "'," + "persona_id" + " = " + userPersonaID + "," + "postags" + " = '" + userPosTags + "'," + "negtags" + " = '" + userNegTags + "'," + "visitedlist" + " = '" + userVisited + "'," + "ratinglist" + " = '" + userRatings + "'," + "timebudget" + " = " + userTime + " WHERE " + "session_id" + " = '" + userSessionID + "';";
            this.stmt.executeUpdate(sql);
            this.c.commit();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Object o) {
    }
}

