package org.wood.guidingall.tools;

/**
 * Created by Danny on 2014/10/30.
 */
public class JSON {
    public static final String KEY_USERNAME = "userName";
    public static final String KEY_USERPASSWD = "userPasswd";
    public static final String KEY_OTHER_USERNAME = "otherUserName";
    public static final String KEY_RESULT = "result";
    public static final String KEY_RESULT_MESSAGE = "resultMessage";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_CONNECTED_USERS = "connectedUsers";
    public static final String KEY_ISFRIEND = "isFriend";
    public static final String KEY_UNKNOWN = "unknown";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_NEED_TO_REPLY = "needToRelpy";
    public static final String KEY_NEED_TO_REQUEST = "needToRequest";
    public static final String KEY_ITEM = "item";
    public static final String KEY_ITEM_NAME = "itemName";
    public static final String KEY_ITEM_UUID = "itemUuid";
    public static final String KEY_ITEM_MINOR = "itemMinor";
    public static final String KEY_ITEM_MAJOR = "itemMajor";
    public static final String KEY_BEACON_RSSI = "beaconRssi";


    public static final String KEY_STATUS = "status";
    public static final int STATUS_LOGOUT = 0;
    public static final int STATUS_POSITION = 1;
    public static final int STATUS_REQUEST_GET_USER_LIST = 2;
    public static final int STATUS_REQUEST_MESSAGE = 3;
    public static final int STATUS_REPLY_MESSAGE = 4;
    public static final int STATUS_REQUEST_GET_USER_ITEM_LIST = 5;
    public static final int STATUS_REQUEST_ITEM_SEARCH = 6;
    public static final int STATUS_BLE_SCAN_ITEM = 7;
    public static final int STATUS_REPLY_ITEM_FOUND_MESSAGE = 8;
    public static final int STATUS_REPLY_ITEM_POSITION = 9;
    public static final int STATUS_REMOVE_SCAN_ITEM = 10;

    public static final int STATUS_POST_LOST_ITEM = 11;
    public static final int STATUS_POST_LOST_DATA = 12;

    public static final String KEY_WANT_TO_KNOW_OTHER_USER_LOCATION = "我想知道你的位置，可以嗎?";
}
