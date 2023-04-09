package org.wood.guidingall;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Danny on 2015/10/25.
 */
public class ProjectData {

    private String name = "";
    private String uuid = "";
    private String lostAddr;
    private String lostDate;
    private String info = "";
    private String phone = "";
    private int reward = 0;

    public static final String NAME = "NAME";
    public static final String UUID = "UUID";
    public static final String MAJOR = "MAJOR";
    public static final String MINOR = "MINOR";
    public static final String ADDR = "ADDR";
    public static final String DATE = "DATE";
    public static final String INFO = "INFO";
    public static final String PHONE = "PHONE";
    public static final String REWARD = "REWARD";
    private String owner;


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
            jsonObject.put(ProjectData.NAME, name);
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}