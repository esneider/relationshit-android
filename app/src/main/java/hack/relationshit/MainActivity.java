package hack.relationshit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import hack.relationshit.utils.ImageHelper;

import static java.util.Arrays.asList;


public class MainActivity extends FragmentActivity {

    private List<PhoneContact> contacts;
    private static final String LOVE_OPTION = "Is It True Love?";

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

        contacts = PhoneContact.allContacts(this);

        Collections.sort(contacts, new Comparator<PhoneContact>() {
            @Override
            public int compare(PhoneContact lhs, PhoneContact rhs) {
                return rhs.score() - lhs.score();
            }
        });

        List<String> selectionList = new ArrayList(asList("Top Friends", "Worst Friends", "Most Annoying"));
        if(((ShitApplication) getApplication()).getBelovedContactName() != null) {
            selectionList.add(LOVE_OPTION);
        }

        setListView(contacts);

        setupDropdown(selectionList.toArray(new String[selectionList.size()]));
    }

    private void setListView(final List<PhoneContact> contacts) {
        final String[] listItems = new String[contacts.size()];
        for(int i = 0; i < contacts.size(); i++) {
            listItems[i] = contacts.get(i).getName();
        }

        final Context that = this;
        ListView lv = (ListView) findViewById(R.id.main_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.item_name, listItems) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                Bitmap image = PhoneContact.byName(that, listItems[position]).getImage(that);
                ((ImageView) view.findViewById(R.id.profile_pic)).setImageBitmap(ImageHelper.getRoundedCornerBitmap(image, image.getHeight() / 2));

                int progress = contacts.get(position).score();
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
//                finish();
            }

        });
    }

    private void setLoveView() {
        ListView listView = (ListView) findViewById(R.id.main_list);
        RelativeLayout loveView = (RelativeLayout) findViewById(R.id.love_view);
        listView.setVisibility(View.INVISIBLE);
        loveView.setVisibility(View.VISIBLE);

        String belovedContactName = ((ShitApplication) getApplication()).getBelovedContactName();
        PhoneContact beloved = PhoneContact.byName(this, belovedContactName);
        ((ImageView)findViewById(R.id.loved_pic)).setImageBitmap(ImageHelper.getRoundedCornerBitmap(beloved.getImage(this), beloved.getImage(this).getHeight() / 2));
        String desc = "";

        double sentPercent = ((beloved.getSentTo() + 200) * 100 ) / (beloved.getSentTo() + beloved.getReceivedFrom());
        desc = "Your have sent "+ new DecimalFormat("#.#").format(sentPercent) +"% of all texts to your beloved which means ";

        if(sentPercent < 40) {
            ((TextView) findViewById(R.id.clingy)).setText("THEY ARE CLINGY");
        } else if (sentPercent < 60) {
            ((TextView) findViewById(R.id.clingy)).setText("HEALTHY RELATIONSHIP");
        } else {
            ((TextView) findViewById(R.id.clingy)).setText("YOU ARE CLINGY");
        }

        ((TextView) findViewById(R.id.cling_desc)).setText(desc);


        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/homework.TTF");
        ((TextView)findViewById(R.id.clingy)).setTypeface(type);
    }

    private void hideLoveView() {
        ListView listView = (ListView) findViewById(R.id.main_list);
        RelativeLayout loveView = (RelativeLayout) findViewById(R.id.love_view);
        listView.setVisibility(View.VISIBLE);
        loveView.setVisibility(View.INVISIBLE);
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
                if (((TextView)view.findViewById(R.id.dropdown_selection)).getText().equals(LOVE_OPTION)) {
                    setLoveView();
                } else {
                    hideLoveView();
                    setListView(contacts);
                }
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
