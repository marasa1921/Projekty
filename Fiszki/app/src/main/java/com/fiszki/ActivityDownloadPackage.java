package com.fiszki;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fiszki.Database.DBAdapter;
import com.fiszki.DownloadPackageMenu.CaptionedImagesAdapterDPackages;
import com.fiszki.DownloadService.DownloadService;
import com.fiszki.HelpClasses.DirectoryHelper;

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

public class ActivityDownloadPackage extends Activity {

    private static final int                WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 54654;
    private String []                       mMainMenuNames;
    private String []                       mMainMenuDescription;
    private RecyclerView                    mRVlanguage;
    private String                          row = "";
    private String                          data = "";
    private CaptionedImagesAdapterDPackages mcaptionedImagesAdapter;
    private String                          lgnumber;
    private String                          languagename;
    private DBAdapter myDB;
    private int []                          packagesdownloaded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_package);
        Intent intencja = getIntent();
        int lgnnumberint= intencja.getExtras().getInt("lgnumber");
        lgnumber = ""+lgnnumberint;
        languagename = intencja.getExtras().getString("languagename");

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
        DirectoryHelper.createDirectory(getBaseContext());
        getListFromServer();
    }

    private void getListFromServer() {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                mRVlanguage = (RecyclerView) findViewById(R.id.downloadpackageRVchosepackage);
                super.onPreExecute();
            }
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL(""+getString(R.string.downloadadres)+lgnumber+"."+languagename+"/"+"listapakietow.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    while (row != null){
                        row = bufferedReader.readLine();
                        data = data + row;
                    }
                    JSONArray JA = new JSONArray(data);
                    mMainMenuNames = new String[JA.length()];
                    mMainMenuDescription = new String[JA.length()];
                    for (int i=0;i<JA.length();i++){
                        JSONObject JO = (JSONObject) JA.get(i);
                        mMainMenuNames[i] = JO.getString("nazwa");
                        mMainMenuDescription[i] =JO.getString("opis");
                    }
                    openDB();
                    Cursor mcursor = myDB.getTablePackagesAllRows();
                    mcursor.moveToFirst();
                    int lendd = mMainMenuDescription.length;
                    packagesdownloaded = new int[lendd];
                    // nie szukamy po długości bazy tylko po iloscci pakietow umieszczonych na serwerze
                    for (int i = 0; i < lendd; i++) {
                        packagesdownloaded[i]=myDB.getTablePackagesCheck(mMainMenuDescription[i],"FISZKI/"+lgnumber+"."+languagename+"/flag.png");
                    }
                    int zzz = mcursor.getCount();
                    for (int i = 0; i <zzz ; i++) {
                        Log.d("mcursor.getCount()","mcursor.getString(1) = "+mcursor.getString(1)+"mcursor.getString(2) = "+mcursor.getString(2));
                    mcursor.moveToNext();
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

            @Override
            protected void onPostExecute(Void aVoid) {

                setdownloadList();

                mcaptionedImagesAdapter.setListener(new CaptionedImagesAdapterDPackages.Listener() {
                    @Override
                    public void onClick(int position) {
                        Intent intent = new Intent(getBaseContext(), DownloadService.class);
                        intent.putExtra("lgnumber",lgnumber);
                        intent.putExtra("languagename",languagename);
                        intent.putExtra("position",position);
                        intent.putExtra("packagename",mMainMenuDescription[position]);
                        startService(intent);
                        packagesdownloaded[position]=1;
                        setdownloadList();
                    }
                });
            }
        };

        asyncTask.execute();
    }

    public void setdownloadList(){
        mcaptionedImagesAdapter = new CaptionedImagesAdapterDPackages(mMainMenuNames,mMainMenuDescription,packagesdownloaded);
        mRVlanguage.setAdapter(mcaptionedImagesAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        mRVlanguage.setLayoutManager(layoutManager);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                DirectoryHelper.createDirectory(this);

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), ActivityDownloadCountry.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(myIntent, 0);
        return true;
    }
    private void openDB() {
        myDB = new DBAdapter(this);
        myDB.open();
    }

    private void closeDB() {
        myDB.close();
    }
    protected void onDestroy() {
        super.onDestroy();
        closeDB();
    }
    @Override
    public void onBackPressed(){
        Intent myIntent = new Intent(getApplicationContext(), ActivityDownloadCountry.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(myIntent, 0);
    }

}
