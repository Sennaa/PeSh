package si.personalshopper.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import si.personalshopper.R;
import si.personalshopper.data.Persona;
import si.personalshopper.data.Ratings;
import si.personalshopper.data.Tag;
import si.personalshopper.data.User;
import si.personalshopper.global.GlobalClass;

public class SettingsActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Get user
        final GlobalClass globalClass = (GlobalClass) getApplicationContext();
        User user = globalClass.getUser();

        // Set persona
        setPersona();
        // Set selected tags
        setSelectedTags();
        // Set time spinner
        setTime();
    }

    // Put persona name in field
    public void setPersona() {
        TextView personaButton = (TextView) findViewById(R.id.activity_settings_persona);
        // Get user
        final GlobalClass globalClass = (GlobalClass) getApplicationContext();
        User user = globalClass.getUser();
        Persona persona = user.getPersona();
        personaButton.setText(persona.getName());
        // If clicked on button, the persona can be changed. User is sent back to PersonaSelectActivity
        final Intent intent = new Intent(this, PersonaSelectActivity.class);
        personaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }

    // Put selected tags in field
    public void setSelectedTags() {
        // Get user
        final GlobalClass globalClass = (GlobalClass) getApplicationContext();
        User user = globalClass.getUser();
        final ArrayList<String> selectedTags = user.getExtraSelectedTags();
        int tagSize = selectedTags.size();
        TextView[] tagViews = new TextView[tagSize];
        Button[] tagDeleteButtons = new Button[tagSize];

        // Load field by id
        final LinearLayout tagField = (LinearLayout) findViewById(R.id.activity_settings_tagselection);

        for (int tag = 0 ; tag < tagSize ; tag++) {
            // Make View with text (tag-name)
            final TextView tagView = new TextView(this);
            tagView.setText(selectedTags.get(tag));
            tagView.setBackgroundColor(Color.WHITE);
            // Add View with tag-name
            tagField.addView(tagView);
            tagViews[tag] = tagView;
        }
    }

    public void setTime() {
        Spinner spinner = (Spinner) findViewById(R.id.activity_settings_timespinner);
        spinner.setOnItemSelectedListener(this);
        ArrayList<Integer> times = new ArrayList<>();
        for (Integer i = 0 ; i <= 240 ; i += 30) {
            times.add(i);
        }
        // Create an ArrayAdapter using the Integer array and a default spinner layout
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, times);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public void adaptTagSelection(View view) {
        // Get user
        final GlobalClass globalClass = (GlobalClass) getApplicationContext();
        User user = globalClass.getUser();
        // Delete selected tags
        user.setSelectedTags(new ArrayList<String>());
        // Start TagSelectionActivity
        Intent intent = new Intent(this, TagSelectionActivity.class);
        startActivity(intent);
    }

    // Start RouteCalculationActivity
    public void findRoute(View view) {
        // Get user
        final GlobalClass globalClass = (GlobalClass) getApplicationContext();
        User user = globalClass.getUser();
        if(user.getTimeBudget() > 0) {
            // set user ratings
            user = setUserRatings(user);

            ArrayList<User> oldUsers = (ArrayList<User>) globalClass.getHandler().getUserTable().getAll();
            globalClass.setUser(user);
            ArrayList<User> newUsers = (ArrayList<User>) globalClass.getHandler().getUserTable().getAll();
            Intent intent = new Intent(this, RouteCalculationActivity.class);
            startActivity(intent);
        }
        else {

            AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
            helpBuilder.setTitle("Missende informatie");
            helpBuilder.setMessage("Vul een tijdslimiet in!");
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

    public User setUserRatings(User currentUser) {
        final GlobalClass globalClass = (GlobalClass) getApplicationContext();
        ArrayList<Tag> allTags = (ArrayList<Tag>) globalClass.getHandler().getTagTable().getAll();

        // Add the extraSelectedTags from the user
        ArrayList<String> selectedTags = currentUser.getExtraSelectedTags();
        ArrayList<String> posTags = currentUser.getPosTags();
        ArrayList<String> negTags = currentUser.getNegTags();

        // Add selectedTags to posTags if posTags does not contain the tag already
        for (String tag : selectedTags) {
            if (!posTags.contains(tag)) {
                posTags.add(tag);
            }
        }

        // Set new user posTags
        currentUser.setPosTags(posTags);

        // Compute new user Ratings and set them to the user
        Ratings newRatings = currentUser.getRatingList().computeRatings(posTags, negTags);
        currentUser.setRatingList(newRatings.getRatingsString());

        // Return user
        return currentUser;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Get user
        final GlobalClass globalClass = (GlobalClass) getApplicationContext();
        User user = globalClass.getUser();
        Integer timeBudget = (Integer) parent.getItemAtPosition(position);
        user.setTimeBudget(timeBudget);
        Log.d("TimeBudget", Integer.toString(user.getTimeBudget()));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
