package hack.relationshit.http;

import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by dario on 12/30/14.
 */
public class ServerDAO {
    public static void sendMessages(Message[] messages) {

        Gson gson = new Gson();
        RequestParams params = new RequestParams();
        params.put("messages", gson.toJson(messages));

        RestClient.post("messages", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // TODO
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // TODO
            }
        });
    }
}
