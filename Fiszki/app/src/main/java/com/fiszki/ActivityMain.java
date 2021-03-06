package com.fiszki;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fiszki.MainMenu.CaptionedImagesAdapterMain;
import com.fiszki.MainMenu.MainMenuName;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

public class ActivityMain extends Activity {
    private String[]                    mMainMenuNames;
    private int[]                       mMainMenuImgsrc;
    private RecyclerView                mRVlanguage;
    private CaptionedImagesAdapterMain mcaptionedImagesAdapter;
    private String                      msiglerowJSON = "";
    private String                      mdatadownloadedJSON = "";
    private String[]                    mDowloadCountryDescrition;
    private String[]                    mDowloadCountryImgsrc;
    private boolean                     connected = false;
    private String                      mleftmenustrings[];
    private ListView                    mdrawerlsvt;
    private DrawerLayout                mDL;
    private ActionBarDrawerToggle       mdrawerToggle;
    private InterstitialAd              interstitialAd;
    ///Okno dialogowe do wprowadzania - CREATE

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setTitle("");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getListFromServer();

        // W zależnosci od wersji sa wyswietlane reklamy
        if (BuildConfig.PAID_VERSION) {// this is the flag configured in build.gradle
            getActionBar().setTitle("");
        } else {
            getActionBar().setTitle("Free Version");

            //fullscreen
            AdRequest adRequestfullscreen = new AdRequest.Builder().build();
            interstitialAd = new InterstitialAd(this);
            interstitialAd.setAdUnitId(getString(R.string.add_id));
            interstitialAd.loadAd(adRequestfullscreen);

        }



        mleftmenustrings = getResources().getStringArray(R.array.homemenu);
        mdrawerlsvt= findViewById(R.id.activitymainLVmenu);
        mDL = (DrawerLayout) findViewById(R.id.maindrawer_layout);

        mdrawerlsvt.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_activated_1,mleftmenustrings));
        mdrawerlsvt.setOnItemClickListener(new DrawerItemClickListener());
        mdrawerToggle = new ActionBarDrawerToggle(this,mDL,
                R.string.app_name,R.string.app_name){

    ////////////////////////////////////////////////////////////////////////////////////////////////

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDL.setDrawerListener(mdrawerToggle);
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Kod wykonywany po kliknięciu elementu w szufladzie nawigacyjnej
            selectItem(position);
        }
    }

    //Rozwijane menu selectItem
    private void selectItem(int position) {

        switch (position) {
            case 0:
                Toast.makeText(getBaseContext(), "Info", Toast.LENGTH_SHORT).show();
                break;

            case 1:
                Toast.makeText(getBaseContext(), "Exit", Toast.LENGTH_SHORT).show();
                break;
        }

        mDL.closeDrawer(mdrawerlsvt);
    }


    private void getListFromServer() {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mRVlanguage = (RecyclerView) findViewById(R.id.mainRVchoselanguage);
            }
            @Override
            protected Void doInBackground(Void... voids) {
                String downloadadres = "" + getString(R.string.downloadadres);

                String serverAddress = getString(R.string.ipadres);
                int serverTCPport = 443;
                int timeoutMS = 1250;
                connected = isHostReachable(serverAddress, serverTCPport, timeoutMS);
               // Log.d("INTENET ", "INTENET FALSE = " + connected);
                if (connected){
                    try {
                        //połączenie z plikiem na serwerze i pobranie danych
                        URL url = new URL(downloadadres + "zlopenkraje.php");
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        InputStream inputStream = httpURLConnection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        while (msiglerowJSON != null) {
                            msiglerowJSON = bufferedReader.readLine();
                            mdatadownloadedJSON = mdatadownloadedJSON + msiglerowJSON;
                        }
                        //utworzenie tablicy JSONrray
                        JSONArray JA = new JSONArray(mdatadownloadedJSON);
                        mDowloadCountryDescrition = new String[JA.length()];
                        mDowloadCountryImgsrc = new String[JA.length()];
                        //wyciągniecie danych za pomocą JSONObject do tablic
                        for (int i = 0; i < JA.length(); i++) {
                            JSONObject JO = (JSONObject) JA.get(i);
                            mDowloadCountryDescrition[i] = JO.getString("name");
                            mDowloadCountryImgsrc[i] = getText(R.string.downloadadres)+JO.getString("img");/// uwaga tu może być błąd
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
                return null;
            }
            //wywoływana po wykonaniu pobierania danych z serwera
            @Override
            protected void onPostExecute(Void aVoid) {

             //   mcaptionedImagesAdapter = new CaptionedImagesAdapterMain(mMainMenuNames,mMainMenuImgsrc,null);
                if (connected) {
                    Log.d("INTENET","INTENET");
                    mcaptionedImagesAdapter = new CaptionedImagesAdapterMain(mDowloadCountryDescrition, null, mDowloadCountryImgsrc);
                }else{
                    Log.d("NO INTENET","NO INTENET");
                    mMainMenuNames = new String[MainMenuName.mainmenuelements.length];
                    mMainMenuImgsrc = new int[MainMenuName.mainmenuelements.length];
                    for (int i =0;i<mMainMenuNames.length;i++){
                        mMainMenuNames[i] = MainMenuName.mainmenuelements[i].getName();
                        mMainMenuImgsrc [i] =MainMenuName.mainmenuelements[i].getImgResourcedID();
                    }
                    mcaptionedImagesAdapter = new CaptionedImagesAdapterMain(mMainMenuNames,mMainMenuImgsrc,null);
                }
                mRVlanguage.setAdapter(mcaptionedImagesAdapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
                //GridLayoutManager layoutManager = new GridLayoutManager(getBaseContext(),2);
                mRVlanguage.setLayoutManager(layoutManager);

                mcaptionedImagesAdapter.setListener(new CaptionedImagesAdapterMain.Listener() {

                    @Override
                    public void onClick(int position) {

                        if (position ==0 ){
                            Intent intent = new Intent(getBaseContext(), ActivityWordPackage.class);
                            startActivity(intent);
                        }if (position>0&&connected){
                            final Intent intent = new Intent(getBaseContext(), ActivityDownloadCountry.class);
                            if (BuildConfig.PAID_VERSION==false) {
                                if (interstitialAd.isLoaded() || interstitialAd.isLoading()) {
                                    interstitialAd.show();
                                    interstitialAd.setAdListener(new AdListener() {
                                        @Override
                                        public void onAdClosed() {
                                            startActivity(intent);
                                        }

                                    });
                                } else {
                                    startActivity(intent);
                                }
                            }else{
                                startActivity(intent);
                            }

                        }
                    }
                });
            }
        };

        asyncTask.execute();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mdrawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mdrawerToggle.syncState();
    }

    //sprawdzenia połaczenia z internetem
    public static boolean isHostReachable(String serverAddress, int serverTCPport, int timeoutMS){
        boolean connected = false;
        Socket socket;
        try {
            socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(serverAddress, serverTCPport);
            socket.connect(socketAddress, timeoutMS);
            if (socket.isConnected()) {
                connected = true;
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket = null;
        }
        return connected;
    }


    public boolean onOptionsItemSelected(MenuItem item){
        if (mdrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

