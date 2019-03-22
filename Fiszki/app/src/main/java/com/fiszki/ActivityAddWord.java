package com.fiszki;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fiszki.Database.DBAdapter;

public class ActivityAddWord extends Activity {
    private DBAdapter myDB;
    private EditText    mETlenguage1;
    private EditText    mETlanguage2;
    private String      mlanguage1;
    private String      mlanguage2;
    private Cursor      cursor;
    private int         index =-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);
        mETlenguage1 =(EditText) findViewById(R.id.addwordETlg1);
        mETlanguage2 =(EditText) findViewById(R.id.addwordETlg2);
        openDB();
        cursor = myDB.getTableWordsAllRows();

        // index informuje czy jest edytowane słowo (-1 gdy jest dodawane)
        Intent intent = getIntent();
        if (intent.getExtras()!=null){
            index = (int) intent.getExtras().get("index");
            if (index>=0) {
                cursor.moveToPosition(index);
                mETlenguage1.setText(cursor.getString(1));
                mETlanguage2.setText(cursor.getString(2));
            }
        }

    }

    public void onClickAddtoBase(View view) {

        mlanguage1 = mETlenguage1.getText().toString();
        mlanguage2 = mETlanguage2.getText().toString();
        if (mlanguage1.isEmpty()|mlanguage2.isEmpty()){
            Toast t;
            t= Toast.makeText(getBaseContext(),getText(R.string.addworderror),Toast.LENGTH_SHORT);
            t.show();
        }else {

            if (index < 0) {
                cursor.moveToLast();
                myDB.insertTableWordstRow(mlanguage1, mlanguage2, 0);
                mETlenguage1.setText("");
                mETlanguage2.setText("");
            } else {

                if (!mlanguage1.equals("") & !mlanguage2.equals("")) {
                    cursor.moveToPosition(index);
                    myDB.updateTableWordsRow(cursor.getInt(0), mlanguage1, mlanguage2, 0);
                    Intent intent = new Intent(getBaseContext(), ActivityWordList.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void openDB() {
        myDB = new DBAdapter(this);
        myDB.open();
    }

    private void closeDB() {
        cursor.close();
        myDB.close();
    }

    protected void onDestroy() {
        super.onDestroy();
        closeDB();
    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), ActivityWordList.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(myIntent, 0);
        return true;
    }
    @Override
    public void onBackPressed(){
        Intent myIntent = new Intent(getApplicationContext(), ActivityWordList.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(myIntent, 0);
    }


}
