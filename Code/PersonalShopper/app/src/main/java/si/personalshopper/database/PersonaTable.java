package si.personalshopper.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import si.personalshopper.data.Persona;
import si.personalshopper.data.Ratings;
import si.personalshopper.data.Shop;
import si.personalshopper.data.Visits;

/**
 * Created by Senna on 11-5-2016.
 */
public class PersonaTable implements Table {

    // Table Name
    private static final String TABLE_PERSONA = "personas";
    // Table Columns
    private static final String KEY_PERSONA_ID = "id";
    private static final String KEY_PERSONA_NAME = "name";
    private static final String KEY_PERSONA_POSTAGS = "postags";
    private static final String KEY_PERSONA_NEGTAGS = "negtags";
    private static final String KEY_PERSONA_VISITEDLIST = "visitedlist";
    private static final String KEY_PERSONA_RATINGLIST = "ratinglist";
    private static final String KEY_PERSONA_DESCRIPTION = "description";

    // The database handler
    private DatabaseHandler handler;

    private ArrayList<Shop> allShops;

    public PersonaTable(DatabaseHandler handler, ArrayList<Shop> allShops) {
        this.handler = handler;
        this.allShops = allShops;
    }

    public String[] getColumnNames() {
        return new String[] {KEY_PERSONA_ID, KEY_PERSONA_NAME, KEY_PERSONA_POSTAGS, KEY_PERSONA_NEGTAGS,
        KEY_PERSONA_VISITEDLIST, KEY_PERSONA_RATINGLIST, KEY_PERSONA_DESCRIPTION};
    }

    public String createTable() {
        String CREATE_PERSONA_TABLE = "CREATE TABLE " + TABLE_PERSONA + "("
                + KEY_PERSONA_ID + " INTEGER," + KEY_PERSONA_NAME + " TEXT,"
                + KEY_PERSONA_POSTAGS + " TEXT," + KEY_PERSONA_NEGTAGS + " TEXT,"
                + KEY_PERSONA_VISITEDLIST + " TEXT," + KEY_PERSONA_RATINGLIST + " TEXT,"
                + KEY_PERSONA_DESCRIPTION + " TEXT" + ")";
        return CREATE_PERSONA_TABLE;
    }

    @Override
    public void add(Object o) {
        Persona persona = (Persona) o;

        SQLiteDatabase db = handler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PERSONA_ID, persona.getID());
        values.put(KEY_PERSONA_NAME, persona.getName());
        values.put(KEY_PERSONA_POSTAGS, persona.getPosTagsString());
        values.put(KEY_PERSONA_NEGTAGS, persona.getNegTagsString());
        values.put(KEY_PERSONA_VISITEDLIST, persona.getVisitedList().getVisitedString());
        values.put(KEY_PERSONA_RATINGLIST, persona.getRatinglist().getRatingsString());
        values.put(KEY_PERSONA_DESCRIPTION, persona.getDescription());

        db.insert(TABLE_PERSONA, null, values);
        db.close();
    }

    @Override
    public Object getIndex(int id) {
        SQLiteDatabase db = handler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PERSONA, new String[] {
                KEY_PERSONA_ID, KEY_PERSONA_NAME, KEY_PERSONA_POSTAGS, KEY_PERSONA_NEGTAGS,
                KEY_PERSONA_VISITEDLIST, KEY_PERSONA_RATINGLIST, KEY_PERSONA_DESCRIPTION },
                KEY_PERSONA_ID + " =?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        String name = cursor.getString(1);
        String posTagsString = cursor.getString(2);
        ArrayList<String> posTags = stringToArray(posTagsString);
        String negTagsString = cursor.getString(3);
        ArrayList<String> negTags = stringToArray(negTagsString);
        String visListString = cursor.getString(4);
        Visits visitedList = new Visits(allShops);
        visitedList.setVisited(visListString);
        String ratListString = cursor.getString(5);
        Ratings ratingList = new Ratings(allShops);
        ratingList.setRatings(ratListString);
        double weight = 1;
        String description = cursor.getString(6);

        cursor.close();

        Persona persona = new Persona(id,name,posTags,negTags,visitedList,ratingList,weight,description);

        return persona;
    }

    private ArrayList<String> stringToArray(String string) {
        String[] stringSplit = string.split(";");
        ArrayList<String> arrayList = new ArrayList<>();
        for (String s : stringSplit) {
            arrayList.add(s);
        }
        return arrayList;
    }


    @Override
    public Object get(String s) {
        // Not necessary
        return null;
    }

    @Override
    public List getAll() {
        List<Persona> personaList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_PERSONA;

        SQLiteDatabase db = handler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String name = cursor.getString(1);
                String posTagsString = cursor.getString(2);
                ArrayList<String> posTags = stringToArray(posTagsString);
                String negTagsString = cursor.getString(3);
                ArrayList<String> negTags = stringToArray(negTagsString);
                String visListString = cursor.getString(4);
                Visits visitedList = new Visits(allShops);
                visitedList.setVisited(visListString);
                String ratListString = cursor.getString(5);
                Ratings ratingList = new Ratings(allShops);
                ratingList.setRatings(ratListString);
                double weight = 1;
                String description = cursor.getString(6);

                Persona persona = new Persona(id,name,posTags,negTags,visitedList,ratingList,weight,description);
                personaList.add(persona);
            }
            while (cursor.moveToNext());
            cursor.close();
        }

        return personaList;
    }

    @Override
    public int getSize() {
        String countQuery = "SELECT * FROM " + TABLE_PERSONA;
        SQLiteDatabase db = handler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    @Override
    public int update(Object o) {
        Persona persona = (Persona) o;

        SQLiteDatabase db = handler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PERSONA_ID, persona.getID());
        values.put(KEY_PERSONA_NAME, persona.getName());
        values.put(KEY_PERSONA_POSTAGS, persona.getPosTagsString());
        values.put(KEY_PERSONA_NEGTAGS, persona.getNegTagsString());
        values.put(KEY_PERSONA_VISITEDLIST, persona.getVisitedList().getVisitedString());
        values.put(KEY_PERSONA_RATINGLIST, persona.getRatinglist().getRatingsString());
        values.put(KEY_PERSONA_DESCRIPTION, persona.getDescription());
        return db.update(TABLE_PERSONA, values, KEY_PERSONA_ID + " =?",
                new String[] { String.valueOf(persona.getID()) });
    }

    @Override
    public void delete(Object o) {
        Persona persona = (Persona) o;
        SQLiteDatabase db = handler.getWritableDatabase();
        db.delete(TABLE_PERSONA, KEY_PERSONA_ID + " =?",
                new String[] { String.valueOf(persona.getID()) });
        db.close();
    }
}
