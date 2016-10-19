package com.mastercard.labs.unattended.vending.demo;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mastercard.labs.unattended.services.PartnerService;
import com.mastercard.labs.unattended.services.dto.Machine;
import com.mastercard.labs.unattended.vending.demo.util.LocationUtils;

import java.util.List;

import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Bind(R.id.left_drawer)
    ListView leftDrawer;

    @Bind(R.id.list_view_machines)
    ListView listview_machines;

//    @Bind(R.id.tv_payment_card_id)
//    TextView tvPaymentCardId;

    ArrayAdapter<Machine> mArrayAdapterMachines;

    View listViewHdr;

    private ActionBarDrawerToggle mDrawerToggle;
    static final int LOGIN_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        enableDrawer();
//        tvPaymentCardId.setText(DemoApplication.getApplication(MainActivity.this).getCardId());
        listViewHdr = (View) getLayoutInflater().inflate(R.layout.list_machines_hdr, null);
        initMachineList();
    }


    @Override
    protected void onResume() {
        super.onResume();
        listMachines();
    }

    private void initMachineList() {
        mArrayAdapterMachines = new ArrayAdapter<Machine>(this, R.layout.list_item_machines) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.list_item_machines, parent, false);
                }

                TextView tvHeader = (TextView) convertView.findViewById(R.id.tvHeader);
                TextView tvsubHeader = (TextView) convertView.findViewById(R.id.tvSubHeader1);

                Machine machine = getItem(position);
                tvHeader.setText(machine.getName());
                tvsubHeader.setText(machine.getDistance() + "m");

                return convertView;
            }
        };

        listview_machines.setAdapter(mArrayAdapterMachines);
        listview_machines.addHeaderView(listViewHdr);

        listview_machines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Machine selectedMachine = (Machine) adapterView.getAdapter().getItem(position);
                if (selectedMachine != null) {
                    Log.d(TAG, String.format("SelectedMachine Identifier:%s Model:%s Serial:%s", selectedMachine.getIdentifier(), selectedMachine.getModel(), selectedMachine.getSerial()));
                    startVendingActivity(selectedMachine.getName(), selectedMachine.getDistance(), selectedMachine.getModel(), selectedMachine.getServiceId(), selectedMachine.getSerial(), selectedMachine.getAddress(), selectedMachine.getDescription());
                }
            }
        });
    }

    private void listMachines() {
        try {

            Location location = LocationUtils.getInstance().getLocation();
            PartnerService partnerService = DemoApplication.getApplication(this).getPartnerService();
            partnerService.listNearbyMachines(location.getLatitude(), location.getLongitude())
                    .onSuccess(new Continuation<List<Machine>, Task<List<Machine>>>() {
                        @Override
                        public Task<List<Machine>> then(Task<List<Machine>> task) throws Exception {
                            Log.d(TAG, "then: machines count:" + task.getResult().size());
                            for (Machine machine : task.getResult()) {
                                Log.d(TAG, String.format("Machine(%s)Distance%f", machine.getName(), machine.getDistance()));
                            }
                            mArrayAdapterMachines.clear();
                            mArrayAdapterMachines.addAll(task.getResult());
                            mArrayAdapterMachines.notifyDataSetChanged();
                            return task;
                        }
                    })
                    .continueWith(new Continuation<Task<List<Machine>>, Void>() {
                        @Override
                        public Void then(Task<Task<List<Machine>>> task) throws Exception {
                            if (task.isFaulted()) {
                                Exception e = task.getError();
                                Log.e(TAG, e.getMessage(), e);
                            }

                            return null;
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "listMachines: ", e);
        }
    }


    private void enableDrawer() {
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);


        leftDrawer.setAdapter(new DrawerListAdapter(this, new String[]{DrawerListAdapter.ITEM_SETTINGS}));
        leftDrawer.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void startVendingActivity(String name, float distance, String modelName, String serviceId, String serial, String address, String description) {
        Intent tempIntent = new Intent(MainActivity.this, VendingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(VendingPresenter.INTENT_KEY_NAME, name);
        bundle.putString(VendingPresenter.INTENT_KEY_LOCATION, Float.toString(distance));
        bundle.putString(VendingPresenter.INTENT_KEY_SERVICE_ID,serviceId);
        bundle.putString(VendingPresenter.INTENT_KEY_MODEL, modelName);
        bundle.putString(VendingPresenter.INTENT_KEY_SERIAL, serial);
        bundle.putString(VendingPresenter.INTENT_KEY_ADDRESS, address);
        bundle.putString(VendingPresenter.INTENT_KEY_DESCRIPTION, description);
        tempIntent.putExtras(bundle);
        startActivity(tempIntent);
    }

    // TODO: 15/8/16 this is to be put to the vending activity
    // TODO: to find the two main colors from developer mastercard website
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //// TODO: 15/8/16 have to test when the user cancel
        if (requestCode == LOGIN_REQUEST && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: :" + data.getData());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position, id, view);
        }

    }

    private void selectItem(int position, long id, View view) {
        String textSelected = ((TextView) view).getText().toString();
        launchDrawerIntent(textSelected);

        drawerLayout.closeDrawer(leftDrawer);
    }

    private void launchDrawerIntent(String drawerItemTitle) {
        switch (drawerItemTitle) {
            case DrawerListAdapter.ITEM_SETTINGS:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
