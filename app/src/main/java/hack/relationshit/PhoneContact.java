package hack.relationshit;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

public class PhoneContact {

    private static HashMap<String,PhoneContact> NUMBERS_TO_CONTACTS = null;
    private static HashMap<String,PhoneContact> NAMES_TO_CONTACTS = null;

    private String number;
    private String name;
    private String imageUri;
    private int score;

    public static PhoneContact byNumber(Context context, String number) {
        populateContactNumbers(context);

        if (NUMBERS_TO_CONTACTS.get(number) != null) {
            return NUMBERS_TO_CONTACTS.get(number);
        } else {
            PhoneContact phoneContact = blankUser();
            phoneContact.number = number;
            phoneContact.name = number;
            return phoneContact;
        }
    }

    public static PhoneContact byName(Context context, String name) {
        populateContactNumbers(context);

        if (NAMES_TO_CONTACTS.get(name) != null) {
            return NAMES_TO_CONTACTS.get(name);
        } else {
            PhoneContact phoneContact = blankUser();
            phoneContact.number = name;
            phoneContact.name = name;
            return phoneContact;
        }
    }

    private static PhoneContact blankUser() {
        PhoneContact phoneContact = new PhoneContact();
        phoneContact.score = new Random().nextInt(100);
        return phoneContact;
    }

    public static Collection<PhoneContact> allContacts(Context context) {
        populateContactNumbers(context);

        return NUMBERS_TO_CONTACTS.values();
    }

    public static void reset() {
        NUMBERS_TO_CONTACTS = null;
        NAMES_TO_CONTACTS = null;
    }

    private static void populateContactNumbers(Context context) {
        if(NUMBERS_TO_CONTACTS == null) {
            NUMBERS_TO_CONTACTS = new HashMap<>();
            NAMES_TO_CONTACTS = new HashMap<>();
            ContentResolver cr = context.getContentResolver(); //Activity/Application android.content.Context
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String image_uri = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                    String number = getContactNumber(cr, cursor);

                    PhoneContact phoneContact = blankUser();
                    phoneContact.imageUri = image_uri;
                    phoneContact.name = name;
                    phoneContact.number = number;

                    NAMES_TO_CONTACTS.put(name, phoneContact);
                    if (phoneContact.getNumber() != null) {
                        NUMBERS_TO_CONTACTS.put(phoneContact.getNumber(), phoneContact);
                    }

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    private static String getContactNumber(ContentResolver cr, Cursor cursor) {
        if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
        {
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ id }, null);
            while (pCur.moveToNext())
            {
                pCur.close();
                return pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
        }

        return null;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public int score() {
        return score;
    }

    public Bitmap getImage(Context context) {
        if(imageUri != null) {
            try {
                return MediaStore.Images.Media
                        .getBitmap(context.getContentResolver(),
                                Uri.parse(imageUri));
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
        }
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.unknown);
    }
}
