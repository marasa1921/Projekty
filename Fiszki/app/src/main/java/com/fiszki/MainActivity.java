package com.fiszki;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

public class MainActivity extends Activity {
    private String []                       mMainMenuNames;
    private int []                          mMainMenuImgsrc;
    private RecyclerView                    mRVlenguage;
    private CaptionedImagesAdapterMain      mcaptionedImagesAdapter;
    private String                          msiglerowJSON="",mdataJSON="";
    private String []                       mDowloadCountryDescrition;
    private String []                       mDowloadCountryImgsrc;
    boolean                                 connected = false;
    private String                          mleftmenustrings [];
    private ListView                        mdrawerlsvt;
    private DrawerLayout                    mDL;
    private ActionBarDrawerToggle           mdrawerToggle;
    ///Okno dialogowe do wprowadzania - CREATE

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Kod wykonywany po kliknięciu elementu w szufladzie nawigacyjnej
            selectItem(position);
        }
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().setTitle("");

        getActionBar().setDisplayHomeAsUpEnabled(true);

        getActionBar().setHomeButtonEnabled(true);

        getListFromServer();




        mleftmenustrings = getResources().getStringArray(R.array.homemenu);
        mdrawerlsvt= findViewById(R.id.activitymainLVmenu);
        mDL = (DrawerLayout) findViewById(R.id.maindrawer_layout);

        mdrawerlsvt.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_activated_1,mleftmenustrings));
        mdrawerlsvt.setOnItemClickListener(new DrawerItemClickListener());
        mdrawerToggle = new ActionBarDrawerToggle(this,mDL,
                R.string.app_add,R.string.app_add){

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

    //Rozwijane menu selectItem
    private void selectItem(int position) {

        switch (position) {
            case 0:

                Toast.makeText(getBaseContext(), "GALKA SPIOCH", Toast.LENGTH_SHORT).show();
                break;

            case 1:
                Toast.makeText(getBaseContext(), "GALKA SPIOCH 1", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(getBaseContext(), "GALKA SPIOCH 2", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(getBaseContext(), "GALKA SPIOCH 3", Toast.LENGTH_SHORT).show();
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
                mRVlenguage = (RecyclerView) findViewById(R.id.mainRVchoselenguage);
            }
            @Override
            protected Void doInBackground(Void... voids) {
                String downloadadres = "" + getString(R.string.downloadadres);

                String serverAddress = getString(R.string.ipadres);
                int serverTCPport = 443;
                int timeoutMS = 1250;
                connected = isHostReachable(serverAddress, serverTCPport, timeoutMS);
                Log.d("INTENET ", "INTENET FALSE = " + connected);
                if (connected){
                    try {
                        Log.d("mdataJSON", "mdata1 = " + mdataJSON);

                        //połączenie z plikiem na serwerze i pobranie danych
                        URL url = new URL(downloadadres + "zlopenkraje.php");
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        InputStream inputStream = httpURLConnection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                        //wyciągnięcie danyh z buuferReadera do Stringa
                        while (msiglerowJSON != null) {
                            msiglerowJSON = bufferedReader.readLine();
                            mdataJSON = mdataJSON + msiglerowJSON;
                        }
                        Log.d("mdataJSON", "mdata2 = " + mdataJSON);
                        //utworzenie tablicy JSONrray
                        JSONArray JA = new JSONArray(mdataJSON);
                        //deklaracja tablic z wielkościa pobranych danych
                        mDowloadCountryDescrition = new String[JA.length()];
                        mDowloadCountryImgsrc = new String[JA.length()];
                        //wyciągniecie danych za pomocą JSONObject do tablic
                        for (int i = 0; i < JA.length(); i++) {
                            JSONObject JO = (JSONObject) JA.get(i);
                            mDowloadCountryDescrition[i] = JO.getString("name");
                            mDowloadCountryImgsrc[i] = getText(R.string.downloadadres)+JO.getString("img");/// uwaga tu może być błąd
                            Log.d("danem", "danem2" + mDowloadCountryImgsrc[i]);
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
                mRVlenguage.setAdapter(mcaptionedImagesAdapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
                //GridLayoutManager layoutManager = new GridLayoutManager(getBaseContext(),2);
                mRVlenguage.setLayoutManager(layoutManager);

                mcaptionedImagesAdapter.setListener(new CaptionedImagesAdapterMain.Listener() {

                    @Override
                    public void onClick(int position) {

                        if (position ==0 ){
                            Intent intent = new Intent(getBaseContext(), ActivityWordPackage.class);
                            startActivity(intent);
                        }if (position>0&&connected){

                            Intent intent = new Intent(getBaseContext(),ActivityDownloadCountry.class);
                            startActivity(intent);
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

