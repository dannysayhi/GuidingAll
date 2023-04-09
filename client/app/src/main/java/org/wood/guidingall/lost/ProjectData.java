package org.wood.guidingall.lost;

import org.json.JSONException;
import org.json.JSONObject;
import org.wood.guidingall.tools.Config;
import org.wood.guidingall.tools.JSON;

/**
 * Created by Danny on 2015/10/25.
 */
public class ProjectData {

    private String name = "";
    private String uuid = "";
    private int major;
    private int minor;
    private String lostAddr;
    private String lostDate;
    private String info = "";
    private String phone = "";
    private int reward = 0;

    private static final String NAME = "NAME";
    private static final String UUID = "UUID";
    private static final String MAJOR = "MAJOR";
    private static final String MINOR = "MINOR";
    private static final String ADDR = "ADDR";
    private static final String DATE = "DATE";
    private static final String INFO = "INFO";
    private static final String PHONE = "PHONE";
    private static final String REWARD = "REWARD";

    public ProjectData(String itemName) {
        switch (itemName) {
            case "小孩":
                name = Config.BT5_ITEM;
                uuid = Config.BT5_UUID;
                major = Config.BT5_MAJOR;
                minor = Config.BT5_MINOR;
                lostAddr = "台中新光三越";
                lostDate = "2015-10-31";
                info = "在新光三越一樓走失";
                phone = "0912368324";
                reward = 10000;
                break;

            case "錢包":
                name = Config.BT6_ITEM;
                uuid = Config.BT6_UUID;
                major = Config.BT6_MAJOR;
                minor = Config.BT6_MINOR;
                lostAddr = "台中新光三越";
                lostDate = "2015-10-31";
                info = "在新光三越一樓遺失";
                phone = "0912368324";
                reward = 500;
                break;
        }
    }

    public ProjectData() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setLostAddr(String lostAddr) {
        this.lostAddr = lostAddr;
    }

    public String getLostAddr() {
        return lostAddr;
    }

    public void setLostDate(String lostDate) {
        this.lostDate = lostDate;
    }

    public String getLostDate() {
        return lostDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSON.KEY_STATUS, JSON.STATUS_POST_LOST_ITEM);
            jsonObject.put(ProjectData.NAME, name);
            jsonObject.put(ProjectData.UUID, uuid);
            jsonObject.put(ProjectData.MAJOR, major);
            jsonObject.put(ProjectData.MINOR, minor);
            jsonObject.put(ProjectData.INFO, info);
            jsonObject.put(ProjectData.ADDR, lostAddr);
            jsonObject.put(ProjectData.DATE, lostDate);
            jsonObject.put(ProjectData.PHONE, phone);
            jsonObject.put(ProjectData.REWARD, reward);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
