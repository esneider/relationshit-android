package hack.relationshit;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.util.Collection;
import java.util.HashMap;

public class PhoneContact {

    private static HashMap<String,String> NUMBERS_TO_NAMES = null;
    private static HashMap<String,String> NAMES_TO_PICS = null;

    public static String forNumber(Context context, String number) {

        if(NUMBERS_TO_NAMES == null) {
            populateContactNumbers(context);
        }

        return NUMBERS_TO_NAMES.containsKey(number) ? NUMBERS_TO_NAMES.get(number) : number;
    }

    public static Collection<String> getContactNames(Context context) {
        if(NUMBERS_TO_NAMES == null) {
            populateContactNumbers(context);
        }

        return NUMBERS_TO_NAMES.values();
    }

    public static void reset() {
        NUMBERS_TO_NAMES = null;
        NAMES_TO_PICS = null;
    }

    public static Bitmap getImage(Context context, String name) {
        if(NUMBERS_TO_NAMES == null) {
            populateContactNumbers(context);
        }

        if(NAMES_TO_PICS.get(name) != null) {
            try {
                return MediaStore.Images.Media
                        .getBitmap(context.getContentResolver(),
                                Uri.parse(NAMES_TO_PICS.get(name)));
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
        }
        return null;
    }

    private static void populateContactNumbers(Context context) {
        NUMBERS_TO_NAMES = new HashMap<>();
        NAMES_TO_PICS = new HashMap<>();
        ContentResolver cr = context.getContentResolver(); //Activity/Application android.content.Context
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if(cursor.moveToFirst())
        {
            do
            {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String image_uri = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                NAMES_TO_PICS.put(name, image_uri);

                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ id }, null);
                    while (pCur.moveToNext())
                    {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        NUMBERS_TO_NAMES.put(contactNumber, name);
                        break;
                    }
                    pCur.close();
                }

            } while (cursor.moveToNext()) ;
        }
        cursor.close();
    }
}
