package com.fiszki.free;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fiszki.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class BannerFragment extends Fragment {
    private AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_banner, container, false);
    }
    public void onStart() {
        super.onStart();
        View v =getView();
        if (v!=null) {

            mAdView = v.findViewById(R.id.adView);
            AdRequest adRequestbanner = new AdRequest.Builder().build();
            mAdView.loadAd(adRequestbanner);
        }
    }

}
