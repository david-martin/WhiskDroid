package com.example.openwhiskclient;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class OpenWhiskClient {

    private String host;
    private String namespace;
    private String credentials;

    public OpenWhiskClient(String host, String namespace, String credentials) {
        this.host = host;
        this.namespace = namespace;
        this.credentials = credentials;
    }

    public void invoke(String action, JSONObject params, Response.Listener<JSONObject> listener, Response.ErrorListener error, Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = String.format("https://%s/api/v1/namespaces/%s/actions/%s?blocking=true&result=true", host, namespace, action);

        // Request a string response from the provided URL.
        JsonObjectRequest whiskCall = new JsonObjectRequest(Request.Method.POST, url, params, listener, error) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(whiskCall);
    }

    public static OpenWhiskClient fromJSON(JSONObject json) throws JSONException {
        String host = json.getString("host");
        String namespace = json.getString("namespace");
        String credentials = String.format("%s:%s", json.getString("username"), json.getString("password"));

        return new OpenWhiskClient(host, namespace, credentials);
    }

}
