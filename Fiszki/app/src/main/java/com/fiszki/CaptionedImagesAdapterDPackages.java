package com.fiszki;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CaptionedImagesAdapterDPackages extends RecyclerView.Adapter<CaptionedImagesAdapterDPackages.ViewHolder> {
    private         String[] captionsname;
    private         String[] captionsdescription;
    private         int[]    packagedownloaded;
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

    public CaptionedImagesAdapterDPackages(String[] captionsname, String [] captionsdescription, int [] packagedownloaded) {
        this.captionsname=captionsname;
        this.captionsdescription=captionsdescription;
        this.packagedownloaded=packagedownloaded;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView cv = (CardView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_captioned_image_dpackages,viewGroup,false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        CardView cardView = viewHolder.cardView;
        TextView textView1 = cardView.findViewById(R.id.dpackagesCVTVname1);
        textView1.setText(captionsname[i]);
        TextView textView2 =  cardView.findViewById(R.id.dpackagesCVTVname2);
        textView2.setText(captionsdescription[i]);
        ImageView imageView = cardView.findViewById(R.id.dpackagesIVdownload);
        Drawable drawable = cardView.getResources().getDrawable(R.drawable.ic_file_download_24dp);
        if (packagedownloaded[i]==0) {
            imageView.setImageDrawable(drawable);
        }
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
        return captionsdescription.length;
    }


}
