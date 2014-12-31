package hack.relationshit.http;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import hack.relationshit.R;
import hack.relationshit.ShitApplication;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by dario on 12/30/14.
 */
public class ServerDAO {
    public static void sendMessageList(final String IMEI, Message[] messages, final Activity activity) {

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
                Log.e("post", responseBody.toString());
                getTopLists(IMEI, activity);
                shit.messagesHaveLoaded(activity, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                shit.messagesHaveLoaded(activity, false);
            }
        });
    }

    public static void getTopLists(String IMEI, final Activity activity) {

        final ShitApplication shit = (ShitApplication) activity.getApplicationContext();

        RestClient.get("topLists?IMEI=" + IMEI, null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray array) {
                Log.e("get SUCC", array.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable error) {
                Log.e("get FAIL", responseString);
            }
        });
    }

    public static void getContactsData(String IMEI, final Activity activity) {

        final ShitApplication shit = (ShitApplication) activity.getApplicationContext();

        RestClient.get("contactsData?IMEI=" + IMEI, null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray array) {
                Log.e("get SUCC", array.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable error) {
                Log.e("get FAIL", responseString);
            }
        });
    }
}
