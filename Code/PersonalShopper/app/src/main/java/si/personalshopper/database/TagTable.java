package si.personalshopper.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import si.personalshopper.data.SimilarityTags;
import si.personalshopper.data.Tag;
import si.personalshopper.data.User;

/**
 * Created by Senna on 12-5-2016.
 */
public class TagTable implements Table {

    // Table Name
    private static final String TABLE_TAG = "tags";
    // Table Columns
    private static final String KEY_TAG_NAME = "name";
    private static final String KEY_TAG_SIMILARITIES = "tagsimilarities";

    private DatabaseHandler handler;

    public TagTable(DatabaseHandler handler) {
        this.handler = handler;
    }

    public String[] getColumnNames() {
        return new String[] {KEY_TAG_NAME, KEY_TAG_SIMILARITIES};
    }

    public String createTable() {
        String CREATE_TAG_TABLE = "CREATE TABLE " + TABLE_TAG + "("
                + KEY_TAG_NAME + " TEXT," + KEY_TAG_SIMILARITIES + " TEXT"
                + ")";
        return CREATE_TAG_TABLE;
    }

    @Override
    public void add(Object o) {
        // Change Object to Tag class
        Tag tag = (Tag) o;

        SQLiteDatabase db = handler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TAG_NAME, tag.getTag());
        values.put(KEY_TAG_SIMILARITIES, tag.getSimilaritiesString());

        db.insert(TABLE_TAG, null, values);
        db.close();
    }

    @Override
    public Object getIndex(int id) {

        return null;
    }

    @Override
    public Object get(String tagName) {
        SQLiteDatabase db = handler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TAG, new String[] { KEY_TAG_NAME, KEY_TAG_SIMILARITIES },
                KEY_TAG_NAME + " =?", new String[] {tagName}, null, null, null);
        // Make new tag (don't add it to SimilarityTags since it already exists)
        String similarities = cursor.getString(1);
        double[] tagSimilarities = getTagSimilaritiesFromString(similarities);
        cursor.close();
        return new Tag(tagName, tagSimilarities);
    }

    private double[] getTagSimilaritiesFromString(String simString) {
        String[] simSplit = simString.split("; ");
        double[] similarities = new double[simSplit.length];
        for (int sim = 0 ; sim < simSplit.length ; sim++) {
            similarities[sim] = Double.valueOf(simSplit[sim]);
        }
        return similarities;
    }

    @Override
    public List getAll() {
        List<Tag> tagList = new ArrayList<>();
        // SELECT All Query
        String selectQuery = "SELECT * FROM " + TABLE_TAG;

        SQLiteDatabase db = handler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String tagName = cursor.getString(0);
                double[] similarites = getSimilaritiesFromString(cursor.getString(1));
                Tag tag = new Tag(tagName, similarites);
                // Add tag to list
                tagList.add(tag);
            }
            while (cursor.moveToNext());
            cursor.close();
        }

        return tagList;
    }

    private double[] getSimilaritiesFromString(String similaritiesString) {
        String[] simSplit = similaritiesString.split("; ");
        double[] similarities = new double[simSplit.length];
        for (int sim = 1 ; sim < simSplit.length ; sim++) { // Sim = 1 because there is a header
            similarities[sim] = Double.valueOf(simSplit[sim]);
        }
        return similarities;
    }

    @Override
    public int getSize() {
        String countQuery = "SELECT * FROM " + TABLE_TAG;
        SQLiteDatabase db = handler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    @Override
    public int update(Object o) {
        Tag tag = (Tag) o;
        SQLiteDatabase db = handler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TAG_NAME, tag.getTag());
        values.put(KEY_TAG_SIMILARITIES, tag.getSimilaritiesString());

        return db.update(TABLE_TAG, values, KEY_TAG_NAME + " = ?",
                new String[] {tag.getTag()});
    }

    @Override
    public void delete(Object o) {
        Tag tag = (Tag) o;
        SQLiteDatabase db = handler.getWritableDatabase();
        db.delete(TABLE_TAG, KEY_TAG_NAME + " = ?",
                new String[] { tag.getTag() });
        db.close();
    }
}
