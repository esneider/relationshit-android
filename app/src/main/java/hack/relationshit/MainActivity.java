package hack.relationshit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.Random;
import hack.relationshit.utils.ImageHelper;


public class MainActivity extends FragmentActivity {

    private String[] actressArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.action_bar);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        actressArray = new String[]{PhoneContact.forNumber(this, "+61410738965"), "Anushka Sharma", "Deepika Padukone",
                "Jacqueline Fernandez", "Kareena Kapoor", "Katrina Kaif",
                "Parineeti Chopra", "Priyanka Chopra", "Shraddha Kapoor",
                "Sonakshi Sinha"};

        String[] selectionList = {"Top Friends", "Worst Friends", "Most Annoying"};

        setListView(actressArray);

        setupDropdown(selectionList);
    }

    private void setListView(final String[] listItems) {
        final Context that = this;
        ListView lv = (ListView) findViewById(R.id.main_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.item_name, listItems) {


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                Bitmap image = PhoneContact.getImage(that, listItems[position]);
                ((ImageView) view.findViewById(R.id.profile_pic)).setImageBitmap(ImageHelper.getRoundedCornerBitmap(image, image.getHeight() / 2));

                int progress = new Random().nextInt(100);

                ((TextView) view.findViewById(R.id.item_percent)).setText(Integer.toString(progress));
                ((ProgressBar) view.findViewById(R.id.progress_bar)).setMax(100);
                ((ProgressBar) view.findViewById(R.id.progress_bar)).setProgress(progress);
                if(progress < 50) {
                    ((ProgressBar) view.findViewById(R.id.progress_bar)).setProgressDrawable(that.getResources().getDrawable(R.drawable.progressbar_low));
                } else {
                    ((ProgressBar) view.findViewById(R.id.progress_bar)).setProgressDrawable(that.getResources().getDrawable(R.drawable.progressbar_high));
                }

                return view;
            }
        };
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, ContactDetail.class);
                startActivity(i);
                finish();
            }

        });
    }

    private void setupDropdown(String[] selectionList) {
        Spinner spinner = (Spinner) findViewById(R.id.list_selection);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this,
                R.layout.spinner_item, selectionList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setListView(actressArray);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
