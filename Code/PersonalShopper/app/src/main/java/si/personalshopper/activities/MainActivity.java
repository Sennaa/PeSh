package si.personalshopper.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

import si.personalshopper.R;
import si.personalshopper.data.User;
import si.personalshopper.database.DatabaseHandler;
import si.personalshopper.global.GlobalClass;

public class MainActivity extends AppCompatActivity {

    private static final long SPLASH_TIME_OUT = 3000;
    private SharedPreferences settings;
    private User user;

    // Initialization for identification user
    private static String sID = null;
    private static final String INSTALLATION = "INSTALLATION";

    // Stuff with database //
    // Database
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getSharedPreferences("si.personalshopper", MODE_PRIVATE);

        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
        finish();
        /*
        // Database set-up
        databaseHandler = new DatabaseHandler(this);

        // Set user
        setGlobals();

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                // If first run, start PersonaChoiceActivity
                // If persona is not selected, go to PersonaChoiceActivity, otherwise to TagSelectionActivity
                if (databaseHandler.getUserTable().get(sID).getPersona().isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, PersonaChoiceActivity.class);
                    startActivity(intent);
                }
                // Else if preferences are not selected, go to TagSelectionActivity
                else if(databaseHandler.getUserTable().get(sID).getExtraSelectedTags().isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, TagSelectionActivity.class);
                    startActivity(intent);
                }
                // Else, start SettingsActivity
                else {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
        */
    }

    // set globals (user & databasehandler)
    public void setGlobals() {
        sID = id(this);
        while (sID==null) {
            try {
                wait(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        User user = databaseHandler.getUser(sID);
        final GlobalClass globalClass = (GlobalClass) getApplicationContext();
        globalClass.setHandler(databaseHandler);
        globalClass.setUser(user);
    }

    public synchronized static String id(Context context) {
        if (sID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists()) {
                    writeInstallationFile(installation);
                }
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
