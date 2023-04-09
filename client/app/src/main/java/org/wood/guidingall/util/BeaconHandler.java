package org.wood.guidingall.util;

import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;
import org.wood.guidingall.tools.JSON;
import org.wood.guidingall.tools.User;

/**
 * Created by Danny on 2015/8/10.
 */
public class BeaconHandler extends Handler{
    public static final int MSG_SEND_TO_SERVER = 0;
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        switch (msg.what) {
            case MSG_SEND_TO_SERVER:
                if(msg.obj == null)
                    sendToServer();
                else
                    User.getInstance().send((String)msg.obj);
                break;
        }
    }

    private void sendToServer() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSON.KEY_STATUS, JSON.STATUS_POSITION);
            jsonObject.put(JSON.KEY_USERNAME, User.getInstance().getUserName());
            jsonObject.put(JSON.KEY_LOCATION, User.getInstance().getUserLocation());
            User.getInstance().send(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
