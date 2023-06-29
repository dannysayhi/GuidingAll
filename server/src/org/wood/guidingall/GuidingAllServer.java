package org.wood.guidingall;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONException;
import org.json.JSONObject;
import org.wood.guidingall.db.GuidingAllDB;
import org.wood.guidingall.handler.ClientHandler;
import org.wood.guidingall.tools.Config;
import org.wood.guidingall.tools.JSON;
import org.wood.guidingall.tools.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * Created by Danny on 2015/4/5.
 */
public class GuidingAllServer {
    private static Logger logger = Logger.getLogger(GuidingAllServer.class);
    private static ServerSocket serverSocket;
    private static User user;


    public static ArrayList<User> userList = new ArrayList<User>();

    public static void main(String args[]) {
        // Load configuration
        PropertyConfigurator.configure("config//log4j.properties");
        Logger.getRootLogger().setLevel(Level.DEBUG);
        GuidingAllDB guidingAllDB = new GuidingAllDB();

        logger.info("Log in server begin!");
        // Create socket connection
        try {
            serverSocket = new ServerSocket(Config.SOCKET_PORT);
        } catch (IOException e) {
            logger.error("Unable to set up port!");
            e.printStackTrace();
        }

        do {
            // Listen new client connection
            try {
                user = new User(serverSocket.accept());
                user.setAccount(JSON.KEY_UNKNOWN);
                user.setLocation(JSON.KEY_UNKNOWN);
            } catch (IOException e) {
                logger.error("Socket accept error!");
                e.printStackTrace();
            }

            logger.info("A new client accepted: " + user.getIpAddress());
            //Auth for incoming clients
            try {
                JSONObject receiveObject;
                String receiveMessage = user.receive();
                receiveObject = new JSONObject(receiveMessage);
                logger.debug("A client say: " + receiveMessage);

                String userName = receiveObject.getString(JSON.KEY_USERNAME);
                String userPasswd = receiveObject.getString(JSON.KEY_USERPASSWD);

                String dbUserPassword = guidingAllDB.getPasswordByUserName(userName);
                
                // Compare username and password
                if(userPasswd.compareTo(dbUserPassword) == 0) {
                    user.setAccount(userName);
                    logger.info("A new client login successfull: " + user.getAccount());

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(JSON.KEY_RESULT, true);
                    jsonObject.put(JSON.KEY_RESULT_MESSAGE, JSON.KEY_LOGIN_SUCCESS);
                    jsonObject.put(JSON.KEY_USERNAME, userName);
                    user.send(jsonObject.toString());
                    logger.debug("server send: " + jsonObject.toString());

                    if(user.isConnected()) {
                        logger.info("A new client connected: " + user.getAccount());

                        new Thread(new ClientHandler(userList, user)).start();
                    }

                } else {
                    // Error handling
                    logger.info("A new client login failed: " + user.getIpAddress());

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(JSON.KEY_RESULT, false);
                    jsonObject.put(JSON.KEY_RESULT_MESSAGE, JSON.KEY_LOGIN_FAIL);
                    user.send(jsonObject.toString());
                    logger.info("server send: " + jsonObject.toString());

                    user.close();
                }
            } catch (JSONException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } while(!serverSocket.isClosed());

        user.close();
        logger.info("A client closed: " + user.getIpAddress());
    }
}