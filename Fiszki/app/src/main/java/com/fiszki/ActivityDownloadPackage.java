package com.fiszki;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ActivityDownloadPackage extends Activity {

    private static final int   WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 54654;
    DownloadManager dm;
    long queueid;
    private String [] mMainMenuNames;
    private String [] mMainMenuDescription;
    private RecyclerView mRVlenguage;
    private String row = "";
    private String data = "";
    private CaptionedImagesAdapterDPackages mcaptionedImagesAdapter;
    private String lgnumber;
    private String lenguagename;
    private DBAdapter myDB;
    private int [] packagesdownloaded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_package);
        Intent intencja = getIntent();
        int lgnnumbetint= intencja.getExtras().getInt("lgnumber");
        lgnumber = ""+lgnnumbetint;
        lenguagename = intencja.getExtras().getString("lenguagename");
       // Log.d("lenguagename = ", lgnumber+"."+lenguagename+"/");

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
                mRVlenguage = (RecyclerView) findViewById(R.id.downloadpackageRVchosepackage);
                super.onPreExecute();
            }

            @Override

            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL(""+getString(R.string.downloadadres)+lgnumber+"."+lenguagename+"/"+"listapakietow.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                 //   Log.d("mdataJSON","mdata1 = "+ bufferedReader);

                    while (row != null){
                        row = bufferedReader.readLine();
                        data = data + row;
                    }
                  //  Log.d("mdataJSON","mdata2 = "+ data);
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

                        Log.d("mdataJSON","  mMainMenuDescription[i] ="+ mMainMenuDescription[i]);
                       // Log.d("mdataJSON","mcursorgetlg1[i] = "+ mcursor.getString(2)+"  mcursorgetlg2[i] = "+ mcursor);
                        packagesdownloaded[i]=myDB.getTablePackagesCheck(mMainMenuDescription[i],"FISZKI/"+lgnumber+"."+lenguagename+"/flag.png");
                        Log.d("mdataJSON","petla = "+ packagesdownloaded[i]);
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
                        Intent intent = new Intent(getBaseContext(),DownloadSongService.class);
                        intent.putExtra("lgnumber",lgnumber);
                        intent.putExtra("lenguagename",lenguagename);
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
        mRVlenguage.setAdapter(mcaptionedImagesAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        mRVlenguage.setLayoutManager(layoutManager);
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
        startActivityForResult(myIntent, 0);
        return true;
    }
    private void openDB() {
        // TODO Auto-generated method stub
        myDB = new DBAdapter(this);
        myDB.open();
    }

    private void closeDB() {
        // TODO Auto-generated method stub
        myDB.close();
    }


}
