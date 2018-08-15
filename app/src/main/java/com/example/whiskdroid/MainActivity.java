package com.example.whiskdroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.openwhiskclient.OpenWhiskAction;
import com.example.openwhiskclient.OpenWhiskClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private static final String OPENWHISK_URL = "openwhisk-openwhisk.apps.boston.openshiftworkshop.com";
    private static final String OPENWHISK_ACTION = "testaction";
    private static final String OPENWHISK_AUTH = "23bc46b1-71f6-4ed5-8c54-816aa4f8c502:jU16oYksc7pPhDC0HJEtsrBnGD0hOuJUJPg0MaPnKnhR3CPILLCYbjdYhEKrLyjR";
    private static final String OPENWHISK_NAMESPACE = "_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NukeSSLCerts.nuke();

        setContentView(R.layout.activity_main);

        Button button= (Button) findViewById(R.id.call_action);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        final TextView mTextView = (TextView) findViewById(R.id.action_result);

        OpenWhiskClient client = new OpenWhiskClient(OPENWHISK_URL, OPENWHISK_NAMESPACE, OPENWHISK_AUTH);

        JSONObject params = new JSONObject();
        try {
            params.put("name", "World");
            params.put("place", "Boston");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        OpenWhiskAction testaction = new OpenWhiskAction() {
            @Override
            public String getName() {
                return "testaction";
            }
        };

        client.invoke(testaction, params, response -> {
                    // Display the first 500 characters of the response string.
                    try {
                        mTextView.setText("Response is: "+ response.toString(2));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    mTextView.setText("That didn't work!");
                    Log.e("app", "error calling openwhisk", error);
                }, this.getApplicationContext());

    }

    public static class NukeSSLCerts {
        protected static final String TAG = "NukeSSLCerts";

        public static void nuke() {
            try {
                TrustManager[] trustAllCerts = new TrustManager[] {
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                                return myTrustedAnchors;
                            }

                            @Override
                            public void checkClientTrusted(X509Certificate[] certs, String authType) {}

                            @Override
                            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                        }
                };

                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }
    }

}
