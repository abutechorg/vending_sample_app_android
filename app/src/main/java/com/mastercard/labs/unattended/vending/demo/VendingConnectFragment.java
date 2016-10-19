package com.mastercard.labs.unattended.vending.demo;


import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.ButterKnife;

public class VendingConnectFragment extends VendingFragment {


//    @Bind(R.id.mainContent)
    ImageView mainContent;

    private AnimationDrawable animationDrawable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vending_connect, container, false);
        ButterKnife.bind(this, rootView);


        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    void startAnimating() {
//        if (animationDrawable.isRunning())
//            return;
//
//        animationDrawable.start();
    }

    void stopAnimating() {
//        if (!animationDrawable.isRunning())
//            return;
//
//        animationDrawable.stop();
//        animationDrawable.selectDrawable(0);
    }
}