package hack.relationshit;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import hack.relationshit.utils.ImageHelper;


public class ContactDetailActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String contactName = extras.getString("contactName");
            ((TextView) findViewById(R.id.contact_name)).setText(contactName.split(" ")[0]);

            PhoneContact contact = PhoneContact.byName(this, contactName);
            Bitmap image = contact.getImage(this);
            ((ImageView) findViewById(R.id.detail_pic)).setImageBitmap(ImageHelper.getRoundedCornerBitmap(image, image.getHeight() / 2));

            ((TextView)findViewById(R.id.messages_num_me)).setText(Integer.toString(contact.getSentTo()));
            ((TextView)findViewById(R.id.messages_num_you)).setText(Integer.toString(contact.getReceivedFrom()));
            ((TextView)findViewById(R.id.length_num_me)).setText(Integer.toString(contact.averageSentLength()));
            ((TextView)findViewById(R.id.length_num_you)).setText(Integer.toString(contact.averageReceivedLength()));

            int total = contact.getSentTo() + contact.getReceivedFrom();

            if(total != 0) {
                int sentPercentage = (contact.getSentTo() * 100) / total;
                int receivedPercentage = (contact.getReceivedFrom() * 100) / total;
                int lengthMax = Math.max(contact.averageReceivedLength(), contact.averageSentLength());
                int percentMax = Math.max(sentPercentage, receivedPercentage);

                setHorizontalBar(normalize(sentPercentage, percentMax), R.id.messages_me);
                setHorizontalBar(normalize(receivedPercentage, percentMax), R.id.messages_you);
                setHorizontalBar(normalize(contact.averageSentLength(), lengthMax), R.id.length_me);
                setHorizontalBar(normalize(contact.averageReceivedLength(), lengthMax), R.id.length_you);
            }
        }
    }

    private void setHorizontalBar(double value, int id) {
        ViewGroup.LayoutParams layoutParams = findViewById(id).getLayoutParams();
        layoutParams.width = new Double(value).intValue();
        findViewById(id).setLayoutParams(layoutParams);
    }

    private int normalize(int num, int max) {
        return (int)((400.0 / max) * num);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_detail, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_contact_detail, container, false);
            return rootView;
        }
    }
}
