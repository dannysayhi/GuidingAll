package org.wood.guidingall.handler;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wood.guidingall.LostData;
import org.wood.guidingall.ProjectData;
import org.wood.guidingall.db.GuidingAllDB;
import org.wood.guidingall.tools.JSON;
import org.wood.guidingall.tools.User;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Danny on 2014/11/2.
 */

public class ClientHandler implements Runnable {
    private static Logger logger = Logger.getLogger(ClientHandler.class);
    private static GuidingAllDB guidingAllDB;

    User client;
    ArrayList<User> clientList;

    public ClientHandler( ArrayList<User> userList, User user) throws IOException {
        PropertyConfigurator.configure("config//log4j.properties");
        Logger.getRootLogger().setLevel(Level.DEBUG);
        client = user;
        clientList = userList;
        guidingAllDB = new GuidingAllDB();
    }
    static int collectionNum = 0;
    static String clientWhoLost;
    static int tempRssi = 0;
    static String tempLocation;
    static String sentLocation = "";

    int itemMajor;
    int itemMinor;
    String itemUuid;

    synchronized void addCollectionNumber() {
        ClientHandler.collectionNum++;
    }

    synchronized void setCollectionNumber(int num) {
        ClientHandler.collectionNum = num;
    }

    synchronized void setWhoLostItem(String name) {
        ClientHandler.clientWhoLost = name;
    }

    synchronized void setTempRssi(int rssi) {
        ClientHandler.tempRssi = rssi;
    }

    synchronized void setTempLocation(String location) {
        ClientHandler.tempLocation = location;
    }

    synchronized String getTempLocation() {return tempLocation;}

    synchronized int getTempRssi(){return tempRssi;}

    synchronized String getSentLocation() {return sentLocation;}

    synchronized void setSentLocation(String location) {sentLocation = location;}

    @Override
    public void run() {
        logger.info("Log in client handler begin!");
        if(!clientList.contains(client))
            clientList.add(client);

        try {
            JSONObject receiveJSON;
            String receiveMessage;
            int status;
            while(client.isConnected()) {
                if((receiveMessage = client.receive()) == null) {
                    client.close();
                    break;
                }
                logger.debug("Client < " + client.getAccount() + " > say: " + receiveMessage);


                receiveJSON = new JSONObject(receiveMessage);
                status = receiveJSON.getInt(JSON.KEY_STATUS);

                switch(status) {
                    case JSON.STATUS_LOGOUT:
                        client.close();
                        break;

                    case JSON.STATUS_POSITION:
                        client.setLocation(receiveJSON.getString(JSON.KEY_LOCATION));
                        break;

                    case JSON.STATUS_REQUEST_GET_USER_LIST:
                        System.out.println(client.getAccount() + getUserListObject().toString());
                        client.send(getUserListObject().toString());
                        break;

                    case JSON.STATUS_REQUEST_MESSAGE:
                        String clientName = receiveJSON.getString(JSON.KEY_USERNAME);
                        String otherClientName = receiveJSON.getString(JSON.KEY_OTHER_USERNAME);
                        String message = receiveJSON.getString(JSON.KEY_MESSAGE);
                        boolean isNeedToRelpy = receiveJSON.getBoolean(JSON.KEY_NEED_TO_REPLY);
                        User otherClient = getClientByName(otherClientName);

                        JSONObject sendObject = new JSONObject();
                        sendObject.put(JSON.KEY_STATUS, JSON.STATUS_REQUEST_MESSAGE);
                        sendObject.put(JSON.KEY_USERNAME, otherClientName);
                        sendObject.put(JSON.KEY_OTHER_USERNAME, clientName);
                        sendObject.put(JSON.KEY_MESSAGE, message);
                        sendObject.put(JSON.KEY_NEED_TO_REPLY, isNeedToRelpy);

//                        client.send(otherClient.getSocket(), sendObject.toString());
                        otherClient.send(sendObject.toString());
                        System.out.println(client.getAccount() + sendObject.toString());
                        break;

                    case JSON.STATUS_REPLY_MESSAGE:
                        clientName = receiveJSON.getString(JSON.KEY_USERNAME);
                        otherClientName = receiveJSON.getString(JSON.KEY_OTHER_USERNAME);
                        otherClient = getClientByName(otherClientName);

                        boolean replyBoolean = receiveJSON.getBoolean(JSON.KEY_MESSAGE);
                        boolean isNeedToRequest = receiveJSON.getBoolean(JSON.KEY_NEED_TO_REQUEST);
                        String location = receiveJSON.getString(JSON.KEY_LOCATION);

                        sendObject = new JSONObject();
                        sendObject.put(JSON.KEY_STATUS, JSON.STATUS_REPLY_MESSAGE);
                        sendObject.put(JSON.KEY_USERNAME, otherClientName);
                        sendObject.put(JSON.KEY_OTHER_USERNAME, clientName);
                        sendObject.put(JSON.KEY_MESSAGE, replyBoolean);
                        sendObject.put(JSON.KEY_LOCATION, location);
                        sendObject.put(JSON.KEY_NEED_TO_REPLY, isNeedToRequest);

                        client.send(otherClient.getSocket(), sendObject.toString());
                        System.out.println(client.getAccount() + sendObject.toString());
                        break;

                    case JSON.STATUS_REQUEST_GET_USER_ITEM_LIST:
                        System.out.println(client.getAccount() + getUserItemListObject().toString());
                        client.send(getUserItemListObject().toString());
                        break;

                    case JSON.STATUS_REQUEST_ITEM_SEARCH:
                        itemUuid = receiveJSON.getString(JSON.KEY_ITEM_UUID);
                        itemMajor = receiveJSON.getInt(JSON.KEY_ITEM_MAJOR);
                        itemMinor = receiveJSON.getInt(JSON.KEY_ITEM_MINOR);

                        setWhoLostItem(receiveJSON.getString(JSON.KEY_USERNAME));

                        sendObject = new JSONObject();
                        sendObject.put(JSON.KEY_STATUS, JSON.STATUS_BLE_SCAN_ITEM);
                        sendObject.put(JSON.KEY_ITEM_NAME, guidingAllDB.getItemNameByUuid(itemUuid));
                        sendObject.put(JSON.KEY_ITEM_UUID, itemUuid);
                        sendObject.put(JSON.KEY_ITEM_MAJOR, itemMajor);
                        sendObject.put(JSON.KEY_ITEM_MINOR, itemMinor);

                        for (User aClient : clientList) {
                            aClient.send(sendObject.toString());
                        }
                        break;

                    case JSON.STATUS_REPLY_ITEM_FOUND_MESSAGE:
                        handleReplyItemFoundMessage(receiveJSON);
                        break;

                    case JSON.STATUS_REMOVE_SCAN_ITEM:
                        String itemUuid = receiveJSON.getString(JSON.KEY_ITEM_UUID);
                        int itemMinor = receiveJSON.getInt(JSON.KEY_ITEM_MINOR);
                        int itemMajor = receiveJSON.getInt(JSON.KEY_ITEM_MAJOR);

                        sendObject = new JSONObject();
                        sendObject.put(JSON.KEY_STATUS, JSON.STATUS_REMOVE_SCAN_ITEM);
                        sendObject.put(JSON.KEY_ITEM_NAME, guidingAllDB.getItemNameByUuid(itemUuid));
                        sendObject.put(JSON.KEY_ITEM_UUID, itemUuid);
                        sendObject.put(JSON.KEY_ITEM_MAJOR, itemMajor);
                        sendObject.put(JSON.KEY_ITEM_MINOR, itemMinor);

                        for (User aClient : clientList) {
                            aClient.send(sendObject.toString());
                        }
                        break;

                    case JSON.STATUS_POST_LOST_ITEM:
                        ProjectData projectData = new ProjectData();
                        projectData.setOwner(client.getAccount());
                        projectData.setName(receiveJSON.getString(ProjectData.NAME));
                        projectData.setLostAddr(receiveJSON.getString(ProjectData.ADDR));
                        projectData.setLostDate(receiveJSON.getString(ProjectData.DATE));
                        projectData.setInfo(receiveJSON.getString(ProjectData.INFO));
                        projectData.setPhone(receiveJSON.getString(ProjectData.PHONE));
                        projectData.setReward(receiveJSON.getInt(ProjectData.REWARD));

                        guidingAllDB.insertProjectData(projectData);

                        String lostUuid = receiveJSON.getString(ProjectData.UUID);
                        int lostMajor = receiveJSON.getInt(ProjectData.MAJOR);
                        int lostMinor = receiveJSON.getInt(ProjectData.MINOR);
                        String lostName = guidingAllDB.getItemNameByUuid(lostUuid);

                        setWhoLostItem(client.getAccount());

                        sendObject = new JSONObject();
                        sendObject.put(JSON.KEY_STATUS, JSON.STATUS_BLE_SCAN_ITEM);
                        sendObject.put(JSON.KEY_ITEM_NAME, lostName);
                        sendObject.put(JSON.KEY_ITEM_UUID, lostUuid);
                        sendObject.put(JSON.KEY_ITEM_MAJOR, lostMajor);
                        sendObject.put(JSON.KEY_ITEM_MINOR, lostMinor);

                        for (User aClient : clientList) {
                            aClient.send(sendObject.toString());
                        }
                        break;

                    case JSON.STATUS_POST_LOST_DATA:
                        String lostOwner = receiveJSON.getString(LostData.OWNER);
                        String itemName = receiveJSON.getString(LostData.ITEMNAME);
                        String itemLocation = receiveJSON.getString(LostData.LOCATION);
                        String PNGSrc = receiveJSON.getString(LostData.PNGSRC);
                        String geoPoint = receiveJSON.getString(LostData.GEOPOINT);

                        LostData lostData = new LostData();
                        lostData.setOwner(lostOwner);
                        lostData.setItemName(itemName);
                        lostData.setLocation(itemLocation);
                        lostData.setPNGsrc(PNGSrc);
                        lostData.setGeoPoint(geoPoint);

                        guidingAllDB.insertLostData(lostData);
                        break;

                    case JSON.STATUS_SOS:
                        location = receiveJSON.getString(JSON.KEY_LOCATION);
                        String caredUser = receiveJSON.getString(JSON.KEY_USERNAME);
                        setWhoLostItem(receiveJSON.getString(JSON.KEY_USERNAME));

                        sendObject = new JSONObject();
                        sendObject.put(JSON.KEY_STATUS, JSON.STATUS_FIND_PEOPLE);
                        sendObject.put(JSON.KEY_USERNAME, caredUser);
                        sendObject.put(JSON.KEY_LOCATION, location);

                        for (User aClient : clientList) {
                            aClient.send(sendObject.toString());
                        }
                        break;


                }
            }

        } catch(IOException | JSONException e) {
            logger.error("Can't receive the message from: " + client.getAccount());
            e.printStackTrace();
        } finally {
            client.close();
        }

        if(clientList.contains(client)) {
            clientList.remove(client);
            logger.info("A client closed and removed: " + client.getAccount());
        }

    }

    private JSONObject getUserListObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = getUserListArray();
        jsonObject.put(JSON.KEY_STATUS, JSON.STATUS_REQUEST_GET_USER_LIST);
        jsonObject.put(JSON.KEY_CONNECTED_USERS, jsonArray);

        return jsonObject;
    }

    private JSONArray getUserListArray() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (User anClientList : clientList) {
            if(anClientList.getAccount().compareTo(client.getAccount()) == 0)
                continue;

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON.KEY_USERNAME, anClientList.getAccount());
            jsonObject.put(JSON.KEY_ISFRIEND, guidingAllDB.isFriend(client.getAccount(), anClientList.getAccount()));
            jsonObject.put(JSON.KEY_LOCATION, anClientList.getLocation());
            jsonArray.put(jsonObject);
        }

        return jsonArray;
    }

    private User getClientByName(String name) {
        User client = null;
        for (User aClientList : clientList) {
            if (aClientList.getAccount().equals(name)) {
                client = aClientList;
                break;
            }
        }

        return client;
    }

    private JSONObject getUserItemListObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = getUserItemListArray();
        jsonObject.put(JSON.KEY_STATUS, JSON.STATUS_REQUEST_GET_USER_ITEM_LIST);
        jsonObject.put(JSON.KEY_ITEM, jsonArray);

        return jsonObject;
    }

    private JSONArray getUserItemListArray() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        ArrayList<String> itemList = guidingAllDB.getItemByUserName(client.getAccount());
        for (String anItemName : itemList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON.KEY_ITEM_NAME, anItemName);
            jsonArray.put(jsonObject);
        }

        return jsonArray;
    }

    private User getClientByAccount(String name) {
        User client = null;
        for (User aClientList : clientList) {
            if (aClientList.getAccount().equals(name)) {
                client = aClientList;
                break;
            }
        }
        return client;
    }

    private void handleReplyItemFoundMessage(JSONObject receiveJSON) throws JSONException {
        String foundClient = receiveJSON.getString(JSON.KEY_USERNAME);
        String itemLocation = receiveJSON.getString(JSON.KEY_LOCATION);
        int itemRssi = receiveJSON.getInt(JSON.KEY_BEACON_RSSI);
        itemUuid = receiveJSON.getString(JSON.KEY_ITEM_UUID);
        logger.debug("foundClient: " + foundClient +", location: " + itemLocation + ", rssi: " + itemRssi);
        //if(collectionNum < 5)
//        while(collectionNum < 5) {
//            addCollectionNumber();
//            logger.info("SendBeaconToServer: " + collectionNum + " " + client.getUserAccount() +  " Ready");
        if(getTempRssi() == 0) {
            setTempRssi(itemRssi);
            setTempLocation(itemLocation);
        }

        if(getTempLocation().equals(itemLocation)) {
            setTempRssi(itemRssi);
        }

        if(getTempRssi() < itemRssi) {
            setTempRssi(itemRssi);
            setTempLocation(itemLocation);
        }

//            continue;
//        }

        if(!getTempLocation().equals(getSentLocation())) {
            JSONObject sendObject = new JSONObject();
            sendObject.put(JSON.KEY_STATUS, JSON.STATUS_REPLY_ITEM_POSITION);
            sendObject.put(JSON.KEY_LOCATION, tempLocation);
            sendObject.put(JSON.KEY_ITEM_MINOR, itemMinor);
            sendObject.put(JSON.KEY_ITEM_MAJOR, itemMajor);
            sendObject.put(JSON.KEY_ITEM_UUID, itemUuid);
            sendObject.put(JSON.KEY_ITEM_NAME, guidingAllDB.getItemNameByUuid(itemUuid));
            User user = getClientByAccount(clientWhoLost);
            //        logger.info("SendBeaconToServer: " + user.getUserAccount() + " Send");
            logger.info("SendBeaconToServer: " + sendObject.toString());
            user.send(sendObject.toString());
            setCollectionNumber(0);
            setSentLocation(tempLocation);
        } else {
            logger.info("same location: " + getSentLocation() + ", " + getTempLocation());
        }
    }
}