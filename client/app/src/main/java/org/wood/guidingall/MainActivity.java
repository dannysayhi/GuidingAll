package org.wood.guidingall;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.os.StrictMode;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.sails.engine.*;

import org.altbeacon.beacon.*;
import org.altbeacon.beacon.Beacon;
import org.json.JSONException;
import org.json.JSONObject;
import org.wood.guidingall.backend.ReceiveMsgSession;
import org.wood.guidingall.fragment.BeaconScanFragment;
import org.wood.guidingall.fragment.FindItemFragment;
import org.wood.guidingall.fragment.FindPeopleFragment;
import org.wood.guidingall.fragment.HomeFragment;
import org.wood.guidingall.fragment.InfoFragment;
import org.wood.guidingall.fragment.IndoorMapFragment;
import org.wood.guidingall.fragment.PlatformFragment;
import org.wood.guidingall.lost.ItemLostPost;
import org.wood.guidingall.lost.LostItemNotification;
import org.wood.guidingall.tools.Config;
import org.wood.guidingall.tools.JSON;
import org.wood.guidingall.tools.User;
import org.wood.guidingall.util.BeaconHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;


public class MainActivity extends Activity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks, BeaconConsumer{
    public static Context activity;
    public static boolean isBt5PostToFind = false;
    public static boolean isBt6PostToFind = false;

    private static final String TAG = "beaconRanging";
    private BeaconManager beaconManager;

    /**
     * Fragment managing the behaviors, interactions and presentation of the
     * navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in
     * {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Menu actionBarMenu;

    public static final int MENU_HOME = 0;
    public static final int MENU_STORE = 1;
    public static final int MENU_INDOOR_NAVIGATION = 2;
    public static final int MENU_FIND_PEOPLE = 3;
    public static final int MENU_FIND_ITEM = 4;
    public static final int MENU_TRACKING = 5;
    public static final int MENU_INFORMATION = 6;

    public static SAILS mSails;
    public static SAILSMapView mSailsMapView;

    private String nearestLocation = "";

    public Region regionA;
    public Region regionB;
    public Region regionC;
    public Region regionD;

    public ArrayList<Region> regionList;

    ArrayAdapter adapter;
    AlertDialog dialog;

    User user;
    public static BeaconHandler beaconHandler;

    public static boolean isLogin = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        beaconHandler = new BeaconHandler();
        loginDialog();
        BeaconManager.setDebug(true);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        BeaconManager.setAndroidLScanningDisabled(true);
        beaconManager.setBackgroundMode(true);
//        beaconManager.setBackgroundScanPeriod(1100l);  //setup the period before calling bind
//        beaconManager.setBackgroundBetweenScanPeriod(1500l);

        beaconManager.setBackgroundScanPeriod(3300l);  //setup the period before calling bind
        beaconManager.setBackgroundBetweenScanPeriod(1200l);

        beaconManager.bind(MainActivity.this);

        /**
         * beacon manager bind method was written in loginDialog function see {@link #loginDialog()} .
         */

        regionList = new ArrayList<>();

        regionA = new Region("regionA", Identifier.parse(Config.BT1_UUID),
                Identifier.fromInt(Config.BT1_MAJOR), Identifier.fromInt(Config.BT1_MINOR));

        regionB = new Region("regionB", Identifier.parse(Config.BT2_UUID),
                Identifier.fromInt(Config.BT2_MAJOR), Identifier.fromInt(Config.BT2_MINOR));

        regionC = new Region("regionC", Identifier.parse(Config.BT3_UUID),
                Identifier.fromInt(Config.BT3_MAJOR), Identifier.fromInt(Config.BT3_MINOR));

        /**
         *  the way to detect the new beacon
         */
        regionD = new Region("regionD", null, null, null);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(Config.BEACON_LAYOUT));


        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(R.id.navigation_drawer);

        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mSails = new SAILS(this);
        mSailsMapView = new SAILSMapView(this);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);

        if(!dialog.equals(null))
            if(dialog.isShowing())
                dialog.dismiss();

        try {
            if(user.isConnected())
                user.close();
        } catch (Exception ignored) {

        }

    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                for (Beacon beacon : beacons) {
                    boolean isLocationChanged;
                    Log.i(TAG, "The beacon:" + beacon.toString() + ", " + beacon.getDistance() + " meters away." + beacon.getRssi());
                    Log.d("TEST", beacon.getId2().toString());
                    switch (beacon.getId2().toInt()) {
                        case Config.MAJOR_LOCATION:
                            isLocationChanged = NearestLocation(beacon);

                            if (isLogin) {
                                if (isLocationChanged) {
                                    Log.i("BEACON", "Location changed: " + nearestLocation);
                                    user.setUserLocation(nearestLocation);
                                    beaconHandler.sendMessage(Message.obtain(null, BeaconHandler.MSG_SEND_TO_SERVER));
                                } else {
                                    Log.i("BEACON", "Location not changed");
                                }
                            }
                            break;

                        case Config.MAJOR_ITEM:
                            switch (beacon.getId1().toString()) {
                                case Config.BT5_UUID:
                                    if (isLogin) {
                                        if (beacon.getRssi() < -70 && !isBt5PostToFind && user.getUserName().equals("danny")) {
                                            LostItemNotification.notify(MainActivity.this, "小孩", 1);
                                        } else
                                            LostItemNotification.cancel(MainActivity.this);
                                    }
                                    break;

                                case Config.BT6_UUID:
                                    if (isLogin) {
                                        if (beacon.getRssi() < -70 && !isBt6PostToFind && user.getUserName().equals("danny")) {
                                            LostItemNotification.notify(MainActivity.this, "錢包", 1);
                                        } else {
                                            LostItemNotification.cancel(MainActivity.this);
                                        }
                                    }
                                    break;
                            }

                            if (isLogin) {
                                Log.i("regionList", "" + regionList.size());
                                for (Region aRegion : regionList) {
                                    if (aRegion.getId1().equals(beacon.getId1()))
                                        sendBeaconToServer(beacon);
                                }

                            }
                            break;
                    }
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(regionA);
            beaconManager.startRangingBeaconsInRegion(regionB);
            beaconManager.startRangingBeaconsInRegion(regionC);
            beaconManager.startRangingBeaconsInRegion(regionD);
        } catch (RemoteException e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        switch(position) {
            case MENU_HOME:
                switchContent(HomeFragment.newInstance(), false, MENU_HOME);
                break;

            case MENU_STORE:
                switchContent(BeaconScanFragment.newInstance(), false, MENU_STORE);
                break;

            case MENU_INDOOR_NAVIGATION:
                switchContent(IndoorMapFragment.newInstance(MENU_INDOOR_NAVIGATION, user), false, MENU_INDOOR_NAVIGATION);
                break;

            case MENU_FIND_PEOPLE:
                switchContent(FindPeopleFragment.newInstance(MENU_FIND_PEOPLE, user), false, MENU_FIND_PEOPLE);
                break;

            case MENU_FIND_ITEM:
                switchContent(FindItemFragment.newInstance(MENU_FIND_ITEM, user), false, MENU_FIND_ITEM);
                break;

            case MENU_TRACKING:
                switchContent(PlatformFragment.newInstance(), false, MENU_TRACKING);
                break;

            case MENU_INFORMATION:
                switchContent(InfoFragment.newInstance(), false, MENU_INFORMATION);
                break;
        }
    }

    public void switchContent(Fragment fragment, Boolean backStack, int fragmentPosition) {
        Bundle bundle = new Bundle();
        bundle.putInt("FragmentPosition", fragmentPosition);
        if(!fragment.isAdded())
            fragment.setArguments(bundle);
        if (backStack) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case MENU_HOME:
                mTitle = getResources().getStringArray(R.array.nav_drawer_items)[MENU_HOME];
                break;
            case MENU_STORE:
                mTitle = getResources().getStringArray(R.array.nav_drawer_items)[MENU_STORE];
                break;
            case MENU_INDOOR_NAVIGATION:
                mTitle = getResources().getStringArray(R.array.nav_drawer_items)[MENU_INDOOR_NAVIGATION];
                break;
            case MENU_FIND_PEOPLE:
                mTitle = getResources().getStringArray(R.array.nav_drawer_items)[MENU_FIND_PEOPLE];
                break;
            case MENU_FIND_ITEM:
                mTitle = getResources().getStringArray(R.array.nav_drawer_items)[MENU_FIND_ITEM];
                break;
            case MENU_TRACKING:
                mTitle = getResources().getStringArray(R.array.nav_drawer_items)[MENU_TRACKING];
                break;
            case MENU_INFORMATION:
                mTitle = getResources().getStringArray(R.array.nav_drawer_items)[MENU_INFORMATION];
                break;


        }
    }


    public void restoreActionBar() {
        android.app.ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            actionBar.setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }

        restoreLoginMenuItem();
    }

    public void restoreLoginMenuItem() {
        MenuItem loginItem = actionBarMenu.findItem(R.id.action_account_settings);
        if(isLogin)
            loginItem.setTitle(user.getUserName());
        else
            loginItem.setTitle(getString(R.string.action_account_settings));
    }

    AlertDialog getAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    AlertDialog getListViewAlertDialog(String title, ArrayAdapter adapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false)
                .setTitle(title)
                .setCancelable(true)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!mSailsMapView.getCurrentBrowseFloorName().equals(mSails.getFloorNameList().get(which)))
                            mSailsMapView.loadFloorMap(mSails.getFloorNameList().get(which));
                    }
                });

        return builder.create();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            actionBarMenu = menu;
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_ip_settings:
                final String PREFS_NAME = "IPAddressPrefsFile";
                final String PREF_IPADDRESS = "IPAddress";
                final EditText ipEditText = new EditText(MainActivity.this);

                final String IPAddress = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(PREF_IPADDRESS, null);

                if(IPAddress != null) {
                    ipEditText.setText(IPAddress);
                }

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
                ipEditText.setLayoutParams(lp);
                ipEditText.setHint(R.string.txtHintIpAppress);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false)
                        .setTitle(getString(R.string.action_settings)).setView(ipEditText)
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                sharedPreferences.edit().putString(PREF_IPADDRESS, ipEditText.getText().toString())
                                        .apply();

                                Config.IPADDRESS = ipEditText.getText().toString();
                                dialog.dismiss();
                            }
                        }).create().show();
                break;

            case R.id.action_floor_choice:
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mSails.getFloorDescList());
                dialog = getListViewAlertDialog(getString(R.string.choice_floor_message), adapter);
                dialog.show();
                break;

            case R.id.action_account_settings:
                if(!isLogin) loginDialog();
                else logoutDialog();
                break;

            case R.id.action_item_settings:

                /*
                // Create Object of Dialog class
                final Dialog itemRegister = new Dialog(this);
                // Set GUI of login screen
                itemRegister.setContentView(R.layout.item_dialog);
                itemRegister.setTitle(getString(R.string.item_settings));

                // Init button of login GUI
                Button btnRegister = (Button) itemRegister.findViewById(R.id.btnRegister);
                Button btnCancel2 = (Button) itemRegister.findViewById(R.id.btnCancel2);
                final EditText txtItem = (EditText)itemRegister.findViewById(R.id.txtItem);
                final EditText txtUUID = (EditText)itemRegister.findViewById(R.id.txtUUID);

                // Attached listener for login GUI button
                btnRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(txtItem.getText().toString().trim().length() > 0 && txtUUID.getText().toString().trim().length() > 0)
                        {
                            // Validate Your login credential here than display message
                            Toast.makeText(MainActivity.this,
                                    "Sucessfull", Toast.LENGTH_LONG).show();

                            // Redirect to dashboard / home screen.
                            itemRegister.dismiss();
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,
                                    "Please enter Username and Password", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                btnCancel2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemRegister.dismiss();
                    }
                });

                // Make dialog box visible.
                itemRegister.show();

                */

                Intent intent = new Intent(MainActivity.this, ItemLostPost.class);
                startActivity(intent);

                break;
            case R.id.action_settings:
                languageChoiceDialog();
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    int nearestMinor = -1;
    double nearestRssi = -999;
    int savedMinor = -1;

    private boolean NearestLocation(Beacon beacon) {
        Log.i("NearestLocation", beacon.toString());
        int thisMinor = beacon.getId3().toInt();
        double thisRssi = beacon.getRssi();
        boolean isMinorChanged = true;

        if(nearestMinor == thisMinor) {
            nearestRssi = thisRssi;
        }

        if(nearestRssi == -999) {
            nearestMinor = thisMinor;
            nearestRssi = thisRssi;
        }


        if(nearestRssi < thisRssi) {
            nearestMinor = thisMinor;
            nearestRssi = thisRssi;
        }

        switch (nearestMinor){
            case Config.BT1_MINOR:
                nearestLocation = Config.BT1_PLACE;
                break;
            case Config.BT2_MINOR:
                nearestLocation = Config.BT2_PLACE;
                break;
            case Config.BT3_MINOR:
                nearestLocation = Config.BT3_PLACE;
                break;
        }

        isMinorChanged = savedMinor != nearestMinor;

        savedMinor = nearestMinor;

        return isMinorChanged;
    }

    public String getNearestLocation() { return nearestLocation; }

    //    int collectCount = 0;
    int tempRssi = 0;
    String tempLocation;
    public void sendBeaconToServer(Beacon beacon) {

        Log.i("sendBeaconToServer", "Ready");
        if(tempRssi == 0) {
            tempRssi = beacon.getRssi();
            tempLocation = nearestLocation;
        }

        if(tempLocation.equals(nearestLocation)) {
            tempRssi = beacon.getRssi();
        }

        if(tempRssi < beacon.getRssi()) {
            tempRssi = beacon.getRssi();
            tempLocation = nearestLocation;
        }


        Log.i("sendBeaconToServer","Send");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSON.KEY_STATUS, JSON.STATUS_REPLY_ITEM_FOUND_MESSAGE);
            jsonObject.put(JSON.KEY_USERNAME, user.getUserName());
            jsonObject.put(JSON.KEY_ITEM_UUID, beacon.getId1());
            jsonObject.put(JSON.KEY_BEACON_RSSI,beacon.getRssi());
            jsonObject.put(JSON.KEY_LOCATION, nearestLocation);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        beaconHandler.sendMessage(Message.obtain(null, BeaconHandler.MSG_SEND_TO_SERVER, jsonObject.toString()));
    }

    public void loginDialog() {
        final String PREFS_NAME = "LoginPrefsFile";
        final String PREF_USERNAME = "userName";
        final String PREF_PASSWORD = "userPassword";

        // Create Object of Dialog class
        final Dialog login = new Dialog(this);
        // Set GUI of login screen
        login.setContentView(R.layout.login_dialog);
        login.setCancelable(false);
        login.setTitle(getString(R.string.action_account_settings));
        // Init button of login GUI
        Button btnLogin = (Button) login.findViewById(R.id.btnLogin);
        Button btnCancel = (Button) login.findViewById(R.id.btnCancel);
        final EditText txtUsername = (EditText)login.findViewById(R.id.txtUsername);
        final EditText txtPassword = (EditText)login.findViewById(R.id.txtPassword);

        String userName = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(PREF_USERNAME, null);
        String userPassword = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(PREF_PASSWORD, null);

        if(userName != null && userPassword != null) {
            txtUsername.setText(userName);
            txtPassword.setText(userPassword);
        }

        // Attached listener for login GUI button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.dismiss();
                try {
                    SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    sharedPreferences.edit().putString(PREF_USERNAME, txtUsername.getText().toString())
                            .putString(PREF_PASSWORD, txtPassword.getText().toString())
                            .apply();

                    JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject.put(JSON.KEY_USERNAME, txtUsername.getText());
                        jsonObject.put(JSON.KEY_USERPASSWD, txtPassword.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ProgressDialog loadingDialog;
                    loadingDialog = new ProgressDialog(MainActivity.this);
                    loadingDialog.setTitle(getString(R.string.loading));
                    loadingDialog.setMessage(getString(R.string.wait));
                    loadingDialog.setCancelable(false);
                    loadingDialog.setIndeterminate(true);
                    loadingDialog.show();

                    user = User.getInstance();
                    user.connect();
                    user.send(jsonObject.toString());
                    JSONObject replyJSON = new JSONObject(user.receive());

                    if (isLogin = replyJSON.getBoolean(JSON.KEY_RESULT)) {
                        user.setUserName(replyJSON.getString(JSON.KEY_USERNAME));
                        user.setUserLocation(JSON.KEY_UNKNOWN);

                        restoreLoginMenuItem();
                        new Thread(new ReceiveMsgSession(MainActivity.activity, user, beaconManager)).start();

                        loadingDialog.dismiss();
                        getAlertDialog("Hi " + user.getUserName(), replyJSON.getString(JSON.KEY_RESULT_MESSAGE)).show();

                    } else {
                            loadingDialog.dismiss();
                            getAlertDialog(replyJSON.getString(JSON.KEY_RESULT_MESSAGE), replyJSON.getString(JSON.KEY_RESULT_MESSAGE)).show();
                            user.close();
                    }
                } catch (IOException | JSONException e) {
                            e.printStackTrace();
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            login.dismiss();
                    }
        });

        login.show();
    }

    public void logoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setTitle(getString(R.string.Warn))
                .setMessage(getString(R.string.logoutWarnMessage) + user.getUserName() + "?")
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        isLogin = true;
                    }
                });


        builder.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                JSONObject logoutObject = new JSONObject();
                try {
                    logoutObject.put(JSON.KEY_STATUS, JSON.STATUS_LOGOUT);
                    user.send(logoutObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                user.close();
                isLogin = false;
                restoreLoginMenuItem();
            }
        });
        builder.show();
    }

    Locale locale = new Locale("zh","TW");
    public void languageChoiceDialog() {

        final CharSequence[] items = {"繁體中文", "English"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.languageChoice));

        builder.setSingleChoiceItems(items,-1,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                if ("繁體中文".equals(items[which]))
                    locale = new Locale("zh","TW");
                else if ("English".equals(items[which]))
                    locale = new Locale("en");

                Locale.setDefault(locale);
            }
        });

        builder.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
                restoreActionBar();

            }
        });

        builder.show();

    }

}
