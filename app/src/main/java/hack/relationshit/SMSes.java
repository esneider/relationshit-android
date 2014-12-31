package hack.relationshit;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import hack.relationshit.http.Message;

public class SMSes {

    private static List<Message> smses = null;

    private static void populateMessages(Context context) {
        if(smses == null) {
            Uri uri = Uri.parse("content://sms");
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            List<Message> messages = new ArrayList<>();

            if (cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    messages.add(new Message(cursor));
                    cursor.moveToNext();
                }
            }

            cursor.close();
            smses = messages;
        }
    }

    public static Message[] getMessages(Context context) {
        populateMessages(context);

        return smses.toArray(new Message[smses.size()]);
    }

    public static List<Message> forNumber(Context context, String number) {
        populateMessages(context);
        ArrayList<Message> messages = new ArrayList<>();

        for(Message message : smses) {
            if(message.getPhoneNumber() != null && message.getPhoneNumber().equals(number)) {
                messages.add(message);
            }
        }

        return messages;
    }
}
