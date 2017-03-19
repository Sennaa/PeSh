package si.personalshopper.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

import si.personalshopper.R;
import si.personalshopper.data.Persona;
import si.personalshopper.data.Ratings;
import si.personalshopper.data.Tag;
import si.personalshopper.data.User;
import si.personalshopper.database.DatabaseHandler;
import si.personalshopper.database.PersonaTable;
import si.personalshopper.global.GlobalClass;

public class PersonaSelectActivity extends ActionBarActivity {

    private User user;
    private Persona selectedPersona;
    private ArrayList<Persona> allPersonas;

    private boolean personaSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persona_select);

        // At the beginning, no persona is selected
        personaSelected = false;

        // Get user
        final GlobalClass globalClass = (GlobalClass) getApplicationContext();
        user = globalClass.getUser();

        DatabaseHandler handler = globalClass.getHandler();
        PersonaTable personaTable = handler.getPersonaTable();
        allPersonas = (ArrayList<Persona>) personaTable.getAll();

        createTextViews();
    }

    private void createTextViews() {
        // Create textViewsName dynamically
        final ArrayList<Persona> personas = allPersonas;
        int personaSize = personas.size();

        // Per persona, add a linear layout consisting of the name of the persona and its description
        LinearLayout[] layouts = new LinearLayout[personaSize];

        final TextView[] descriptions = new TextView[personaSize];

        // Do for each persona
        for( int persona = 0; persona < personaSize; persona++ ) {
            final int index = persona;
            // create LinearLayout
            TableLayout rowLinearLayout = new TableLayout(this);

            // set properties
            rowLinearLayout.setOrientation(LinearLayout.HORIZONTAL); // Set horizontal orientation

            // make TextView for name
            TextView name = new TextView(this);
            name.setText(personas.get(persona).getName());
            name.setBackgroundColor(Color.parseColor("#ffffd5b5")); // Set color

            // make TextView for description
            final TextView description = new TextView(this);
            description.setText(personas.get(persona).getDescription());
            description.setBackgroundColor(Color.parseColor("#ffffffff")); // Set color

            // add name and description to LinearLayout
            rowLinearLayout.addView(name);
            rowLinearLayout.addView(description);

            // make a button of the layout element
            rowLinearLayout.setOnClickListener(new View.OnClickListener() {
                // Make sure only one persona can be selected and thus one persona has a darker backgroundcolor
                @Override
                public void onClick(View v) {
                    selectedPersona = selectPersona(personas, personas.get(index).getName());
                    if (personaSelected == true) {
                        for (TextView d : descriptions) {
                            d.setBackgroundColor(Color.parseColor("#ffffffff"));
                        }
                        description.setBackgroundColor(Color.parseColor("#468499"));
                    }
                    else {
                        description.setBackgroundColor(Color.parseColor("#468499"));
                        personaSelected = true;

                    }
                }
            });

            // add LinearLayout to TableLayout
            TableLayout TL = (TableLayout) findViewById(R.id.activity_persona_select_table_layout);
            TL.addView(rowLinearLayout);

            // save a reference to the LinearLayout for later
            layouts[persona] = rowLinearLayout;
            descriptions[persona] = description;
        }
    }

    private Persona selectPersona(ArrayList<Persona> personas, String name) {
        for (Persona persona : personas) {
            if (persona.getName().equals(name)) {
                selectedPersona = persona;
            }
        }
        return selectedPersona;
    }

    public User setNegTags(User currentUser, Persona userPersona) {
        final GlobalClass globalClass = (GlobalClass) getApplicationContext();
        ArrayList<Tag> allTags = (ArrayList<Tag>) globalClass.getHandler().getTagTable().getAll();
        ArrayList<String> allTagNames = new ArrayList<>();
        for (Tag tag : allTags) {
            allTagNames.add(tag.getTag());
        }
        currentUser.setNegTags(userPersona.getNegTagsString(), allTagNames);
        return currentUser;
    }

    public User setPosTags(User currentUser, Persona userPersona) {
        final GlobalClass globalClass = (GlobalClass) getApplicationContext();
        ArrayList<Tag> allTags = (ArrayList<Tag>) globalClass.getHandler().getTagTable().getAll();
        ArrayList<String> allTagNames = new ArrayList<>();
        for (Tag tag : allTags) {
            allTagNames.add(tag.getTag());
        }
        currentUser.setPosTags(userPersona.getPosTagsString(), allTagNames);
        return currentUser;
    }

    public void confirmPersona(View view) {
        if (!(selectedPersona == null)) {
            user.setPersona(selectedPersona);

            // Set ratings from persona
            // First, set posTags and negTags
            user = setPosTags(user, selectedPersona);
            user = setNegTags(user, selectedPersona);

            // Then, set Ratings
            Ratings personaRatings = selectedPersona.getRatinglist();
            user.setRatingList(personaRatings.getRatingsString());

            final GlobalClass globalClass = (GlobalClass) getApplicationContext();
            globalClass.setUser(user);

            // If preferences are not selected, go to TagSelectionActivity
            if (user.getExtraSelectedTags().isEmpty()) {
                Intent intent = new Intent(PersonaSelectActivity.this, TagSelectionActivity.class);
                startActivity(intent);
            }
            // Else, go to SettingsActivity
            else {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            }
        }
        else {

            AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
            helpBuilder.setTitle("Missende informatie");
            helpBuilder.setMessage("Kies een type gebruiker!");
            helpBuilder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                        }
                    });

            // Remember, create doesn't show the dialog
            AlertDialog helpDialog = helpBuilder.create();
            helpDialog.show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_persona_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
