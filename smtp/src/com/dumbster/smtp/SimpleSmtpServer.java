/*
 * Dumbster: a dummy SMTP server.
 * Copyright (C) 2003, Jason Paul Kitchen
 * lilnottsman@yahoo.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.dumbster.smtp;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Dummy SMTP server for testing purposes.
 */
public class SimpleSmtpServer implements Runnable {
  /** Stores all of the email received since this instance started up. */
  private List receivedMail;
  
  /** Stores references to any listeners */
  private List listeners;

  /** Default SMTP port is 25. */
  public static final int DEFAULT_SMTP_PORT = 25;

  /** Indicates whether this server is stopped or not. */
  private volatile boolean stopped = true;

  /** Indicates if a stop request has been sent to this server. */
  private volatile boolean doStop = false;

  /** Port the server listens on - set to the default SMTP port initially. */
  private int port = DEFAULT_SMTP_PORT;

  /**
   * Constructor.
   */
  public SimpleSmtpServer(int port) {
    receivedMail = new ArrayList();
    listeners = new ArrayList();
    this.port = port;
  }

  /**
   * Main loop of the SMTP server.
   */
  public void run() {
    stopped = false;
    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(port);
      serverSocket.setSoTimeout(500); // Block for maximum of 1.5 seconds

      // Server: loop until stopped
      while(!doStop) {
        Socket socket = null;
        try {
          socket = serverSocket.accept();
        } catch(InterruptedIOException iioe) {
          if (socket != null) { socket.close(); }
          continue; // Non-blocking socket timeout occurred: try accept() again
        }

        // Get the input and output streams
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());

        // Initialize the state machine
        SmtpState smtpState = SmtpState.CONNECT;
        SmtpRequest smtpRequest = new SmtpRequest(SmtpActionType.CONNECT, "", smtpState);

        // Execute the connection request
        SmtpResponse smtpResponse = smtpRequest.execute();

        // Send initial response
        sendResponse(out, smtpResponse);
        smtpState = smtpResponse.getNextState();

        SmtpMessage msg = new SmtpMessage();

        while(smtpState != SmtpState.CONNECT) {
          String line = input.readLine();

          if (line == null) {
            break;
          }
          // Create request from client input and current state
          SmtpRequest request = SmtpRequest.createRequest(line, smtpState);
          // Execute request and create response object
          SmtpResponse response = request.execute();
          // Move to next internal state
          smtpState = response.getNextState();
          // Send reponse to client
          sendResponse(out, response);

          // Store input in message
          msg.store(response, request.getParams());
        }

        addMessage(msg);
        socket.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (serverSocket != null) {
        try {
          serverSocket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    stopped = true;
  }

  /**
   * Send response to client.
   * @param out socket output stream
   * @param smtpResponse response object
   */
  private void sendResponse(PrintWriter out, SmtpResponse smtpResponse) {
    if (smtpResponse.getCode() > 0) {
      out.print(smtpResponse.getCode()+" "+smtpResponse.getMessage()+"\r\n");
      out.flush();
    }
  }
  
  /**
   * Adds a message to the list and informs any listeners
   * @param message SMTP message to be added
   */
  private void addMessage(SmtpMessage message) {
	receivedMail.add(message);
	for (int num = 0; num < listeners.size(); num++) {
	  SimpleSmtpServerListener listener = (SimpleSmtpServerListener) listeners.get(num);
	  listener.newMessage(message);
	}
  }

  /**
   * Get email received by this instance since start up.
   * @return List of String
   */
  public Iterator getReceivedEmail() {
    return receivedMail.iterator();
  }

  /**
   * Get the number of messages received.
   * @return size of received email list
   */
  public int getReceievedEmailSize() {
    return receivedMail.size();
  }

  /**
   * Forces the server to stop after processing the current request.
   */
  public void stop() {
    doStop = true;
    while(!isStopped()) {} // Wait for email server to stop
  }

  /**
   * Indicates whether this server is stopped or not.
   * @return true iff this server is stopped
   */
  public boolean isStopped() {
    return stopped;
  }
  
  /**
   * Adds a listener to this instance.
   * @param listener Listener to add
   */
  public void addListener(SimpleSmtpServerListener listener) {
  	listeners.add(listener);
  }
  
  /**
   * Removes a listener from this instance.
   * @param listener Listener to remove
   */
  public void removeListener(SimpleSmtpServerListener listener) {
  	listeners.remove(listener);
  }

  /**
   * Creates an instance of SimpleSmtpServer and starts it. Will listen on the default port.
   * @return a reference to the SMTP server
   */
  public static SimpleSmtpServer start() {
    return start(DEFAULT_SMTP_PORT);
  }

  /**
   * Creates an instance of SimpleSmtpServer and starts it.
   * @param port port number the server should listen to
   * @return a reference to the SMTP server
   */
  public static SimpleSmtpServer start(int port) {
    SimpleSmtpServer server = new SimpleSmtpServer(port);
    Thread t = new Thread(server);
    t.start();
    return server;
  }

}
