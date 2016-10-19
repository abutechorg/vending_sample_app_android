package com.mastercard.labs.unattended.vending.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jameslian on 19/9/16.
 */
public class ReceiptActivity extends AppCompatActivity {
    public static final String INTENT_PRODUCT_PRICE = "INTENT_PRODUCT_PRICE";
    public static final String INTENT_PRODUCT_NAME = "INTENT_PRODUCT_NAME";
    public static final String INTENT_PRODUCT_VENDING = "INTENT_PRODUCT_VENDING";

    @Bind(R.id.price_hdr)
    TextView price_hdr;
    @Bind(R.id.price_detail)
    TextView price_detail;
    @Bind(R.id.product_name)
    TextView product_name;
    @Bind(R.id.product_price)
    TextView product_price;
    @Bind(R.id.product_total)
    TextView product_total;

    @Bind(R.id.divider_1)
    ImageView divider_1;
    @Bind(R.id.divider_2)
    ImageView divider_2;
    @Bind(R.id.divider_3)
    ImageView divider_3;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_receipt);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        populateValues(getIntent());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setElevation(0);

        }
    }

    private void populateValues(Intent intent) {
        float productPrice = intent.getFloatExtra(INTENT_PRODUCT_PRICE, 0.0f);
        String productName = intent.getStringExtra(INTENT_PRODUCT_NAME);
        String productMachine = intent.getStringExtra(INTENT_PRODUCT_VENDING);

        String productPriceFmt = String.format("SGD %.2f", productPrice);
        String productDateAndTime = new SimpleDateFormat("E , d MMM hh:mm").format(new Date());
        price_hdr.setText(productPriceFmt);
        price_detail.setText(productDateAndTime + "\n" + productMachine);
        product_name.setText(productName);
        product_total.setText(productPriceFmt);
        product_price.setText(productPriceFmt);

        divider_1.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        divider_2.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        divider_3.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }

    @Override
    public void onBackPressed() {
        Intent showMainActivityIntent = new Intent(this, MainActivity.class);
        showMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        startActivity(showMainActivityIntent);
//        super.onBackPressed();
    }
}
