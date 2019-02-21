package com.fiszki;

import android.app.DownloadManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadSongService extends IntentService {
    private     String lgnumber;
    private     String lenguagename;
    private     int position;


    public DownloadSongService() {
        super("DownloadSongService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        lgnumber = intent.getStringExtra("lgnumber");
        lenguagename = intent.getStringExtra("lenguagename");
        position = intent.getIntExtra("position",0);


        String downloadfatherlenguage = getText(R.string.downloadadres).toString()+lgnumber+"."+lenguagename+"/pakiet"+position+lenguagename+".php";//adres ww pliku jezykowego
        String downloadflag = getText(R.string.downloadadres).toString()+lgnumber+"."+lenguagename+"/flag.png";// aders ww flagi
        String destinationPath = "FISZKI/"+lgnumber+"."+lenguagename+"/";
        String lgname = intent.getStringExtra("packagename")+".txt";
        String baselenguagename= intent.getStringExtra("packagename");
        String [] filename = new String[2];
        String [] downloadadres = new String[2];

        filename [1] ="flag.png";
        filename [0] =lgname;
        downloadadres [0] =downloadfatherlenguage;
        downloadadres [1] =downloadflag;
        for (int i = 0; i < 2; i++) {
            downloadpackage(downloadadres[i],destinationPath,filename[i],i,baselenguagename);
        }
    }

    private void downloadpackage(String downloadadres,String destinationPath,String filename,int filenumber,String baselenguagename){
        File directoryName = new File(Environment.getExternalStorageDirectory(), destinationPath);

           if (!isDirectoryExists(destinationPath)) {
              File file = new File(Environment.getExternalStorageDirectory(), destinationPath);
               file.mkdir();
          }
        File file = new File(directoryName, filename);
        if (file.exists()){
        }else {
            FileOutputStream stream = null;
            try {
                if (filenumber==0) {
                    URL url = new URL(downloadadres);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String row="";
                    String data="";
                    while (row != null){
                        row = bufferedReader.readLine();
                        data  += row;
                    }
                    stream = new FileOutputStream(file);
                    stream.write(data.getBytes());
                    DBAdapter myDB = new DBAdapter(getBaseContext());
                    myDB.open();
                    Cursor cursor = myDB.getTablePackagesAllRows();
                    cursor.moveToLast();
                        myDB.insertTablePackagesRow(baselenguagename, "FISZKI/"+lgnumber+"."+lenguagename+"/flag.png", 0);
                        cursor.close();
                        myDB.close();
                        showText(getText(R.string.serviceComplete).toString());
                }else {
                    startDownload(downloadadres, destinationPath);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {if (stream!=null){
                    stream.close();}
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    private void startDownload(String downloadPath, String destinationPath) {
        DownloadManager dm;
        long queueid;
        Uri uri = Uri.parse(downloadPath); // Path where you want to download file.
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  // This will show notification on top when downloading the file.
        String nameOfFile = URLUtil.guessFileName(downloadPath, null,
                MimeTypeMap.getFileExtensionFromUrl(downloadPath));
        request.setTitle(nameOfFile); // Title for notification.
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(destinationPath, uri.getLastPathSegment());  // Storage directory path
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        queueid = dm.enqueue(request);
    }
    private boolean isDirectoryExists(String directoryName) {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + directoryName);
        return file.isDirectory() && file.exists();
    }

    private void showText(String text) {

        int NOTIFICATION_ID = 5453;

        Intent intent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ActivityWordPackage.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.app_add))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setContentText(text)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,notification);
    }

}
