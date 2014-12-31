package hack.relationshit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

/**
 * Created by blyakhm on 12/30/2014.
 */
public class ShitApplication extends Application {

    private static final String classString = ShitApplication.class.getSimpleName();

    private boolean messagesLoaded = false;
    private boolean infoLoaded = false;

    private boolean overallLoadSuccess = true;

    private String belovedContactName;

    public void messagesHaveLoaded(Activity callerActivity, boolean isSuccess) {
        Log.d(classString, "messages have loaded");
        messagesLoaded = true;

        if (!isSuccess) {
            Log.d(classString, "setting overalLoadSuccess to false");
            overallLoadSuccess = false;
        }
        if (infoLoaded) {
            Log.d(classString, "messages and info loaded");
            if (!overallLoadSuccess) {
                Log.d(classString, "server action failed");
                // do popup
                AlertDialog dialog = createDialog(callerActivity);
                dialog.show();
            } else {
                Log.d(classString, "moving to main activity");
                // move to main activity
                Intent i = new Intent(callerActivity, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                callerActivity.finish();
            }
        } else {
            Log.d(classString, "messages have loaded, but info has not");
            // might have a race condition
        }
    }

    public void infoHasLoaded(Activity callerActivity, boolean isSuccess) {
        infoLoaded = true;
        if (!isSuccess) {
            Log.d(classString, "setting overalLoadSuccess to false");
            overallLoadSuccess = false;
        }

        if (messagesLoaded) {
            Log.d(classString, "messages and info have loaded");
            if (!overallLoadSuccess) {
                // do popup
                AlertDialog dialog = createDialog(callerActivity);
                dialog.show();
            } else {
                // move to main activity
                Intent i = new Intent(callerActivity, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                callerActivity.finish();
            }
        } else {
            Log.d(classString, "messages have not loaded");
            // show loading

            // might have a race condition
        }
    }

    private AlertDialog createDialog(final Activity activity) {

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("message")
                .setTitle("title");

        builder.setPositiveButton("okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent i = new Intent(activity, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                activity.finish();
            }
        });

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        return dialog;
    }

    public void resetState() {
        messagesLoaded = false;
        infoLoaded = false;

        overallLoadSuccess = true;

        belovedContactName = null;

        PhoneContact.reset();
    }

    public void setBelovedContactName(String belovedContactName) {
        this.belovedContactName = belovedContactName;
    }

    public String getBelovedContactName() {
        return this.belovedContactName;
    }

}
