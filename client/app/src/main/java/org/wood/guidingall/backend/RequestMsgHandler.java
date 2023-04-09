package org.wood.guidingall.backend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.wood.guidingall.tools.User;
import org.wood.guidingall.tools.JSON;


/**
 * Created by Danny on 2014/11/3.
 */
public class RequestMsgHandler implements Runnable {
    private User user;
    private Context context;
    String userName;
    String otherUserName;
    String requestMsg;

    public RequestMsgHandler(Context context, User user) {
        this.user = user;
        this.context = context;
    }

    @Override
    public void run() {
        JSONObject receiveObject;
        String tempMsg;
        while (true) {
            if( (tempMsg = ReceiveMsgSession.requestMsg) != null) {
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
                getRequestAlertDialog(otherUserName + " for your message ", requestMsg).show();
            }
        });
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


        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

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
                    sendObject.put(JSON.KEY_LOCATION, user.getUserLocation());
                    sendObject.put(JSON.KEY_NEED_TO_REQUEST, false);

                    user.send(sendObject.toString());
                    ReceiveMsgSession.receiveMsg = null;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


        return builder.create();

    };
}
