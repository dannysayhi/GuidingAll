package org.wood.guidingall.tools;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class User implements Serializable {
	private static Logger logger = Logger.getLogger(User.class);
	
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
    private String account;
    private String location;
	private boolean isClose = false;

	public User(Socket socket) throws IOException {
		this.socket = socket;
		isClose = false;
		input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
	}

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setLocation(String location) { this.location = location; }

    public String getLocation() {return location;}

	public void send(String message) {
		output.println(message);
	}

    public void send(Socket otherSocket, String message) {
        try {
            PrintWriter otherOutput = new PrintWriter(new OutputStreamWriter(otherSocket.getOutputStream(), "UTF-8"), true);
            otherOutput.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public String receive() throws IOException {
		return input.readLine();
	}
	
	public int read() throws IOException {
		return input.read();
	}

	public void close() {
		try {
			isClose = true;
			input.close();
			output.close();
			socket.close();
		} catch (IOException e) {
			logger.error("Fail to close socket!");
			e.printStackTrace();
		}
	}
	
	public boolean isConnected() {
		return socket.isConnected();
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public String getIpAddress() {
		return socket.getInetAddress().toString();
	}

}
