package com.mastercard.labs.unattended.vending.demo;

import android.app.Activity;
import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Created by jameslian on 16/8/16.
 */
public class DrawerListAdapter extends ArrayAdapter<String> {
    private static final String TAG = "DrawerListAdapter";

    public static final String ITEM_SETTINGS = "Settings";

    private final String[] items;

    public DrawerListAdapter(Context context, String[] items) {
        super(context, R.layout.drawer_list_item, R.id.drawer_item);
        DemoApplication demoAppl = DemoApplication.getApplication((Activity) this.getContext());
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public String getItem(int position) {
        return items[position];
    }

}
