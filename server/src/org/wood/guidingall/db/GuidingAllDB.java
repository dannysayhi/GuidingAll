package org.wood.guidingall.db;

import org.wood.guidingall.LostData;
import org.wood.guidingall.ProjectData;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GuidingAllDB {

    Connection conn = null;

    public GuidingAllDB(){

        conn = DBConnect.getConn();
    }

    public String getPasswordByUserName(String userName) {
        String password = "";
        String sql = "SELECT Password FROM user WHERE Name = ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
                password = resultSet.getString("Password");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return password;
    }

    public ArrayList getFriendListByUserName(String userName) {
        ArrayList<String> friendList = new ArrayList<String>();
        String sql ="SELECT userFriend FROM friendship WHERE user = ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
                friendList.add(resultSet.getString("userFriend"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friendList;
    }

    public boolean isFriend(String userName, String friendName) {
        boolean isfriend = false;
        String sql ="SELECT userFriend FROM friendship WHERE user = ? AND userFriend = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, friendName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
                isfriend = true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isfriend;
    }

    public ArrayList<String> getItemByUserName(String userName) {

        ArrayList<String> itemList = new ArrayList<String>();
        String sql = "SELECT itemName FROM userItem WHERE userName = ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
                itemList.add(resultSet.getString("itemName"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return itemList;
    }

    public String getItemNameByUuid(String itemUuid) {
        String itemName = "";
        String sql = "SELECT itemName FROM userItem WHERE itemUuid = ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, itemUuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
                itemName = resultSet.getString("itemName");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return itemName;
    }

    public void insertProjectData(ProjectData projectData) {
        String sql = "INSERT INTO project " +
                "(`Owner`, `Name`, `LostAddr`, `LostDate`, `Describe`, `Phone`, `Reward`, `UploadDate`) VALUES" +
                "(" + "'" + projectData.getOwner() + "'" + "," +
                "'" + projectData.getName() + "'" + "," +
                "'" + projectData.getLostAddr() + "'" + "," +
                "'" + getCurrentTimeStamp() + "'" + "," +
                "'" + projectData.getInfo() + "'" + "," +
                "'" + projectData.getPhone() + "'" + "," +
                projectData.getReward() + "," +
                "'" + getCurrentTimeStamp() + "'" + ")";
        System.out.println(sql);
        try {
            Statement statement = conn.createStatement();
            System.out.println("update: " + statement.executeUpdate(sql));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertLostData(LostData lostData) {
        String sql = "INSERT INTO lostdata " +
                "(`Owner`, `ItemName`, `Location`, `PNGsrc`, `GeoPoint`, `FoundTime`) VALUES" +
                "(" + "'" + lostData.getOwner() + "'" + "," +
                "'" + lostData.getItemName() + "'" + "," +
                "'" + lostData.getLocation() + "'" + "," +
                "'" + lostData.getPNGsrc() + "'" + "," +
                "'" + lostData.getGeoPoint() + "'" + "," +
                "'" + getCurrentTimeStamp() + "'" + ")";
        System.out.println(sql);
        try {
            Statement statement = conn.createStatement();
            System.out.println("update: " + statement.executeUpdate(sql));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String getCurrentTimeStamp() {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date today = new java.util.Date();
        return dateFormat.format(today.getTime());
    }
}
