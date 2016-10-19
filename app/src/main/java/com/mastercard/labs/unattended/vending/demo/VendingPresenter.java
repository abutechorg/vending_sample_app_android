package com.mastercard.labs.unattended.vending.demo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mastercard.labs.unattended.services.PartnerService;
import com.mastercard.labs.unattended.services.dto.Approval;
import com.mastercard.labs.unattended.services.dto.FinalisedApproval;
import com.mastercard.labs.unattended.vending.demo.util.PrefHelper;
import com.mastercard.labs.unattendedsdk.ProcessStatus;
import com.mastercard.labs.unattendedsdk.VendController;
import com.mastercard.labs.unattendedsdk.VendControllerCallback;
import com.mastercard.labs.unattendedsdk.VendControllerFactory;
import com.mastercard.labs.unattendedsdk.VendingMessage;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by jameslian on 28/6/16.
 */
class VendingPresenter implements VendControllerCallback, VendingContract.UserActionsListener {
    private static final String TAG = VendingPresenter.class.getSimpleName();
    private VendingContract.View mView;
    private MainThread mainThread;
    private boolean enablingBluetooth;
    private VendController mVendController;

    private String mPeripheralSerialNumber;
    private String mPeripheralModelNumber;
    private String mPeripheralServiceId;

    private String mTransactionId;
    private String mApproveToken;
    private int mVendingAmount;

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public static final String INTENT_KEY_NAME = "INTENT_KEY_NAME";
    public static final String INTENT_KEY_LOCATION = "INTENT_KEY_LOCATION";
    public static final String INTENT_KEY_MODEL = "INTENT_KEY_MODEL";
    public static final String INTENT_KEY_SERVICE_ID = "INTENT_KEY_SERVICE_ID";
    public static final String INTENT_KEY_SERIAL = "INTENT_KEY_SERIAL";
    public static final String INTENT_KEY_ADDRESS = "INTENT_KEY_MODEL_ADDRESS";
    public static final String INTENT_KEY_DESCRIPTION = "INTENT_KEY_MODEL_DESCRIPTION";

    VendingPresenter(VendingContract.View view, MainThread mainThread) {

        if (view == null) throw new NullPointerException();
        this.mView = view;
        this.mainThread = mainThread;

    }

    @Override
    public void onCreate(Bundle bundle) {

        Log.d(TAG, "onCreate");
        mView.setUserActionsListener(this);

        if (bundle != null) {
            mPeripheralModelNumber = bundle.getString(INTENT_KEY_MODEL);
            mPeripheralSerialNumber = bundle.getString(INTENT_KEY_SERIAL);
            mPeripheralServiceId = bundle.getString(INTENT_KEY_SERVICE_ID);
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        if (mVendController != null)
            mVendController.disconnect();

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

    }
    //End VendingContract.UserActionsListener

    public void getPreferences() {

    }

    public void connectMachine() {

        mVendController = VendControllerFactory.newInstance(PrefHelper.getVendControllerType((Activity) mView), this,
                mPeripheralSerialNumber, mPeripheralModelNumber, mPeripheralServiceId, 5000, PrefHelper.getMockConfig((Activity) mView));

        mView.showConnecting();
        mVendController.connect();
    }

    @Override
    public void authRequest(int amount, String payload) {
        Log.d(TAG, "authRequest: ");
        mVendingAmount = amount;
        mApproveToken = payload;
        DemoApplication application = DemoApplication.getApplication((Activity) mView);
        final PartnerService partnerService = getPartnerService();
        partnerService.createApproval(amount, mApproveToken)
                .onSuccess(new Continuation<Approval, Task<Approval>>() {
                    @Override
                    public Task<Approval> then(Task<Approval> task) throws Exception {
                        //Action after successful approval
                        Approval result = task.getResult();
                        mTransactionId = result.getId();
                        mVendController.approveAuth(result.getApprovalPayload());
                        mView.showApproving();
                        return task;
                    }
                });
        mView.showRequestingAuth();
    }

    @Override
    public void connected() {
        mView.showMakeSelection();
    }

    @Override
    public void processCompleted(ProcessStatus status, int amount, String payload, VendingMessage vendingMessage) {
//        Log.d(TAG, "processCompleted: " + vendingMessage.name());
        completeSession(status, payload, null);
        if (status == ProcessStatus.SUCCESS)
            mView.showVendingSuccess(amount);
        else if (status == ProcessStatus.INVALID) {
            mView.showErrorMessage(vendingMessage);
        } else if (status == ProcessStatus.FAIL && vendingMessage.equals(VendingMessage.BLUETOOTH_NOT_ENABLED)) {
            mView.showErrorMessage(vendingMessage);
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mView.getActivity().startActivityForResult(enableBtIntent, 0);
        } else if (status == ProcessStatus.FAIL) {
            mView.showErrorMessage(vendingMessage);

        }

    }

    @Override
    public void disconnected() {
    }

    @Override
    public void timeoutWarning() {

    }

    private void completeSession(ProcessStatus status, String payload, Continuation continuation) {
        final PartnerService partnerService = getPartnerService();
        String state = status.getCode().equals(ProcessStatus.SUCCESS.getCode()) ? ProcessStatus.SUCCESS.getCode() : ProcessStatus.FAIL.getCode();
        partnerService.finaliseApproval(mTransactionId, state, payload)
                .onSuccess(new Continuation<FinalisedApproval, Task<FinalisedApproval>>() {
                    @Override
                    public Task<FinalisedApproval> then(Task<FinalisedApproval> task) throws Exception {
                        return task;
                    }
                })
                .continueWith(continuation);
    }


    private PartnerService getPartnerService() {

        DemoApplication application = DemoApplication.getApplication((Activity) mView);
        return application.getPartnerService();
    }

    @Override
    public Activity getActivity() {
        return (Activity) mView;

    }


    @Override
    public void invalidProduct() {
        // this is in the event of making product selection is invalid and the machine will callback
        // in invalid selection
    }
}