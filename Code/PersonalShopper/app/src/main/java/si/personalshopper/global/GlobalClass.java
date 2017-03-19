package si.personalshopper.global;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;

import si.personalshopper.data.User;
import si.personalshopper.database.DatabaseHandler;

/**
 * Created by Senna on 18-5-2016.
 */
public class GlobalClass extends Application {

    private User user;
    private DatabaseHandler handler;

    public User getUser() { return user; }

    public void setUser(User user) {
        this.user = user;
        ArrayList<User> oldUser = (ArrayList<User>) handler.getUserTable().getAll();
        handler.getUserTable().update(user);
        ArrayList<User> newUser = (ArrayList<User>) handler.getUserTable().getAll();
        if (oldUser.equals(newUser)) {
            Log.d("users", "equal");
        }
    }

    public DatabaseHandler getHandler() { return handler; }

    public void setHandler(DatabaseHandler handler) { this.handler = handler; }

}
