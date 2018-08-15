package com.example.whiskdroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.openwhiskclient.OpenWhiskClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private OpenWhiskClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            JSONObject openwhiskConfig = parseJSONConfig();
            client = OpenWhiskClient.fromJSON(openwhiskConfig);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        NukeSSLCerts.nuke();

        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.call_action);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final TextView mTextView = findViewById(R.id.action_result);

        JSONObject params = new JSONObject();
        try {
            params.put("name", "World");
            params.put("place", "Boston");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Log.d("app", "Calling action");
        client.invoke("testaction", params, response -> {
            // Display the first 500 characters of the response string.
            try {
                mTextView.setText(response.getString("msg"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            mTextView.setText("That didn't work!");
            Log.e("app", "error calling openwhisk", error);
        }, this.getApplicationContext());

    }

    public JSONObject parseJSONConfig() throws IOException, JSONException {
        String jsonStr = null;
        JSONObject json = null;

        InputStream inputStream = getAssets().open("openwhisk.json");
        int sizeOfJSONFile = inputStream.available();

        byte[] bytes = new byte[sizeOfJSONFile];
        inputStream.read(bytes);
        inputStream.close();

        jsonStr = new String(bytes, "UTF-8");
        return new JSONObject(jsonStr);
    }

    public static class NukeSSLCerts {
        protected static final String TAG = "NukeSSLCerts";

        public static void nuke() {
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                                return myTrustedAnchors;
                            }

                            @Override
                            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            }
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
