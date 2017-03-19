/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.activities;

import java.util.ArrayList;
import java.util.List;
import si.personalshopper.MainRecommender;
import si.personalshopper.data.Persona;
import si.personalshopper.data.Ratings;
import si.personalshopper.data.Tag;
import si.personalshopper.data.User;
import si.personalshopper.database.DatabaseHandler;
import si.personalshopper.database.PersonaTable;
import si.personalshopper.database.TagTable;
import si.personalshopper.database.UserTable;
import si.personalshopper.global.GlobalClass;

public class PersonaSelectActivity {
    private ArrayList<Persona> allPersonas;
    private MainRecommender mainRecommender;
    private GlobalClass globalClass;

    public PersonaSelectActivity(MainRecommender mainRecommender) {
        this.mainRecommender = mainRecommender;
        this.globalClass = mainRecommender.getGlobalClass();
        DatabaseHandler handler = globalClass.getHandler();
        PersonaTable personaTable = handler.getPersonaTable();
        this.allPersonas = (ArrayList)personaTable.getAll();
    }

    public String getHTML() {
        String htmlString = "";
        for (Persona persona : this.allPersonas) {
            htmlString = htmlString + this.addPersonasHTML(persona);
        }
        return htmlString;
    }

    private String addPersonasHTML(Persona persona) {
        String personaString = "<div class=\"persona panel panel-default\" style=\"width:500px; max-width=800px onclick=\"selectPersona('' + " + persona.getName() + "'')\"> \n\t\t <div id=\"" + persona.getName() + "\" class=\"personaName panel-heading\" style=\"background-color:#3399ff; max-width:800px;\">";
        personaString = personaString + persona.getName();
        personaString = personaString + "</div>\n\t\t  <table class=\"table\">\n\t\t   <tr>\n\t\t    <th>\n\t\t     <b>Je houdt van:</b>\n\t\t    </th>\n\t\t    <th>\n\t\t     <b>Je houdt niet van:</b>\n\t\t    </th>\n\t\t   </tr>";
        personaString = personaString + this.addTagsHTML(persona);
        personaString = personaString + "\n</div>";
        return personaString;
    }

    private String addTagsHTML(Persona persona) {
        String personaTagString = "";
        ArrayList allTags = (ArrayList)globalClass.getHandler().getTagTable().getAll();
        ArrayList<String> posTags = persona.getPosTags();
        ArrayList<String> negTags = persona.getNegTags();
        ArrayList<String> posTagsString = this.getTags(posTags, allTags);
        ArrayList<String> negTagsString = this.getTags(negTags, allTags);
        int size = Math.max(posTagsString.size(), negTagsString.size());
        for (int i = 0; i < size; ++i) {
            personaTagString = personaTagString + "\n\t\t   <tr>\n\t\t    <th>\n\t\t     <i>";
            if (posTagsString.size() > i) {
                personaTagString = personaTagString + posTagsString.get(i);
            }
            personaTagString = personaTagString + "</i>\n\t\t    </th>\n\t\t    <th>\n\t\t     <i>";
            if (negTagsString.size() > i) {
                personaTagString = personaTagString + negTagsString.get(i);
            }
            personaTagString = personaTagString + "</i>\n\t\t    </th>\n\t\t   </tr>";
        }
        return personaTagString;
    }

    private ArrayList<String> getTags(ArrayList<String> binaryTags, ArrayList<Tag> allTags) {
        ArrayList<String> tags = new ArrayList<String>();
        for (int i = 0; i < binaryTags.size(); ++i) {
            if (!binaryTags.get(i).equals("1")) continue;
            tags.add(allTags.get(i).getTag());
        }
        return tags;
    }

    public void setPersona(String personaName, String sID) {
        int index;
        User user = globalClass.getHandler().getUserTable().get(sID);
        ArrayList<String> allPersonaNames = new ArrayList<String>();
        for (Persona persona2 : this.allPersonas) {
            allPersonaNames.add(persona2.getName());
        }
        if (personaName != "" && (index = allPersonaNames.indexOf(personaName)) >= 0) {
            Persona persona2;
            persona2 = this.allPersonas.get(index);
            ArrayList<Tag> allTags = (ArrayList)globalClass.getHandler().getTagTable().getAll();
            ArrayList<String> allTagNames = new ArrayList<String>();
            for (Tag tag : allTags) {
                allTagNames.add(tag.getTag());
            }
            user.setPersona(persona2);
            user.setTags(persona2.getPosTagsString(), persona2.getNegTagsString(), allTagNames);
            user.setRatingList(persona2.getRatinglist().getRatingsString());
            globalClass.setUser(user);
            mainRecommender.setGlobalClass(globalClass);
        }
    }
}

