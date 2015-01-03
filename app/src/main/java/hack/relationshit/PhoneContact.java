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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hack.relationshit.http.Message;
import hack.relationshit.utils.CustomPhoneNumberUtils;

public class PhoneContact {

    private static HashMap<String,PhoneContact> NUMBERS_TO_CONTACTS = null;
    private static HashMap<String,PhoneContact> NAMES_TO_CONTACTS = null;

    private String number;
    private String name;
    private String imageUri;
    private int score = 0;
    private int sentTo = 0;
    private int receivedFrom = 0;
    private long charsSentTo = 0;
    private long charsReceivedFrom = 0;

    private static PhoneContact byNumber(Context context, String number) {
        populateContactNumbers(context);

        if (NUMBERS_TO_CONTACTS.get(CustomPhoneNumberUtils.normalizeNumber(number)) != null) {
            return NUMBERS_TO_CONTACTS.get(CustomPhoneNumberUtils.normalizeNumber(number));
        } else {
            PhoneContact phoneContact = blankUser();
            phoneContact.number = number;
            phoneContact.name = number;
            NUMBERS_TO_CONTACTS.put(CustomPhoneNumberUtils.normalizeNumber(number), phoneContact);
            NAMES_TO_CONTACTS.put(number, phoneContact);
            return phoneContact;
        }
    }

    public static PhoneContact byName(Context context, String name) {
        populateContactNumbers(context);

        return NAMES_TO_CONTACTS.get(name);
    }

    private static PhoneContact blankUser() {
        return new PhoneContact();
    }

    public static List<PhoneContact> allContacts(Context context) {
        populateContactNumbers(context);

        return new ArrayList<>(NAMES_TO_CONTACTS.values());
    }

    public static void reset() {
        NUMBERS_TO_CONTACTS = null;
        NAMES_TO_CONTACTS = null;
    }

    private static void populateContactNumbers(Context context) {
        if(NUMBERS_TO_CONTACTS == null) {
            NUMBERS_TO_CONTACTS = new HashMap<>();
            NAMES_TO_CONTACTS = new HashMap<>();
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String image_uri = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                    String number = getContactNumber(cr, cursor);

                    PhoneContact phoneContact;
                    if(NAMES_TO_CONTACTS.containsKey(name)) {
                        phoneContact = NAMES_TO_CONTACTS.get(name);
                        if (image_uri != null)
                            phoneContact.imageUri = image_uri;

                        if(number != null)
                            phoneContact.number = number;

                    } else {
                        phoneContact = blankUser();
                        phoneContact.imageUri = image_uri;
                        phoneContact.name = name;
                        phoneContact.number = number;

                        NAMES_TO_CONTACTS.put(name, phoneContact);
                    }

                    if (phoneContact.getNumber() != null) {
                        NUMBERS_TO_CONTACTS.put(CustomPhoneNumberUtils.normalizeNumber(phoneContact.getNumber()), phoneContact);
                    }

                } while (cursor.moveToNext());
            }
            cursor.close();

            populateMessageNumbers(context);
        }
    }

    private static void populateMessageNumbers(Context context) {
        for (Message message : SMSes.getMessages(context)) {
            PhoneContact phoneContact = byNumber(context, message.getPhoneNumber());
            phoneContact.score++;
            if(message.getDirection().equals("send")) {
                phoneContact.sentTo++;
                phoneContact.charsSentTo += message.getMessageLength();
            } else if(message.getDirection().equals("receive")) {
                phoneContact.receivedFrom++;
                phoneContact.charsReceivedFrom += message.getMessageLength();
            }
        }
    }

    private static String getContactNumber(ContentResolver cr, Cursor cursor) {
        if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
        {
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ id }, null);
            while (pCur.moveToNext())
            {
                String number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                pCur.close();
                return number;
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

    public int getSentTo() {
        return sentTo;
    }

    public int averageSentLength() {
        return (sentTo > 0) ? ((int) charsSentTo / sentTo) : 0;
    }

    public int averageReceivedLength() {
        return (receivedFrom > 0) ? (int) charsReceivedFrom / receivedFrom : 0;
    }

    public int getReceivedFrom() {
        return receivedFrom;
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
