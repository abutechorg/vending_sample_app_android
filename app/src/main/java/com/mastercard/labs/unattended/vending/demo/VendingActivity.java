package com.mastercard.labs.unattended.vending.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import com.mastercard.labs.unattendedsdk.VendingMessage;

import butterknife.ButterKnife;

public class VendingActivity extends AppCompatActivity implements VendingContract.View {

    private static final String TAG = VendingActivity.class.getSimpleName();
    public static final int UNATTENDED_REQUEST_CODE = 8000;


    private VendingContract.UserActionsListener actionsListener;
    private VendingConnectFragment vendingConnectFragment;
    private VendingPairFragment vendingPairFragment;
    private VendingSelectionFragment vendingSelectionFragment;
    private VendingRequestAuthFragment vendingRequestAuthFragment;
    private VendingApprovingFragment vendingApprovingFragment;
    private VendingConnectFailFragment vendingConnectFailFragment;
    private VendingFailFragment vendingFailFragment;

    private VendingPresenter presenter;
    private boolean didSaveInstance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_vending);
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setElevation(0);

        }


        presenter = new VendingPresenter(VendingActivity.this,
                new MainThreadImpl(getMainLooper()));
        presenter.onCreate(getIntent().getExtras());

        vendingPairFragment = new VendingPairFragment();
        vendingPairFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, vendingPairFragment).commit();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onResumeFragments() {
        didSaveInstance = false;
        super.onResumeFragments();

    }

    @Override
    protected void onPause() {
        super.onPause();
        didSaveInstance = true;
        presenter.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }


    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void setUserActionsListener(VendingContract.UserActionsListener userActionsListener) {
        actionsListener = userActionsListener;
    }


    @Override
    public void showErrorMessage(final VendingMessage message) {
        Log.d(TAG, "showErrorMessage: ");
        if (didSaveInstance) {
            return;
        }

        final @StringRes int text;
        switch (message) {
            case VENDING_MACHINE_NOT_LOCATED:
                text = R.string.vending_machine_not_located;
                showConnectingFail();
                break;
            case VENDING_MACHINE_NOT_AVAILABLE:
                text = R.string.vending_machine_not_available;
                showConnectingFail();
                break;
            case VENDING_CANCELLED:
                text = R.string.vending_cancelled;
                break;
            case CONNECTION_TIMEOUT:
                text = R.string.vending_connection_timed_out;
                break;
            case DISCONNECTED:
                text = R.string.vending_disconnected;
                break;
            case BLUETOOTH_NOT_SUPPORTED:
                text = R.string.bluetooth_not_supported;
                showConnectingFail();
                break;
            case BLUETOOTH_NOT_ENABLED:
                text = R.string.bluetooth_not_enabled;
                showConnectingFail();
                break;
            case VENDING_DATA_NOT_AVAILABLE:
                text = R.string.vending_data_not_available;
                break;
            case VENDING_FAILED:
                text = R.string.vending_failed;
                break;
            case ALREADY_IN_TRANSACTION:
                text = R.string.vending_already_in_transaction;
                break;
            default:
                text = 0;
                break;
        }

        if (text == 0)
            return;

        showErrorMessage(getString(text));
    }


    public void showErrorMessage(final String message) {
        Bundle args = new Bundle();
        args.putString(vendingFailFragment.KEY_FAIL_MESSAGE, message);
        vendingFailFragment = new VendingFailFragment();
        vendingFailFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, vendingFailFragment).commit();

        if (didSaveInstance) {
            return;
        }
    }

    public VendingPresenter getPresenter() {
        return presenter;
    }


    @Override
    public void showConnecting() {
        vendingConnectFragment = new VendingConnectFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, vendingConnectFragment).commit();
    }

    @Override
    public void showMakeSelection() {
        vendingSelectionFragment = new VendingSelectionFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, vendingSelectionFragment).commit();
    }

    @Override
    public void showRequestingAuth() {
        vendingRequestAuthFragment = new VendingRequestAuthFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, vendingRequestAuthFragment).commit();
    }

    @Override
    public void showApproving() {
        vendingApprovingFragment = new VendingApprovingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, vendingApprovingFragment).commit();
    }

    @Override
    public void showConnectingFail() {
        vendingConnectFailFragment = new VendingConnectFailFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, vendingConnectFailFragment).commit();
    }

    @Override
    public void showVending() {
    }


    @Override
    public void showVendingSuccess(int amount) {
        navigateToReceiptPage(amount);
    }


    @Override
    public void close() {
        finish();
    }


    private void navigateToReceiptPage(int amount) {
        Log.d(TAG, "Navigating to thank you page");
        Intent showReceiptIntent = new Intent(this, ReceiptActivity.class);
        showReceiptIntent.putExtra(ReceiptActivity.INTENT_PRODUCT_PRICE, ((float) amount * 0.01f));
        showReceiptIntent.putExtra(ReceiptActivity.INTENT_PRODUCT_NAME, "Drink");
        showReceiptIntent.putExtra(ReceiptActivity.INTENT_PRODUCT_VENDING, "Snacks");
        showReceiptIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(showReceiptIntent);
    }
}
