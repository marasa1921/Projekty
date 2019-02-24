package com.fiszki;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

import com.fiszki.Adapter.OnSwipeListItemClickListener;
import com.fiszki.Adapter.SwipeListAdapter;
import com.fiszki.Adapter.SwipeListView;

import android.widget.TextView;
import android.widget.Toast;

public class ActivityWordPackage extends Activity {
    private DBAdapter               myDB;
    private Cursor                  mcursor;
    private SwipeListView           mswipemenulistView;
    private ListAdapter             mswipemenulistAdapter;
    private ArrayList<Packages>     mswipemenulistData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_package);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
            listDataadd();

        //////////////////////////////////
        mswipemenulistView = findViewById(R.id.wordpackageSLV);
        mswipemenulistView.setListener(new OnSwipeListItemClickListener() {
            @Override
            public void OnClick(View view, int index) {
                Intent inencja = new Intent(getBaseContext(),ActivityWordList.class);
                mcursor.moveToPosition(index);
                Log.d("indeks = ","indeks = "+index+ "||" +mcursor.getString(1));
                ViewHolder.package_name=mcursor.getString(1);//tutaj oszukuje ?!!!!!!
                inencja.putExtra("package_name",mcursor.getString(1));
                ViewHolder.flag_source=mcursor.getString(2);//tutaj oszukuje ?!!!!!!
                inencja.putExtra("flag_source",mcursor.getString(2));
                ViewHolder.package_own=mcursor.getInt(3);//tutaj oszukuje ?!!!!!!
                inencja.putExtra("package_own",mcursor.getInt(3));
                startActivity(inencja);
            }

            @Override
            public boolean OnLongClick(View view, int index) {
                return false;
            }

            @Override
            public void OnControlClick(int rid, View view, int index) {
                switch (rid){
                    case R.id.delete:
                        mcursor.moveToPosition(index);
                        long i = mcursor.getLong(0);

                        int mownpackage = mcursor.getInt(3);
                        if (mownpackage==0) {
                            String mcursor1 = mcursor.getString(1);
                            String mystring = mcursor.getString(2);
                            String arr[] = mystring.split("/", 3);

                            String Word1 = arr[0];
                            String Word2 = arr[1];

                            String delete = Environment.getExternalStorageDirectory() + "/" + Word1 + "/" + Word2 + "/" + mcursor1 + ".txt";

                            File f1 = new File(delete);
                            boolean d1 = f1.delete();
                        }else{
                            String mcursor1 = mcursor.getString(1);
                            String delete = Environment.getExternalStorageDirectory() + "/FISZKI/"+mcursor1+".txt";
                            File f1 = new File(delete);
                            boolean d1 = f1.delete();
                        }

                        myDB.deleteTablePackagesRow(i);

                        onDestroy();
                        onCreate(Bundle.EMPTY);
                        break;
                }
            }
        },new int[]{R.id.delete});
        mswipemenulistAdapter = new ListAdapter(mswipemenulistData);
        mswipemenulistView.setAdapter(mswipemenulistAdapter);
        getWindow().setAllowEnterTransitionOverlap(false);
    }
    //funkcja uzupełniająca Arraylist z bazy sql // lista pakietow
    private void listDataadd() {
        openDB();
        mcursor = myDB.getTablePackagesAllRows();
        mswipemenulistData = new ArrayList<Packages>();
        if (mcursor.moveToFirst()){
            do {
                Packages mpackage = new Packages();
                mpackage.lg1 = mcursor.getString(1);
                mswipemenulistData.add(mpackage);
            }while (mcursor.moveToNext());
        }
    //    cursor.close();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.word_package_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // menu po prawej stronie
    public boolean onOptionsItemSelected(MenuItem item){
      if (item.getItemId()==R.id.wordpackageBTcreate){
          showadddialog();
      }else{
          Intent intent = new Intent(getBaseContext(),MainActivity.class);
          startActivity(intent);
      }
        return super.onOptionsItemSelected(item);
    }
    ///Okno dialogowe do wprowadzania - CREATE
    public void showadddialog(){
        final Dialog customDialog = new Dialog(this);
        customDialog.setTitle(getString(R.string.addpackagename));
        customDialog.setContentView(R.layout.package_add_layout);
        final EditText sd_txtInputData = (EditText) customDialog.findViewById(R.id.sd_editText1);

        ((Button) customDialog.findViewById(R.id.sd_btnClose)) .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mcursor.moveToLast();
                String lg1 =sd_txtInputData.getText().toString();
              //  Log.d("Lg1 +lg2 ","Lg1 ="+lg1+"Lg2 ="+"c");
////////////////////////////JESLI PAKIET JEST WŁASNY WTEDTY WARTOSC OWN = 1 /////////////////////////////////////
                if (lg1.equals("")) {
                    Toast.makeText(getBaseContext(),getString(R.string.addpackageERROR),Toast.LENGTH_SHORT).show();
                }else{
                    // Tutaj trzeba dodać flagę
                    myDB.insertTablePackagesRow(lg1, "", 1);
                    customDialog.dismiss();
                //    Log.d("Lg1 +lg2 ","Lg1 = "+lg1+"Lg2 = ");
                    onDestroy();
                    onCreate(Bundle.EMPTY);
                }

            }
        });
        customDialog.show();
    }
    // Lista rozwijana - menu
    class ListAdapter extends SwipeListAdapter {
        private ArrayList<Packages> listData;
        public ListAdapter(ArrayList<Packages> listData){
            this.listData= (ArrayList<Packages>) listData.clone();
        }
        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();
            if(convertView == null){//list 1
                convertView = View.inflate(getBaseContext(),R.layout.style_word_package_list,null);
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                viewHolder.delete = (Button) convertView.findViewById(R.id.delete);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(listData.get(position).lg1);
            mcursor = myDB.getTablePackagesAllRows();
            mcursor.moveToPosition(position);
            String mflagadres = mcursor.getString(2);
            File mflagfile = new File(Environment.getExternalStorageDirectory(),mflagadres);

            viewHolder.image.setImageBitmap(BitmapFactory.decodeFile(String.valueOf(mflagfile)));
            return super.bindView(position, convertView);
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
        myDB.close();
    }

    protected void onDestroy() {
        super.onDestroy();
        closeDB();
    }


}
