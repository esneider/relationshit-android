package hack.relationshit.http;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import hack.relationshit.R;
import hack.relationshit.ShitApplication;

/**
 * Created by dario on 12/30/14.
 */
public class ServerDAO {
    public static void sendMessages(Message[] messages, final Activity activity) {

        Gson gson = new Gson();
        RequestParams params = new RequestParams();
        params.put("messages", gson.toJson(messages));

        final ShitApplication shit = (ShitApplication) activity.getApplicationContext();

        RestClient.post("messages", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // TODO process server data

                shit.messagesHaveLoaded(activity, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                shit.messagesHaveLoaded(activity, false);
            }
        });
    }
}
