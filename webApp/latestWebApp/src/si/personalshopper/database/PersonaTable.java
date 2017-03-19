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
import si.personalshopper.data.Visits;
import si.personalshopper.database.DatabaseHandler;
import si.personalshopper.database.Table;

public class PersonaTable
implements Table {
    private static final String TABLE_PERSONA = "personas";
    private static final String KEY_PERSONA_ID = "id";
    private static final String KEY_PERSONA_NAME = "name";
    private static final String KEY_PERSONA_POSTAGS = "postags";
    private static final String KEY_PERSONA_NEGTAGS = "negtags";
    private static final String KEY_PERSONA_VISITEDLIST = "visitedlist";
    private static final String KEY_PERSONA_RATINGLIST = "ratinglist";
    private static final String KEY_PERSONA_DESCRIPTION = "description";
    private DatabaseHandler handler;
    private Connection c;
    private Statement stmt;
    private ArrayList<Shop> allShops;

    public PersonaTable(DatabaseHandler handler, ArrayList<Shop> allShops) {
        this.handler = handler;
        this.c = handler.getConnection();
        this.stmt = handler.getStatement();
        this.allShops = allShops;
    }

    public String[] getColumnNames() {
        return new String[]{"id", "name", "postags", "negtags", "visitedlist", "ratinglist", "description"};
    }

    public String createTable() {
        String CREATE_PERSONA_TABLE = "CREATE TABLE personas(id INTEGER,name TEXT,postags TEXT,negtags TEXT,visitedlist TEXT,ratinglist TEXT,description TEXT)";
        return CREATE_PERSONA_TABLE;
    }

    public void insert(Object o) {
        Persona persona = (Persona)o;
        String columnString = "";
        for (String column : this.getColumnNames()) {
            columnString = columnString + column + ",";
        }
        columnString = columnString.substring(0, columnString.length() - 1);
        String sql = "INSERT INTO personas (" + columnString + ") VALUES (";
        sql = sql + persona.getID();
        sql = sql + "'" + persona.getName() + "',";
        sql = sql + "'" + persona.getPosTagsString() + "',";
        sql = sql + "'" + persona.getNegTagsString() + "',";
        sql = sql + "'" + persona.getVisitedList().getVisitedString() + "',";
        sql = sql + "'" + persona.getRatinglist().getRatingsString() + "',";
        sql = sql + "'" + persona.getDescription() + "',";
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
        Persona persona = new Persona();
        try {
            this.stmt = this.c.createStatement();
            ResultSet rs = this.stmt.executeQuery("SELECT * FROM personas WHERE id = " + id + ";");
            while (rs.next()) {
                int personaID = rs.getInt("id");
                String personaName = rs.getString("name");
                String personaPosTagsString = rs.getString("postags");
                ArrayList<String> personaPosTags = this.stringToArray(personaPosTagsString);
                String personaNegTagsString = rs.getString("negtags");
                ArrayList<String> personaNegTags = this.stringToArray(personaNegTagsString);
                String personaVisitedString = rs.getString("visitedlist");
                Visits personaVisited = new Visits(this.allShops);
                personaVisited.setVisited(personaVisitedString);
                String personaRatingsString = rs.getString("ratinglist");
                Ratings personaRatings = new Ratings(this.allShops);
                personaRatings.setRatings(personaRatingsString);
                double weight = 1.0;
                String personaDescription = rs.getString("description");
                persona = new Persona(personaID, personaName, personaPosTags, personaNegTags, personaVisited, personaRatings, weight, personaDescription);
            }
            rs.close();
            this.stmt.close();
        }
        catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return persona;
    }

    private ArrayList<String> stringToArray(String string) {
        String[] stringSplit = string.split(";");
        ArrayList<String> arrayList = new ArrayList<String>();
        for (String s : stringSplit) {
            arrayList.add(s);
        }
        return arrayList;
    }

    public Object get(String s) {
        return null;
    }

    public List getAll() {
        ArrayList<Persona> personaList = new ArrayList<Persona>();
        try {
            System.out.println("Get all personas: " + new File(".").getCanonicalPath());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.stmt = this.c.createStatement();
            ResultSet rs = this.stmt.executeQuery("SELECT * FROM personas");
            while (rs.next()) {
                int personaID = rs.getInt("id");
                String personaName = rs.getString("name");
                String personaPosTagsString = rs.getString("postags");
                ArrayList<String> personaPosTags = this.stringToArray(personaPosTagsString);
                String personaNegTagsString = rs.getString("negtags");
                ArrayList<String> personaNegTags = this.stringToArray(personaNegTagsString);
                String personaVisitedString = rs.getString("visitedlist");
                Visits personaVisited = new Visits(this.allShops);
                personaVisited.setVisited(personaVisitedString);
                String personaRatingsString = rs.getString("ratinglist");
                Ratings personaRatings = new Ratings(this.allShops);
                personaRatings.setRatings(personaRatingsString);
                double weight = 1.0;
                String personaDescription = rs.getString("description");
                personaList.add(new Persona(personaID, personaName, personaPosTags, personaNegTags, personaVisited, personaRatings, weight, personaDescription));
            }
            rs.close();
            this.stmt.close();
        }
        catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return personaList;
    }

    @Override
    public int getSize() {
        int size = 0;
        try {
            this.stmt = this.c.createStatement();
            ResultSet rs = this.stmt.executeQuery("SELECT * FROM personas");
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
        Persona persona = (Persona)o;
        try {
            this.stmt = this.c.createStatement();
            int personaID = persona.getID();
            String personaName = persona.getName();
            String personaPosTags = persona.getPosTagsString();
            String personaNegTags = persona.getNegTagsString();
            String personaVisited = persona.getVisitedList().getVisitedString();
            String personaRatings = persona.getRatinglist().getRatingsString();
            String personaDescriptions = persona.getDescription();
            String sql = "UPDATE personas set id = " + personaID + "," + "name" + " = '" + personaName + "'," + "postags" + " = '" + personaPosTags + "'," + "negtags" + " = '" + personaNegTags + "'," + "visitedlist" + " = '" + personaVisited + "'," + "ratinglist" + " = '" + personaRatings + "'," + "description" + " = '" + personaDescriptions + "' WHERE " + "id" + " = " + personaID + ";";
            this.stmt.executeUpdate(sql);
            this.c.commit();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Object o) {
        Persona persona = (Persona)o;
        try {
            this.stmt = this.c.createStatement();
            String sql = "DELETE from personas WHERE name = " + persona.getName() + ";";
            this.stmt.executeUpdate(sql);
            this.c.commit();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

