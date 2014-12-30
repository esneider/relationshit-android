package hack.relationshit.http;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import hack.relationshit.R;
import hack.relationshit.ShitApplication;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

/**
 * Created by dario on 12/30/14.
 */
public class ServerDAO {
    public static void sendMessageList(String IMEI, Message[] messages, final Activity activity) {

        Gson gson = new Gson();

        final ShitApplication shit = (ShitApplication) activity.getApplicationContext();

        String json = "{\"userId\": \"" + IMEI + "\", \"messageList\": " + gson.toJson(messages) + "}";

        StringEntity entity = null;
        try {
            entity = new StringEntity(json);
            entity.setContentType("application/json");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        RestClient.post("messageList", entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                shit.messagesHaveLoaded(activity, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                shit.messagesHaveLoaded(activity, false);
            }
        });
    }
}
