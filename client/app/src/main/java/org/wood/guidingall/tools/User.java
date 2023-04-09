package org.wood.guidingall.tools;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;

public class User implements Serializable {
    private static User user;
	private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String userName;
    private String userLocation;
    private boolean isClose = false;

    public static synchronized User getInstance(){
        if(user == null) {
            user = new User();
            return user;
        }
        return user;
    }


    public void connect() throws IOException {
        socket = new Socket(Config.IPADDRESS, Config.SOCKET_PORT);
        isClose = false;
        input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
    }


    public void setUserName(String name) {
        userName = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserLocation(String location) {
        userLocation = location;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void send(String message) {
        output.println(message);
        Log.d("User send", message);
    }

    public String receive() throws IOException {
        if(!isClose) {
            String reply = input.readLine();
            Log.d("User receive", reply);
            return reply;
        }
        else
            return "";
    }

    public boolean isConnected() {return socket.isConnected();}

    public boolean isClose() {return isClose;}

    public void close() {
      try {
          isClose = true;
          input.close();
          output.close();
          socket.close();
      }catch(IOException e) {
          e.printStackTrace();
      }
    }

}
