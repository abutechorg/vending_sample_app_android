package com.mastercard.labs.unattended.vending.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mastercard.labs.unattended.vending.demo.VendingPresenter.INTENT_KEY_ADDRESS;
import static com.mastercard.labs.unattended.vending.demo.VendingPresenter.INTENT_KEY_DESCRIPTION;
import static com.mastercard.labs.unattended.vending.demo.VendingPresenter.INTENT_KEY_LOCATION;
import static com.mastercard.labs.unattended.vending.demo.VendingPresenter.INTENT_KEY_NAME;

public class VendingPairFragment extends VendingFragment {

    @Bind(R.id.ven_mach_hdr)
    TextView ven_mach_hdr;

    @Bind(R.id.mach_name_details)
    TextView vend_mach_details;

    @Bind(R.id.ven_mach_loc)
    TextView ven_mach_loc;

    @Bind(R.id.mach_loc_details)
    TextView ven_mach_loc_details;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vending_pair, container, false);
        ButterKnife.bind(this, rootView);
        String name = getArguments().getString(INTENT_KEY_NAME);
        String address = getArguments().getString(INTENT_KEY_ADDRESS);
        String distance = getArguments().getString(INTENT_KEY_LOCATION);
        String description = getArguments().getString(INTENT_KEY_DESCRIPTION);
        ven_mach_hdr.setText(name);
        ven_mach_loc.setText(distance + "m");
        vend_mach_details.setText(description);
        ven_mach_loc_details.setText(address);

        return rootView;
    }


    @OnClick(R.id.btn_pair)
    public void onPairBtnClicked() {
        getVendingPresenter().connectMachine();
    }

}
