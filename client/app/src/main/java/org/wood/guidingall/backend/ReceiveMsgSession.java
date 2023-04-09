package org.wood.guidingall.backend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;
import org.wood.guidingall.MainActivity;
import org.wood.guidingall.lost.LostData;
import org.wood.guidingall.tools.Config;
import org.wood.guidingall.tools.User;
import org.wood.guidingall.fragment.FindItemFragment;
import org.wood.guidingall.tools.JSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Danny on 2014/11/3.
 */
public class ReceiveMsgSession implements Runnable {
    public static String receiveMsg = null;
    public static String findUserReceiveMsg = null;
    public static String userItemListReceiveMsg = null;
    public static String requestMsg = null;
    public static String replyMsg = null;
    public static String itemFoundMessage = null;
    public static String itemPositionMessage = null;
    public String itemLocaiotn = null;
    public String itemUuid = null;
    public static String removeScanItemMessage = null;

    private int itemMinor;
    private int itemMajor;
    private String itemName = null;

    private User user;
    private Context context;
    String userName;
    String otherUserName;

    BeaconManager beaconManager;

    public ReceiveMsgSession(Context context, User user, BeaconManager beaconManager) {
        this.user = user;
        this.context = context;
        this.beaconManager = beaconManager;
    }

    @Override
    public void run() {
            while (true) {
                String tempMsg;
                try {
                    if((tempMsg = user.receive())== null)
                        continue;
                    if(tempMsg.equals(""))
                        break;

                    JSONObject receiveObject = new JSONObject(tempMsg);
                    int status = receiveObject.getInt(JSON.KEY_STATUS);

                    Log.i("receive", receiveObject.toString());

                    switch (status) {
                        case JSON.STATUS_REQUEST_GET_USER_LIST:
                            ReceiveMsgSession.findUserReceiveMsg = tempMsg;
                            break;
                        case JSON.STATUS_REQUEST_MESSAGE:
                            ReceiveMsgSession.requestMsg = tempMsg;
                            requestMsgHandler();
                            break;
                        case JSON.STATUS_REPLY_MESSAGE:
                            ReceiveMsgSession.replyMsg = tempMsg;
                            break;

                        case JSON.STATUS_REQUEST_GET_USER_ITEM_LIST:
                            ReceiveMsgSession.userItemListReceiveMsg = tempMsg;
                            break;

                        case JSON.STATUS_BLE_SCAN_ITEM:
                            bleScanItemProgress(tempMsg);
                            break;

                        case JSON.STATUS_REPLY_ITEM_FOUND_MESSAGE:
                            ReceiveMsgSession.itemFoundMessage = tempMsg;
                            break;

                        case JSON.STATUS_REPLY_ITEM_POSITION:
                            ReceiveMsgSession.itemPositionMessage = tempMsg;
                            itemPositionHandler();
                            break;

                        case JSON.STATUS_REMOVE_SCAN_ITEM:
                            ReceiveMsgSession.removeScanItemMessage = tempMsg;
                            removeBleScanItemProgress(tempMsg);
                            break;

                        default:
                            ReceiveMsgSession.receiveMsg = tempMsg;
                            break;
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
    }

    private void requestMsgHandler() {

        JSONObject receiveObject;
        String tempMsg;
        while (true) {
            if( !(tempMsg = ReceiveMsgSession.requestMsg).equals(null)) {
                ReceiveMsgSession.requestMsg = null;
                break;
            }
        }

        try {
            receiveObject = new JSONObject(tempMsg);
            userName = receiveObject.getString(JSON.KEY_USERNAME);
            otherUserName = receiveObject.getString(JSON.KEY_OTHER_USERNAME);
            requestMsg = receiveObject.getString(JSON.KEY_MESSAGE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getRequestAlertDialog(otherUserName + "for your message", requestMsg).show();
            }
        });

    }

    private AlertDialog itemDialog;
    List<AlertDialog> dialogList = new ArrayList<>();

    private void itemPositionHandler() {
        JSONObject receiveObject;
        String tempMsg;
        while (true) {
            if( !(tempMsg = ReceiveMsgSession.itemPositionMessage).equals(null) ) {
                ReceiveMsgSession.itemPositionMessage = null;
                break;
            }
        }

        try {
            receiveObject = new JSONObject(tempMsg);
            itemLocaiotn = receiveObject.getString(JSON.KEY_LOCATION);
            itemUuid = receiveObject.getString(JSON.KEY_ITEM_UUID);
            itemMinor = receiveObject.getInt(JSON.KEY_ITEM_MINOR);
            itemMajor = receiveObject.getInt(JSON.KEY_ITEM_MAJOR);
            itemName = receiveObject.getString(JSON.KEY_ITEM_NAME);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog tempDialog;
                if(!itemName.equals(null)) {
                    if(dialogList.size() > 0) {
                        dialogList.get(0).dismiss();
                        dialogList.remove(0);
                    }

                    itemDialog = getItemFoundAlertDialog("Find out the item", "Item: " + itemName + "in: " + itemLocaiotn);
                    dialogList.add(itemDialog);

                    itemDialog.show();
                }
            }
        });
    }

    private void bleScanItemProgress(String tempMsg) throws JSONException {
        JSONObject replyObject = new JSONObject(tempMsg);

        itemName = replyObject.getString(JSON.KEY_ITEM_NAME);
        itemUuid = replyObject.getString(JSON.KEY_ITEM_UUID);
        itemMinor = replyObject.getInt(JSON.KEY_ITEM_MINOR);
        itemMajor = replyObject.getInt(JSON.KEY_ITEM_MAJOR);

        Region itemRegion = new Region(itemName, Identifier.parse(itemUuid), Identifier.fromInt(itemMajor), Identifier.fromInt(itemMinor));
        try {
            beaconManager.startRangingBeaconsInRegion(itemRegion);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(!((MainActivity)context).regionList.contains(itemRegion))
            ((MainActivity)context).regionList.add(itemRegion);

    }

    private void removeBleScanItemProgress(String tempMsg) throws JSONException {

        JSONObject replyObject = new JSONObject(tempMsg);
        String itemUuid = replyObject.getString(JSON.KEY_ITEM_UUID);
        String itemName = replyObject.getString(JSON.KEY_ITEM_NAME);
        int itemMajor = replyObject.getInt(JSON.KEY_ITEM_MAJOR);
        int itemMinor = replyObject.getInt(JSON.KEY_ITEM_MINOR);

        Region itemRegion = new Region(itemName, Identifier.parse(itemUuid), Identifier.fromInt(itemMajor), Identifier.fromInt(itemMinor));
        try {
            beaconManager.startRangingBeaconsInRegion(itemRegion);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(((MainActivity)context).regionList.contains(itemRegion))
            ((MainActivity)context).regionList.remove(itemRegion);
    }

    AlertDialog getRequestAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                            JSONObject sendObject;
                            sendObject = new JSONObject();
                            sendObject.put(JSON.KEY_STATUS, JSON.STATUS_REPLY_MESSAGE);
                            sendObject.put(JSON.KEY_USERNAME, userName);
                            sendObject.put(JSON.KEY_OTHER_USERNAME, otherUserName);
                            sendObject.put(JSON.KEY_MESSAGE, false);
                            sendObject.put(JSON.KEY_NEED_TO_REQUEST, false);

                            user.send(sendObject.toString());
                            ReceiveMsgSession.receiveMsg = null;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                try {
                    JSONObject sendObject;
                    sendObject = new JSONObject();
                    sendObject.put(JSON.KEY_STATUS, JSON.STATUS_REPLY_MESSAGE);
                    sendObject.put(JSON.KEY_USERNAME, userName);
                    sendObject.put(JSON.KEY_OTHER_USERNAME, otherUserName);
                    sendObject.put(JSON.KEY_MESSAGE, true);
                    sendObject.put(JSON.KEY_LOCATION, ((MainActivity) context).getNearestLocation());
                    sendObject.put(JSON.KEY_NEED_TO_REQUEST, false);

                    user.send(sendObject.toString());
                    ReceiveMsgSession.receiveMsg = null;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        return builder.create();

    }

    AlertDialog getItemFoundAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false)
                .setTitle(title)
                .setMessage(message)

                //按下No鍵，則在DB上建立新的lostData，為了是在網頁可以顯示新的資訊，For demo version
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO:
                        LostData lostData = new LostData();

                        switch (itemName) {
                            case Config.BT5_ITEM:   //小芳 (室外)
                                lostData.setOwner(user.getUserName());
                                lostData.setItemName(Config.BT5_ITEM);
                                lostData.setLocation("逢甲大學");
                                lostData.setPNGsrc("nofloordata.png");
                                lostData.setGeoPoint("[24.179261, 120.649530]");
                                break;

                            case Config.BT6_ITEM:   //深藍錢包 (室內)
                                lostData.setOwner(user.getUserName());
                                lostData.setItemName(Config.BT6_ITEM);
                                Log.d("itemLocaiotn", itemLocaiotn);
                                lostData.setLocation(itemLocaiotn);
                                lostData.setPNGsrc(floorExe(itemName, itemLocaiotn));
                                lostData.setGeoPoint("[24.165541, 120.643719]");

                                JSONObject sendObject = new JSONObject();
                                try {
                                    sendObject.put(JSON.KEY_STATUS, JSON.STATUS_POST_LOST_DATA);
                                    sendObject.put(LostData.OWNER, lostData.getOwner());
                                    sendObject.put(LostData.ITEMNAME, lostData.getItemName());
                                    sendObject.put(LostData.LOCATION, itemLocaiotn);
                                    Log.d("itemLocaiotn", lostData.getLocation());
                                    sendObject.put(LostData.PNGSRC, lostData.getPNGsrc());
                                    sendObject.put(LostData.GEOPOINT, lostData.getGeoPoint());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                user.send(sendObject.toString());
                                break;
                        }

                        dialog.dismiss();
                    }
                });


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                try {
                    JSONObject sendObject;
                    sendObject = new JSONObject();
                    sendObject.put(JSON.KEY_STATUS, JSON.STATUS_REMOVE_SCAN_ITEM);
                    sendObject.put(JSON.KEY_ITEM_UUID, itemUuid);
                    sendObject.put(JSON.KEY_ITEM_MAJOR, itemMajor);
                    sendObject.put(JSON.KEY_ITEM_MINOR, itemMinor);
                    user.send(sendObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ((MainActivity)context).switchContent(FindItemFragment.newInstance(MainActivity.MENU_FIND_ITEM, user, true, itemLocaiotn, itemName), false, MainActivity.MENU_FIND_ITEM);
            }
        });

        return builder.create();
    }

    int count = 0;
    String thisOwner = "";
    String tempLocation = "";

    private String floorExe(String owner, String location) {
        String res;
        String fileName = "floor";
        String fileCat = ".png";
        count++;

        if (thisOwner.equals("")) {
            thisOwner = owner;
        }

        if (!thisOwner.equals(owner)) {
            tempLocation = "";
            count = 0;
        }

        if (count == 4) {
            tempLocation = "";
            count = 1;
        }

        switch (location) {
            case Config.BT1_PLACE:
                tempLocation += "1";
                break;

            case Config.BT2_PLACE:
                tempLocation += "2";
                break;

            case Config.BT3_PLACE:
                tempLocation += "3";
                break;
        }

        res = fileName + tempLocation + fileCat;

        return res;
    }


}
