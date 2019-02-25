package com.fiszki;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


import java.util.Locale;

public class ActivityWords extends Activity {
    private     TextToSpeech mtxtspeach;
    private     ViewFlipper mVFlg1,mVFlg2;
    private     TextView    mTVlg1;
    private     Button      mBTlg2;
    private     DBAdapter   myDB;
    private     Cursor      cursor;
    private     int         mok=1,mrowid=0;
    private     String      mword1,mword2;
    private     boolean     mmsgdlg;
    private     Intent      mintencja;
    private     boolean     mTVlg2clicked=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words);
            mintencja = getIntent();
            mmsgdlg = mintencja.getExtras().getBoolean("mmsgdlg");

            mVFlg1= findViewById(R.id.wordsVFlg1);
            mVFlg2= findViewById(R.id.wordsVFlg2);
            mTVlg1= findViewById(R.id.wordsTVlg1);
            mBTlg2= findViewById(R.id.wordsTVlg2);

            onClickNext();

    }
    public void onClickVoice(View view) {

        mtxtspeach = new TextToSpeech(getBaseContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                // TODO Auto-generated method stub
                if(status != TextToSpeech.ERROR){
                    int result=mtxtspeach.setLanguage(Locale.GERMANY);
                    if(result==TextToSpeech.LANG_MISSING_DATA ||
                            result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast t;
                        t=  Toast.makeText(getBaseContext(),getString(R.string.activitywordssounderror),Toast.LENGTH_SHORT);
                        t.show();
                    }
                    else{
                        String stringtospeach = mTVlg1.getText().toString();
                        mtxtspeach.speak(stringtospeach,TextToSpeech.QUEUE_FLUSH,null);
                    }
                }
                else{
                 //   Log.e("error", "Initilization Failed!");
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
        if (mmsgdlg==true){

            mTVlg1.setText(cursor.getString(1));
            mword2 =cursor.getString(2);
            onClickCardChange(null);
            do {
                myDB.updateTableWordsRow(cursor.getInt(0),cursor.getString(1),cursor.getString(2),0);
            }while (cursor.moveToNext());

            mmsgdlg=false;
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
                    startActivity(intent);
                }else {
                    mword1 = cursor.getString(1);
                    mTVlg1.setText(mword1);
                    mword2 = cursor.getString(2);
                    onClickCardChange(null);
                    mVFlg1.showNext();
                    mVFlg2.showNext();
                    mrowid = cursor.getInt(0);
                }
        }
    }

    public void onClickYes(View view) {
        mTVlg2clicked=true;
        mok=1;
        myDB.updateTableWordsRow(mrowid,mword1,mword2,mok);
        mok=0;
        onClickNext();

    }
    public void onClickNo(View view){
        mTVlg2clicked=true;
        mok=0;
        myDB.updateTableWordsRow(mrowid,mword1,mword2,mok);
        onClickNext();


    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), ActivityWordList.class);
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

    protected void onDestroy() {
        super.onDestroy();
        closeDB();
    }

    public void onClickCardChange(View view) {
Log.d("mTVlg2clicked = ","mTVlg2clicked = "+mTVlg2clicked);
        if (mTVlg2clicked){
            mBTlg2.setText("");
            mBTlg2.setBackgroundResource(R.drawable.backgroundnoanswer);
            mTVlg2clicked=false;



        }else{

            mBTlg2.setBackgroundResource(R.drawable.backgroundanswer);
            mBTlg2.setText(mword2);
            mTVlg2clicked=true;
        }


    }
}

