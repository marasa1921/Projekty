package com.fiszki;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityAddWord extends Activity {
    private DBAdapter   myDB;
    private EditText    mETlg1,mETlg2;
    private String      mlg1,mlg2;
    private Cursor      cursor;
    private int         index =-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);
        mETlg1 =(EditText) findViewById(R.id.addwordETlg1);
        mETlg2 =(EditText) findViewById(R.id.addwordETlg2);
        openDB();
        cursor = myDB.getTableWordsAllRows();

        // index informuje czy jest edytowane słowo (-1 gdy jest dodawane)
        Intent intent = getIntent();
        if (intent.getExtras()!=null){
            index = (int) intent.getExtras().get("index");
            if (index>=0) {
                cursor.moveToPosition(index);
                mETlg1.setText(cursor.getString(1));
                mETlg2.setText(cursor.getString(2));
            }
        }

    }

    public void onClickAddtoBase(View view) {

        mlg1 = mETlg1.getText().toString();
        mlg2 = mETlg2.getText().toString();
        if (mlg1.isEmpty()|mlg2.isEmpty()){
            Toast t;
            t= Toast.makeText(getBaseContext(),getText(R.string.addworderror),Toast.LENGTH_SHORT);
            t.show();
        }else {

            if (index < 0) {
                cursor.moveToLast();
                myDB.insertTableWordstRow(mlg1, mlg2, 0);
                mETlg1.setText("");
                mETlg2.setText("");
            } else {

                if (!mlg1.equals("") & !mlg2.equals("")) {
                    cursor.moveToPosition(index);
                    myDB.updateTableWordsRow(cursor.getInt(0), mlg1, mlg2, 0);
                    Intent intent = new Intent(getBaseContext(), ActivityWordList.class);
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
        // TODO Auto-generated method stub
        myDB = new DBAdapter(this);
        myDB.open();
    }

    private void closeDB() {
        // TODO Auto-generated method stub
        cursor.close();
        myDB.close();
    }

    protected void onDestroy() {
        super.onDestroy();
        closeDB();
    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), ActivityWordList.class);
        myIntent.putExtra("fromDB_change","ss");
        startActivityForResult(myIntent, 0);
        return true;
    }


}
