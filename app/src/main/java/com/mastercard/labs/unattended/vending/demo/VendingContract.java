package com.mastercard.labs.unattended.vending.demo;

import android.app.Activity;
import android.os.Bundle;

import com.mastercard.labs.unattendedsdk.VendingMessage;

/**
 * Created by jameslian on 27/6/16.
 */
public interface VendingContract {


    interface View {
        Activity getActivity();

        void setUserActionsListener(VendingContract.UserActionsListener userActionsListener);

        void showErrorMessage(VendingMessage vendingMessage);

        void showConnecting();

        void showMakeSelection();

        void showRequestingAuth();

        void showApproving();

        void showVending();

        void showConnectingFail();

        void showVendingSuccess(int amount);

        void close();


    }

    interface UserActionsListener {


        void onCreate(Bundle bundle);

        void onResume();

        void onPause();

        void onDestroy();
    }
}
