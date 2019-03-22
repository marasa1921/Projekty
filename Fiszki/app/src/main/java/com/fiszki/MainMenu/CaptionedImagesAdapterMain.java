package com.fiszki.MainMenu;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fiszki.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CaptionedImagesAdapterMain extends RecyclerView.Adapter<CaptionedImagesAdapterMain.ViewHolder> {
    private         String[] menuname;
    private         int[] imgsrcstorage;
    private         String[] imgsrcdownload;
    private static  Listener listener;

    public static interface Listener {
        public void onClick(int position);
    }
    public void setListener(Listener listener){
        this.listener=listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView=v;
        }
    }

    public CaptionedImagesAdapterMain(String[] name, int [] imgsrcstorage,String[] imgsrcdownload) {
        this.menuname=name;
        this.imgsrcstorage=imgsrcstorage;
        this.imgsrcdownload=imgsrcdownload;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView cv = (CardView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_captioned_image_main,viewGroup,false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        CardView cardView = viewHolder.cardView;
        ImageView imageView = cardView.findViewById(R.id.mainCVIVchangelanguage);

        if (imgsrcstorage!=null) {
            Drawable drawable = cardView.getResources().getDrawable(imgsrcstorage[i]);
            imageView.setImageDrawable(drawable);
            //pobrane z internetu adrresy
        }if (imgsrcdownload!=null){
            SendHttpRequestTask(imgsrcdownload[i],imageView);
        }
        TextView textView =  cardView.findViewById(R.id.mainCVTVchangelanguage);
        textView.setText(menuname[i]);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onClick(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuname.length;
    }
    private void SendHttpRequestTask(final String urldadres, final ImageView imageView) {
        @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, Bitmap> asyncTask = new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... strings) {
                try {
                    URL url = new URL(urldadres);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    return myBitmap;
                }catch (Exception e){
                    Log.d("TAG",e.getMessage());
                }
                return null;
            }
            @Override
            protected void onPostExecute(Bitmap result) {
                imageView.setImageBitmap(result);
            }
        };
        asyncTask.execute();
    }


}
