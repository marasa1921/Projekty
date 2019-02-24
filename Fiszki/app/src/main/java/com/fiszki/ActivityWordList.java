package com.fiszki;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fiszki.Adapter.OnSwipeListItemClickListener;
import com.fiszki.Adapter.SwipeListAdapter;
import com.fiszki.Adapter.SwipeListView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

public class ActivityWordList extends Activity {
    /////////SLIDE MENU///////////////
    private SwipeListView       mswipelistView;
    private ListAdapter         mswipelistAdapter;
    private ArrayList<Packages> mswipeArraylistData;
    private DBAdapter           myDB;
    private Cursor              mcursor;
    private StringToJSON        zlobp;
    private String              package_name,flag_source;
    private int                 package_own;
    private InterstitialAd      interstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.add_id));
        interstitialAd.loadAd(adRequest);

        //pobieranie z bazy wszystkich slow
        Intent intent = getIntent();
        if (intent.getExtras()!=null) {
            package_name = intent.getStringExtra("package_name");
            flag_source = intent.getStringExtra("flag_source");
            package_own = intent.getExtras().getInt("package_own");
        }
        Log.d("Bundle = ","Bundle package_name = "+package_name);
        Log.d("Bundle = ","Bundle package_own = "+package_own);

        // ładowanie nazwy pakietu po restarcie aktywności
        if (savedInstanceState !=null){
            Log.d("Bundle = ","Bundle package_name22 = "+package_name);
            package_name=savedInstanceState.getString("package_name");
        }
        // ładowanie pliku językowego do bazy pliku JSON-a
        if (package_name!=null){
            if (package_own == 1) {
                Log.d("111 package_name = ", "package_name = " + package_name);
                zlobp = new StringToJSON(package_name);
                mswipeArraylistData = zlobp.LoadDATAown();
                insertDataDB();
                LoadDataDB();
            }else if (package_own == 0) {
                Log.d("Bundle = ","Bundle flag_source = "+flag_source);
                zlobp = new StringToJSON(package_name);
                mswipeArraylistData = zlobp.LoadDATAfromServer(flag_source); /////!!!!!
                insertDataDB();
                LoadDataDB();
            }
        }
        // odtworzenie nazwy pakietu językowego i ładowanie do bazy
        else  {
            Log.d("package_name = ","VviewHolder = "+package_name);
            package_name= ViewHolder.package_name;
            flag_source= ViewHolder.flag_source;
            package_own= ViewHolder.package_own;
            LoadDataDB();
        }

    //  ustawienie SwipeListView
        mswipelistView = (SwipeListView) findViewById(R.id.listView);
        mswipelistView.setListener(new OnSwipeListItemClickListener() {
            @Override
            public void OnClick(View view, int index) {
            }

            @Override
            public boolean OnLongClick(View view, int index) {
                return false;
            }

            @Override
            public void OnControlClick(int rid, View view, int index) {
                switch (rid){
                    case R.id.modify:
                        Intent intent = new Intent(getBaseContext(),ActivityAddWord.class);
                        intent.putExtra("index",index);
                        startActivity(intent);
                        break;
                    case R.id.delete:

                        mcursor.moveToPosition(index);
                        long i = mcursor.getLong(0);
                        myDB.deleteTableWordsRow(i);
                        onDestroy();
                        onCreate(Bundle.EMPTY);
                        break;
                }
            }
        },new int[]{R.id.modify,R.id.delete});
        mswipelistAdapter = new ListAdapter(mswipeArraylistData);
        mswipelistView.setAdapter(mswipelistAdapter);
        getWindow().setAllowEnterTransitionOverlap(false);

    }//onCreate
    //pobieranie z bazy wszystkich slow

    private void LoadDataDB() {
        Log.d("LoadDataDB = ","LoadDataDB  !!!#@!4324");
        mswipeArraylistData = new ArrayList<Packages>();
        openDB();
        mcursor = myDB.getTableWordsAllRows();

        if (mcursor.moveToFirst()){
            do {
                Packages packages = new Packages();
                packages.word = mcursor.getString(1);
                packages.desc = mcursor.getString(2);
                packages.ok = mcursor.getInt(3);
                mswipeArraylistData.add(packages);

            }while (mcursor.moveToNext());
        }
    }
    //umieszczanie danych w bazie
    private void insertDataDB() {
        openDB();
        myDB.deleteTableWordsAll();
        //mcursor = myDB.getTableWordsAllRows();
       /* if (mcursor.moveToFirst()){
            do {
                Packages packages = new Packages();
                packages.word = mcursor.getString(1);
                packages.desc = mcursor.getString(2);
                packages.ok = mcursor.getInt(3);
             //   mswipeArraylistData.add(packages);

            }while (mcursor.moveToNext());
        } */
       mcursor=myDB.getTableWordsAllRows();
       mcursor.moveToFirst();
        for (int i = 0; i <mswipeArraylistData.size() ; i++) {
            myDB.insertTableWordstRow(mswipeArraylistData.get(i).word,mswipeArraylistData.get(i).desc,mswipeArraylistData.get(i).ok);
            mcursor.moveToNext();
        }
    }

    //ustawienie menu po prawej stronie
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.word_list_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override // przejscie do aktywnosci powodujacej dodannie nowego slowa do listy
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.mainBTadd:
                Intent i = new Intent(getBaseContext(),ActivityAddWord.class);
              //  i.putExtra("package_name",package_name);
                startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    // przejscie do aktywnosci powtarzajacej slowka
    public void onClickWords(View view) {

        if (myDB.getTableWordsOKLenght()>0) {
            alertDialog();
        }else{
            final Intent intent =new Intent(ActivityWordList.this,ActivityWords.class);
            intent.putExtra("mmsgdlg",true);
            if (BuildConfig.PAID_VERSION==false) {
                if (interstitialAd.isLoaded() || interstitialAd.isLoading()) {
                    interstitialAd.show();
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            // Code to be executed when the interstitial ad is closed.
                            startActivity(intent);
                        }

                    });
                } else {
                    startActivity(intent);
                }
            }else{
                startActivity(intent);
            }

        }
    }
    //alert dialog pytajacy czy chcemy powtorzyc wszystkie slowka czy tez wyswietlic tylko te ktorych nie pamiętamy
    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AppCompatAlertDialogStyle);
        String load= getString(R.string.wordslloadall);
        String yes=getString(R.string.wordslloadyes);
        String no=getString(R.string.wordslloadno);
        builder.setMessage(""+load);

        builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                final Intent intent =new Intent(ActivityWordList.this,ActivityWords.class);
                intent.putExtra("mmsgdlg",true);
                if (BuildConfig.PAID_VERSION==false) {
                    if (interstitialAd.isLoaded() || interstitialAd.isLoading()) {
                        interstitialAd.show();
                        interstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                // Code to be executed when the interstitial ad is closed.
                                startActivity(intent);
                            }

                        });
                    } else {
                        startActivity(intent);
                    }
                }else{
                    startActivity(intent);
                }
                dialog.dismiss();
            }

        });

        builder.setNegativeButton(no, new DialogInterface.OnClickListener() {


            public void onClick(DialogInterface dialog, int which) {
                Intent intencja =new Intent(ActivityWordList.this,ActivityWords.class);
                intencja.putExtra("mmsgdlg",false);
                startActivity(intencja);

                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /////////SLIDE MENU///////////////
    class ListAdapter extends SwipeListAdapter {
        private ArrayList<Packages> listData;

        public ListAdapter(ArrayList<Packages> listData) {
            this.listData = (ArrayList<Packages>) listData.clone();
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
            if (convertView == null) {
                convertView = View.inflate(getBaseContext(), R.layout.style_word_list_list, null);
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                viewHolder.desc = (TextView) convertView.findViewById(R.id.desc);
                viewHolder.modify = (Button) convertView.findViewById(R.id.modify);
                viewHolder.delete = (Button) convertView.findViewById(R.id.delete);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(listData.get(position).word);
            viewHolder.desc.setText(listData.get(position).desc);
            return super.bindView(position, convertView);
        }

    }
    //otwieranie bazy danych
    private void openDB() {
        // TODO Auto-generated method stub
        myDB = new DBAdapter(this);
        myDB.open();
    }
    // zamykanie bazy danych
    private void closeDB() {
        // TODO Auto-generated method stub
        myDB.close();
    }
    // zapisanie danych do PLIKU - pliku JSON
    protected void onDestroy() {
        super.onDestroy();
           zlobp = new StringToJSON(package_name);
        String mfolder ="";
        if (package_own==0) {
            String arr[] = flag_source.split("/", 3);
            mfolder = arr[1];
        }
           zlobp.SaveDATA(mswipeArraylistData,mfolder);
        closeDB();
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("package_name",package_name);
        savedInstanceState.putString("flag_source",flag_source);
        savedInstanceState.putInt("package_own",package_own);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        onCreate(Bundle.EMPTY);
    }
}
