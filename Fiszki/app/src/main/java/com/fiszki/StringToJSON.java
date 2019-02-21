package com.fiszki;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class StringToJSON {
    public          String package_name; // NAZWA PAKIETU KÓTRY JEST ZAPISYWANY LUB POBIERANY
    public          StringToJSON(String package_name) {

        this.package_name = package_name;
    }
    //ZAPIS DO PLIKU JSON
    public void SaveDATA(ArrayList<Packages> WordList,String folder){
        ArrayList<Packages> mWordList = WordList;
        String msaveData ="[";
        // pobranie danych i przekazanie ich do Stringa typu JSON
        for (int i = 0; i <mWordList.size() ; i++) {
            msaveData = msaveData+"{\"word\":\""+mWordList.get(i).word+"\",\"desc\":\""+mWordList.get(i).desc+"\",\"ok\":\""+mWordList.get(i).ok+"\"}";
            if (i<mWordList.size()-1){
                msaveData =msaveData+",";
            }
        }
        msaveData=msaveData +"]";
        File mmainfolder = new File(Environment.getExternalStorageDirectory(), "FISZKI/"+folder);
        File mfile = new File(mmainfolder, package_name+".txt");

            FileOutputStream mstream = null;
            try {
                mstream = new FileOutputStream(mfile);
                mstream.write(msaveData.getBytes());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    mstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


    }
    //ŁADOWANIE Z PLIKU JSON-A STWORZONE PRZEZ UŻYTKOWNIKA DANE
    public ArrayList<Packages> LoadDATAown() {
        ArrayList<Packages> mSwipeListData = new ArrayList<Packages>();
        //pakiet 1 stworzony przez użytkownika

        try {
            File mFile = new File(Environment.getExternalStorageDirectory(), "FISZKI/" + package_name + ".txt");
            FileInputStream fIn = new FileInputStream(mFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
            String aDataRow = "", aBuffer = "";
            while ((aDataRow = myReader.readLine()) != null) {
                aBuffer += aDataRow + "\n";
            }
            JSONArray JA = new JSONArray(aBuffer);

            for (int i = 0; i < JA.length(); i++) {
                JSONObject JO = (JSONObject) JA.get(i);
                Packages packet = new Packages();
                packet.word = JO.getString("word");
                packet.desc = JO.getString("desc");
                packet.ok = JO.getInt("ok");
                mSwipeListData.add(packet);
            }

        } catch (Exception e) {
            e.getMessage();
        }
        //pakiet pobrany z serwera
        return mSwipeListData;
    }

    // ŁADOWANIE Z SERWERA
    public ArrayList<Packages> LoadDATAfromServer(String flag_source) {

        String arr[] = flag_source.split("/", 3);

        ArrayList<Packages> mSwipeListData = new ArrayList<Packages>();


        try {
            File mFile = new File(Environment.getExternalStorageDirectory(), arr[0]+"/"+arr[1]+"/" + package_name + ".txt");
            FileInputStream fIn = new FileInputStream(mFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
            String aDataRow = "", aBuffer = "";
            while ((aDataRow = myReader.readLine()) != null) {
                aBuffer += aDataRow + "\n";
            }
            JSONArray JA = new JSONArray(aBuffer);
            for (int i = 0; i < JA.length(); i++) {
                JSONObject JO = (JSONObject) JA.get(i);
                Packages packet = new Packages();
                packet.word = JO.getString("word");
                packet.desc = JO.getString("desc");
                packet.ok = JO.getInt("ok");
                mSwipeListData.add(packet);
            }

        } catch (Exception e) {
            e.getMessage();
        }
        //pakiet pobrany z serwera
        return mSwipeListData;
    }

}



