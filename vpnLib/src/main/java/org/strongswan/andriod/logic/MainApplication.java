

package org.strongswan.android.logic;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.one.hotspot.vpn.free.master.firebase.DatabaseManager;
import com.one.hotspot.vpn.free.master.manager.CacheManager;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pixplicity.easyprefs.library.Prefs;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.strongswan.android.security.LocalCertificateKeyStoreProvider;

import java.security.Security;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    private static Context mContext;
    public static SharedPreferences prefs;

    public static String BANNER_ADMOB="", NATIVE_ADMOB="", REWARD_ADMOB="", INTERSTITIAL_ADMOB="";

    static {
        Security.addProvider(new LocalCertificateKeyStoreProvider());
    }


    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            System.loadLibrary("strongswan");

            System.loadLibrary("tpmtss");
            System.loadLibrary("tncif");
            System.loadLibrary("tnccs");
            System.loadLibrary("imcv");
            System.loadLibrary("charon");
            System.loadLibrary("ipsec");
        }
        System.loadLibrary("androidbridge");
    }

    public static Context getContext() {
        return MainApplication.mContext;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        MainApplication.mContext = getApplicationContext();
        CacheManager.INSTANCE.setContext(getApplicationContext());
        prefs = PreferenceManager.getDefaultSharedPreferences(this);



        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, "http://snapvpn.myazainfo.com/api.php?action=get_ad_ids", null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            BANNER_ADMOB = response.getString("admob_banner");
                            INTERSTITIAL_ADMOB = response.getString("admob_inter");
                            NATIVE_ADMOB = response.getString("admob_native");




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Exception", error.toString());
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);


        MobileAds.initialize(this);

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getContext().getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();





    }
}
