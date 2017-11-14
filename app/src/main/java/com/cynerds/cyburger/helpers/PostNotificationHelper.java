package com.cynerds.cyburger.helpers;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getCacheDir;

/**
 * Created by fabri on 04/11/2017.
 */

public class PostNotificationHelper {

    private static PostNotificationHelper mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    private PostNotificationHelper(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();


    }

    public static synchronized PostNotificationHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PostNotificationHelper(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }


    public static JsonObjectRequest buildRequest(String text, String title,  String clickAction, String topic) {

        JSONObject notification = new JSONObject();
        JSONObject jsonBody = new JSONObject();


        try {
            notification.put("title", title);
            notification.put("text", text);
            notification.put("click_action", clickAction);
            jsonBody.put("to", "/topics/" + topic);
            jsonBody.put("notification", notification);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();

        String url = "https://fcm.googleapis.com/fcm/send";

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url,
                jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                LogHelper.log("Notification posted!");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                LogHelper.log(error.getMessage());
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                byte[] data = Base64.decode(KEY, Base64.DEFAULT);
                String hex = "";
                try {
                    hex = new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String auth = "key="+hexToString(hex);

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Authorization", auth);

                return headers;
            }
        };

        return req;
    }


    public static void post(Context context, String title, String text, String clickAction, String topic) {


        JsonObjectRequest jsonObjectRequest = buildRequest(title, text, clickAction, topic);
        getInstance(context).addToRequestQueue(jsonObjectRequest);


    }


    public static void post(Context context, String title, String text, String topic) {


        post(context, title, text, "OPEN_ACTIVITY_1", topic);


    }

    public static String hexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        char[] hexData = hex.toCharArray();
        for (int count = 0; count < hexData.length - 1; count += 2) {
            int firstDigit = Character.digit(hexData[count], 16);
            int lastDigit = Character.digit(hexData[count + 1], 16);
            int decimal = firstDigit * 16 + lastDigit;
            sb.append((char) decimal);
        }
        return sb.toString();
    }

    private final static String KEY =
            "NDE0MTQxNDE3OTY1NmY0NDM4NGQzMDNhNDE1MDQxMzkzMTYyNDY0ZDY1MmQ0NTcwNDk2OTY1NjUzMDVhNjE0ZDc"
                    + "0NTM2ODQ1Mzc1MzU2MmQ0NDMyMzY3MTU0NTI3MTQ3NjQ2MjRhMzQ0NDUxNDU0ZDY1NDU3MzU1Njc2"
                    + "OTRjNjI3NDU2NzA0ZjY5NDgzMzZhNmQ1MzY1NmI2YzczNTk1OTVhNTE2ODY0NDY0NzU3NGE2MTRmNj"
                    + "kyZDUyNTI2NzczNzYzNzc4N2E2MzQyNzQ2YjMyNmU0ZDMyNjY1NjQxNzA0YjM1NzA1MjZlMzQ0NzMw"
                    + "NGU2NzMxNGYzNjJkNGQ0MzQ0NDY1MTZjNzc1NDM2NDk1NTU4N2E2ZDY4NTc1OTUzMzI3NzQxNjM1Y"
                    + "TUwNGU0YQ==";


}
