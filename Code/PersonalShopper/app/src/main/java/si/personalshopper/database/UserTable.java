package si.personalshopper.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import si.personalshopper.data.Persona;
import si.personalshopper.data.Shop;
import si.personalshopper.data.Tag;
import si.personalshopper.data.User;

/**
 * Created by Senna on 11-5-2016.
 */
public class UserTable implements Table {

    // Table name
    private static final String TABLE_USER = "users";
    // Column names
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_PERSONA_ID = "persona_id";
    private static final String KEY_USER_POSTAGS = "postags";
    private static final String KEY_USER_NEGTAGS = "negtags";
    private static final String KEY_USER_VISITEDLIST = "visitedlist";
    private static final String KEY_USER_RATINGLIST = "ratinglist";
    private static final String KEY_USER_SIMILARITIES = "usersimilarities";
    private static final String KEY_USER_TIMEBUDGET = "timebudget";
    private static final String KEY_USER_EXTRATAGS = "extratags";

    // The database handler
    private DatabaseHandler handler;

    // The belonging table PersonaTable and TagTable
    private PersonaTable personaTable;
    private TagTable tagTable;

    // List of all shops
    ArrayList<Shop> allShops;

    public UserTable(DatabaseHandler handler, PersonaTable personaTable, TagTable tagTable, ArrayList<Shop> allShops) {
        this.handler = handler;
        this.personaTable = personaTable;
        this.tagTable = tagTable;
        this.allShops = allShops;
    }

    public String[] getColumnNames() {
        return new String[] {KEY_USER_ID, KEY_USER_PERSONA_ID, KEY_USER_POSTAGS, KEY_USER_NEGTAGS,
        KEY_USER_VISITEDLIST, KEY_USER_RATINGLIST, KEY_USER_SIMILARITIES, KEY_USER_TIMEBUDGET,
        KEY_USER_EXTRATAGS};
    }

    public String createTable() {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_USER_ID + " INTEGER," + KEY_USER_PERSONA_ID + " INTEGER,"
                + KEY_USER_POSTAGS + " TEXT," + KEY_USER_NEGTAGS + " TEXT,"
                + KEY_USER_VISITEDLIST + " TEXT," + KEY_USER_RATINGLIST + " TEXT,"
                + KEY_USER_SIMILARITIES + " TEXT," + KEY_USER_TIMEBUDGET + " INTEGER,"
                + KEY_USER_EXTRATAGS + " TEXT" + ")";
        return CREATE_USER_TABLE;
    }

    @Override
    public void add(Object o) {
        // Change Object to User class
        User user = (User) o;

        SQLiteDatabase db = handler.getWritableDatabase();

        ArrayList<Tag> allTags = (ArrayList<Tag>) tagTable.getAll();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user.getID());
        values.put(KEY_USER_PERSONA_ID, user.getPersona().getID());
        values.put(KEY_USER_POSTAGS, user.getPosTagsString(allTags));
        values.put(KEY_USER_NEGTAGS, user.getNegTagsString(allTags));
        values.put(KEY_USER_VISITEDLIST, user.getVisitedList().getVisitedString());
        values.put(KEY_USER_RATINGLIST, user.getRatingList().getRatingsString());
        values.put(KEY_USER_SIMILARITIES, user.getUsersimilaritiesString());
        values.put(KEY_USER_TIMEBUDGET, user.getTimeBudget());
        values.put(KEY_USER_EXTRATAGS, user.getExtraSelectedTagsString(allTags));

        db.insert(TABLE_USER, null, values);
        db.close();
    }

    @Override
    public User getIndex(int id) {
        return null;
    }

    @Override
    // Get user that belongs to sID
    public User get(String sID) {
        int count = getSize();
        SQLiteDatabase db = handler.getWritableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, KEY_USER_ID + " =?",
                new String[] {sID}, null, null, null);
        if (cursor.moveToFirst()) {
            // Retrieve user
            User user = new User(sID, allShops, count);
            int persona_id = cursor.getInt(1);
            if (persona_id >= 0) {
                user.setPersona((Persona) personaTable.getIndex(persona_id));
            }
            ArrayList<String> allTags = new ArrayList<>();
            ArrayList<Tag> allTagsTemp = (ArrayList<Tag>) tagTable.getAll();
            for (Tag tag : allTagsTemp) {
                allTags.add(tag.getTag());
            }
            user.setPosTags(cursor.getString(2), allTags);
            user.setNegTags(cursor.getString(3), allTags);
            user.setVisitedList(cursor.getString(4));
            user.setRatingList(cursor.getString(5));
            user.setUsersimilarities(cursor.getString(6));
            user.setTimeBudget(cursor.getInt(7));
            user.setSelectedTagsFromString(cursor.getString(8), allTags);
            cursor.close();
            return user;
        }
        else {
            cursor.close();
            User user = new User(sID, allShops, count + 1);
            add(user);
            return user;
        }
    }

    private double[] makeUserSimilaritiesFromString(String userSimilaritiesString) {
        String[] userSimilaritiesSplit = userSimilaritiesString.split(";");
        double[] userSimilarities = new double[userSimilaritiesSplit.length];
        for (int sim = 0 ; sim < userSimilaritiesSplit.length ; sim++) {
            userSimilarities[sim] = Double.parseDouble(userSimilaritiesSplit[sim]);
        }
        return userSimilarities;
    }

    public double[][] getUserSimilarities() {
        int size = getSize();
        double[][] similarities = new double[size][size];
        // SELECT All Query
        String selectQuery = "SELECT * FROM " + TABLE_USER;

        SQLiteDatabase db = handler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int index = 0;

        if (cursor.moveToFirst()) {
            do {
                String userSimilaritiesString = cursor.getString(6);
                double[] userSimilarities = makeUserSimilaritiesFromString(userSimilaritiesString);
                similarities[index] = userSimilarities;
                index += 1;
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return similarities;
    }

    @Override
    public List getAll() {
        List<User> userList = new ArrayList<>();
        // SELECT All Query
        String selectQuery = "SELECT * FROM " + TABLE_USER;

        SQLiteDatabase db = handler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int count = getSize();

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // Retrieve user
                User user = new User(cursor.getString(0), allShops, count);
                int persona_id = cursor.getInt(1);
                if (persona_id >= 0) {
                    user.setPersona((Persona) personaTable.getIndex(persona_id));
                }
                ArrayList<String> allTags = (ArrayList<String>) tagTable.getAll();
                user.setPosTags(cursor.getString(2), allTags);
                user.setNegTags(cursor.getString(3), allTags);
                user.setVisitedList(cursor.getString(4));
                user.setRatingList(cursor.getString(5));
                user.setUsersimilarities(cursor.getString(6));
                user.setTimeBudget(cursor.getInt(7));
                user.setSelectedTagsFromString(cursor.getString(8), allTags);
                // Add tag to list
                userList.add(user);
            }
            while (cursor.moveToNext());
            cursor.close();
        }

        return userList;
    }

    @Override
    public int getSize() {
        String countQuery = "SELECT * FROM " + TABLE_USER;
        SQLiteDatabase db = handler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    @Override
    public int update(Object o) {
        // Change Object to User class
        User user = (User) o;

        SQLiteDatabase db = handler.getWritableDatabase();

        ArrayList<Tag> allTags = (ArrayList<Tag>) tagTable.getAll();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user.getID());
        values.put(KEY_USER_PERSONA_ID, user.getPersona().getID());
        values.put(KEY_USER_POSTAGS, user.getPosTagsString(allTags));
        values.put(KEY_USER_NEGTAGS, user.getNegTagsString(allTags));
        values.put(KEY_USER_VISITEDLIST, user.getVisitedList().getVisitedString());
        values.put(KEY_USER_RATINGLIST, user.getRatingList().getRatingsString());
        values.put(KEY_USER_SIMILARITIES, user.getUsersimilaritiesString());
        values.put(KEY_USER_TIMEBUDGET, user.getTimeBudget());
        String test = user.getExtraSelectedTagsString(allTags);
        values.put(KEY_USER_EXTRATAGS, user.getExtraSelectedTagsString(allTags));

        // updating row
        return db.update(TABLE_USER, values, KEY_USER_ID + " = ?",
                new String[] { String.valueOf(user.getID())});
    }

    @Override
    public void delete(Object o) {
        // Change Object to UserID class
        User user = (User) o;

        SQLiteDatabase db = handler.getWritableDatabase();
        db.delete(TABLE_USER, KEY_USER_ID + " =?",
                new String[] { String.valueOf(user.getID())});
        db.close();
    }
}
