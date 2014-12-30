package hack.relationshit.http;

import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;

import java.util.Date;

/**
 * Created by dario on 12/30/14.
 */
public class Message {
    String userId;
    String direction;
    String phoneNumber;
    Integer messageLength;
    Long timestamp;

    public Message(Context context, Cursor cursor) {

        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        this.userId = manager.getDeviceId();

        String message     = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();
        String timestamp   = cursor.getString(cursor.getColumnIndexOrThrow("date")).toString();
        String type        = cursor.getString(cursor.getColumnIndexOrThrow("type")).toString();
        String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("address")).toString();

        this.phoneNumber = phoneNumber;
        this.messageLength = message.length();
        this.timestamp = Long.valueOf(timestamp);

        switch (Integer.parseInt(type)) {
            case 1:
                this.direction = "receive";
                break;
            case 2:
                this.direction = "send";
                break;
            case 3:
                this.direction = "draft";
                break;
        }
    }

}
