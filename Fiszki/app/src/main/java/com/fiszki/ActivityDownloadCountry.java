package com.fiszki;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class ActivityDownloadCountry extends Activity {
    private static final int                mWRITE_EXTERNAL_STORAGE_REQUEST_CODE = 54654;
    private RecyclerView                    mRVcountry;
    private String                          msiglerowJSON="",mdataJSON="";
    private String []                       mDowloadCountryDescrition;
    private String []                       mDowloadCountryImgsrc;
    private CaptionedImagesAdapterMain mcaptionedImagesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_country);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
            //Prawa zapisu dla wersji wiekszej lub równej M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, mWRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
        //Tworzenie folderu do zapisu danych jeśli nie istnieje
        DirectoryHelper.createDirectory(getBaseContext());
        getListFromServer();
    }

    private void getListFromServer() {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                mRVcountry = (RecyclerView) findViewById(R.id.downloadcountryRVchoosecountry);
                super.onPreExecute();
            }
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    //połączenie z plikiem na serwerze i pobranie danych
                    String downloadadres=""+getString(R.string.downloadadres);

                    URL url = new URL(downloadadres+"zlopenkrajedownload.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    //wyciągnięcie danyh z buuferReadera do Stringa
                    while (msiglerowJSON != null){
                        msiglerowJSON = bufferedReader.readLine();
                        mdataJSON = mdataJSON + msiglerowJSON;
                    }
                    //utworzenie tablicy JSONrray
                    JSONArray JA = new JSONArray(mdataJSON);
                    //deklaracja tablic z wielkościa pobranych danych
                    mDowloadCountryDescrition = new String[JA.length()];
                    mDowloadCountryImgsrc = new String[JA.length()];
                    //wyciągniecie danych za pomocą JSONObject do tablic
                    for (int i=0;i<JA.length();i++){
                        JSONObject JO = (JSONObject) JA.get(i);
                        mDowloadCountryDescrition[i] = JO.getString("name");
                        mDowloadCountryImgsrc[i] =getText(R.string.downloadadres)+JO.getString("img");/// uwaga tu może być błąd
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
            //wywoływana po wykonaniu pobierania danych z serwera
            @Override
            protected void onPostExecute(Void aVoid) {
                Log.d("mDowloadCountryImgsrc","mDowloadCountryImgsrc = "+mDowloadCountryImgsrc[1]);
                //przekazanie danych do adaptera :)
                mcaptionedImagesAdapter = new CaptionedImagesAdapterMain(mDowloadCountryDescrition,null,mDowloadCountryImgsrc);
                //przypisanie adaptera;
                mRVcountry.setAdapter(mcaptionedImagesAdapter);
                //określenie układu (liniowy)
                GridLayoutManager layoutManager = new GridLayoutManager(getBaseContext(),2);
                mRVcountry.setLayoutManager(layoutManager);
                //ustawienie listenera dla adaptera
                mcaptionedImagesAdapter.setListener(new CaptionedImagesAdapterMain.Listener() {
                    @Override
                    public void onClick(int position) {
                        //?????????????????????
                        Log.d("pozycja = ",""+position);
                          Intent intent = new Intent(getBaseContext(), ActivityDownloadPackage.class);
                          intent.putExtra("lgnumber",position);
                          intent.putExtra("lenguagename",mDowloadCountryDescrition[position]);
                          startActivity(intent);
                    }
                });
            }
        };

        asyncTask.execute();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == mWRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                DirectoryHelper.createDirectory(this);


        }
    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}
