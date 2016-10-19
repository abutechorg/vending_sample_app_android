package com.mastercard.labs.unattended.vending.demo;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VendingFailFragment extends VendingFragment {

    @Bind(R.id.vending_fail_message)
    TextView vendingFailMessage;

    public static final String KEY_FAIL_MESSAGE = "KEY_FAIL_MESSAGE";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vending_fail, container, false);
        ButterKnife.bind(this, rootView);
        vendingFailMessage.setText(getArguments().getString(KEY_FAIL_MESSAGE));


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

}
