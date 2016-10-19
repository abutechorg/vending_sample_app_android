package com.mastercard.labs.unattended.vending.demo;

import android.support.v4.app.Fragment;

public class VendingFragment extends Fragment {


    protected VendingPresenter getVendingPresenter() {
        return ((VendingActivity) getActivity()).getPresenter();
    }

}
