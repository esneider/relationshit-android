package hack.relationshit;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hack.relationshit.http.Message;
import hack.relationshit.http.ServerDAO;


public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        String[] actressArray = {PhoneContact.forNumber(this, "+61410738965"), "Anushka Sharma", "Deepika Padukone",
                "Jacqueline Fernandez", "Kareena Kapoor", "Katrina Kaif",
                "Parineeti Chopra", "Priyanka Chopra", "Shraddha Kapoor",
                "Sonakshi Sinha"};

        String[] selectionList = {"Top Friends", "Worst Friends", "Most Annoying"};

        setListView(actressArray);

        setupDropdown(selectionList);
    }

    private void setListView(String[] listItems) {
        ListView lv = (ListView) findViewById(R.id.main_list);
        lv.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, R.id.item_name, listItems));
    }

    private void setupDropdown(String[] selectionList) {
        Spinner spinner = (Spinner) findViewById(R.id.list_selection);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this,
                R.layout.spinner_item, selectionList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    @Override
    public void onBackPressed() {
        final ShitApplication shit = (ShitApplication) this.getApplicationContext();
        shit.resetState();

        super.onBackPressed();
    }
}
