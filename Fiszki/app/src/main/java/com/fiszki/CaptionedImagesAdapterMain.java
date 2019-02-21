package com.fiszki;

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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CaptionedImagesAdapterMain extends RecyclerView.Adapter<CaptionedImagesAdapterMain.ViewHolder> {
    private         String[] name;
    private         int[] imgIds;
    private         String[] img_src;
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

    public CaptionedImagesAdapterMain(String[] name, int [] imgIds,String[] img_src) {
        this.name=name;
        this.imgIds=imgIds;
        this.img_src=img_src;
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
        ImageView imageView = cardView.findViewById(R.id.mainCVIVchangelenguage);

        if (imgIds!=null) {
            Drawable drawable = cardView.getResources().getDrawable(imgIds[i]);
            imageView.setImageDrawable(drawable);
            //pobrane z internetu adrresy
        }if (img_src!=null){
            SendHttpRequestTask(img_src[i],imageView);
        }
        TextView textView =  cardView.findViewById(R.id.mainCVTVchangelenguage);
        textView.setText(name[i]);

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
        return name.length;
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
