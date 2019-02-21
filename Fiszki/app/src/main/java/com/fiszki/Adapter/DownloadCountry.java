package com.fiszki.Adapter;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.fiszki.CaptionedImagesAdapterMain;
import com.fiszki.R;

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
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

public class DownloadCountry {
    private int mdownloadpakiet;
    private String row = "";
    private String data = "";
    String [] mMainMenuNames;
    String [] mMainMenuImgsrc;
    public DownloadCountry(int id) {
        mdownloadpakiet =id;
        getListFromServer();
    }

    private void getListFromServer() {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected Void doInBackground(Void... voids) {

                String serverAddress ="www.google.pl";
                int serverTCPport = 443;
                int timeoutMS = 3500;
                isHostReachable(serverAddress,serverTCPport,timeoutMS);

                try {
                    Log.d("mdataJSON","mdata1 = "+ data);
                    URL url = new URL("http://192.168.64.2/listapakietow.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    while (row != null){
                        row = bufferedReader.readLine();
                        data = data + row;
                    }
                    Log.d("mdataJSON","mdata2 = "+ data);
                    JSONArray JA = new JSONArray(data);
                    mMainMenuNames = new String[JA.length()];
                    mMainMenuImgsrc = new String[JA.length()];
                    for (int i=0;i<JA.length();i++){
                        JSONObject JO = (JSONObject) JA.get(i);
                        mMainMenuNames[i] = JO.getString("nazwa");
                        mMainMenuImgsrc[i] =JO.getString("opis");/// uwaga tu może być błąd
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

            }
        };

        asyncTask.execute();
    }


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
        Log.d("INTENET","INTENET = " + connected);

        return connected;
    }


}

