package si.personalshopper.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

import si.personalshopper.R;
import si.personalshopper.data.SimilarityTags;
import si.personalshopper.data.User;
import si.personalshopper.database.DatabaseHandler;
import si.personalshopper.global.GlobalClass;

public class TagSelectionActivity extends ActionBarActivity {

    private ArrayList<String> allTags;
    private TableLayout tagTable;
    private ArrayList<String> selectedTags;
    private SimilarityTags similarityTags;
    private DatabaseHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_selection);
        final GlobalClass globalClass = (GlobalClass) getApplicationContext();

        this.handler = globalClass.getHandler();

        // Get all tags
        similarityTags = new SimilarityTags(handler);
        allTags = similarityTags.getTags();

        selectedTags = new ArrayList<>();
        tagTable = (TableLayout) findViewById(R.id.activity_tagselection_table);
        insertTags();
    }

    private void insertTags() {
        String[] tags = new String[allTags.size()];

        for (int i = 0 ; i < allTags.size() ; i++ ) {
            tags[i] = allTags.get(i);
            TextView tagName = new TextView(this);
            tagName.setText(tags[i]);
            tagName.setBackgroundColor(Color.WHITE);

            tagName.setOnClickListener(new View.OnClickListener() {
                // Make sure only one persona can be selected and thus one persona has a darker backgroundcolor
                @Override
                public void onClick(View view) {
                    String tag = (String) ((TextView) view).getText();
                    // If it contains the tag, unselect it
                    if (selectedTags.contains(tag)) {
                        selectedTags.remove(tag);
                        view.setBackgroundColor(Color.WHITE);
                    }
                    // Else, select the tag
                    else {
                        selectedTags.add(tag);
                        view.setBackgroundColor(Color.parseColor("#468499"));
                    }
                }
            });

            // add LinearLayout to TableLayout
            TableLayout TL = (TableLayout) findViewById(R.id.activity_tagselection_table);
            TL.addView(tagName);
        }
    }

    public void confirmTagSelection(View view) {
        final GlobalClass globalClass = (GlobalClass) getApplicationContext();
        User user = globalClass.getUser();
        user.setSelectedTags(selectedTags);
        globalClass.setUser(user);
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tag_selection, menu);
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
