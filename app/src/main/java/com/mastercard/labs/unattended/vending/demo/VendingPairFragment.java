/*
 * Copyright 2016 MasterCard International.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 * Neither the name of the MasterCard International Incorporated nor the names of its
 * contributors may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 */

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
