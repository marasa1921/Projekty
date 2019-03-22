package com.fiszki;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


import com.fiszki.Database.DBAdapter;

import java.util.Locale;

public class ActivityWords extends Activity {
    private     TextToSpeech mtxtspeach;
    private     ViewFlipper  mVFlanguage1;
    private     ViewFlipper  mVFlanguage2;
    private     TextView     mTVlanguage1;
    private     Button       mBTlanguage2;
    private     DBAdapter    myDB;
    private     Cursor       cursor;
    private     int          mok=1,mrowid=0;
    private     String       mword1,mword2;
    private     boolean      mesagedialogloadallwords;
    private     Intent       mintencja;
    private     boolean      mTVlanguage2clicked=true;
    private     boolean      mchangecard=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words);
            mintencja = getIntent();
            mesagedialogloadallwords = mintencja.getExtras().getBoolean("mesagedialogloadallwordss");

            mVFlanguage1= findViewById(R.id.wordsVFlg1);
            mVFlanguage2= findViewById(R.id.wordsVFlg2);
            mTVlanguage1= findViewById(R.id.wordsTVlg1);
            mBTlanguage2= findViewById(R.id.wordsTVlg2);

            onClickNext();
            activeButton();

    }
    public void onClickVoice(View view) {
        mtxtspeach = new TextToSpeech(getBaseContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    int result=mtxtspeach.setLanguage(Locale.GERMANY);
                    if(result==TextToSpeech.LANG_MISSING_DATA ||
                            result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast t;
                        t=  Toast.makeText(getBaseContext(),getString(R.string.activitywordssounderror),Toast.LENGTH_SHORT);
                        t.show();
                    }
                    else{
                        String stringtospeach = mTVlanguage1.getText().toString();
                        mtxtspeach.speak(stringtospeach,TextToSpeech.QUEUE_FLUSH,null);
                    }
                }
                else{
                Toast t;
                t=  Toast.makeText(getBaseContext(),getString(R.string.activitywordssounderror),Toast.LENGTH_SHORT);
                t.show();
                }
            }
        });
    }

    public void onClickNext() {
            if (mrowid ==0){
                openDB();
                cursor = myDB.getTableWordsAllRows();
                cursor.moveToPosition(mrowid);

            }
        ////////////////opcja pierwsza/////////////////wszystko///////////
        if (mesagedialogloadallwords==true){
            mTVlanguage1.setText(cursor.getString(1));
            mword2 =cursor.getString(2);
            onClickCardChange(null);
            do {
                myDB.updateTableWordsRow(cursor.getInt(0),cursor.getString(1),cursor.getString(2),0);
            }while (cursor.moveToNext());

            mesagedialogloadallwords=false;
            cursor.moveToFirst();
            mword1 = cursor.getString(1);
        }else{
         //   druga opcja jesli tylko nie znane///////////////////////////// z ma powodowac start od 1
            do {
                    if (cursor.moveToNext()) {
                    } else {
                        cursor = myDB.getTableWordsAllRows();
                        cursor.moveToFirst();
                    }
            }while(cursor.getInt(3)>0^myDB.getTableWordsLenght()==myDB.getTableWordsOKLenght());
                if (myDB.getTableWordsLenght()==myDB.getTableWordsOKLenght()) {
                    Intent intent = new Intent(getBaseContext(), ActivityWordList.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else {
                    mword1 = cursor.getString(1);
                    mTVlanguage1.setText(mword1);
                    mword2 = cursor.getString(2);
                    onClickCardChange(null);
                    mVFlanguage1.showNext();
                    mVFlanguage2.showNext();
                    mrowid = cursor.getInt(0);
                }
        }
    }

    public void onClickYes(View view) {
        if (mchangecard) {
            mTVlanguage2clicked = true;
            activeButton();
            mok = 1;
            myDB.updateTableWordsRow(mrowid, mword1, mword2, mok);
            mok = 0;
            onClickNext();
            mchangecard = false;
            activeButton();
        }
    }
    public void onClickNo(View view){
        if (mchangecard==true) {
            mTVlanguage2clicked = true;
            activeButton();
            mok = 0;
            myDB.updateTableWordsRow(mrowid, mword1, mword2, mok);
            onClickNext();
            mchangecard = false;
            activeButton();
        }
    }
    public void onClickCardChange(View view) {


        if (mTVlanguage2clicked){
            mBTlanguage2.setText("");
            mBTlanguage2.setBackgroundResource(R.drawable.backgroundnoanswer);
            mTVlanguage2clicked=false;


        }else{
            mBTlanguage2.setBackgroundResource(R.drawable.backgroundanswer);
            mBTlanguage2.setText(mword2);
            mTVlanguage2clicked=true;
            mchangecard=true;
            activeButton();
        }
    }

    public void activeButton(){
        int colorbuttonok = ContextCompat.getColor(getBaseContext(), R.color.colorbuttonok);
        int colorbuttonnook = ContextCompat.getColor(getBaseContext(),R.color.colorbuttonnook);
        Button mBTYes = findViewById(R.id.wordsBTyes);
        Button mBTNo =  findViewById(R.id.wordsBTNo);
        if (mchangecard==false){  // jezelei false to jest zolta czcionka
            mBTNo.setTextColor(colorbuttonnook);
            mBTYes.setTextColor(colorbuttonnook);
        }
        else{
            mBTNo.setTextColor(colorbuttonok);
            mBTYes.setTextColor(colorbuttonok);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), ActivityWordList.class);
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
        Intent myIntent = new Intent(getApplicationContext(), ActivityWordList.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(myIntent, 0);
    }
}

