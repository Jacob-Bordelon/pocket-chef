package com.example.pocket_chef_application;

import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class UDPClient implements Runnable { // Thread implementation
    TextView parentView; // -> for displaying status on parents view
    int udp_port;
    String server_ip; 

    public UDPClient(TextView textView, int port, String IP) {
        parentView = textView; // -> sets the statusbar view
        udp_port = port;
        server_ip = IP;
    }

    @Override
    public void run() {
        // varibables to keep track of
        InetAddress serverAddr; // -> server address
        DatagramSocket udpSocket = null; // -> for socket creation
        DatagramPacket packet; // -> for packet creation
        try {
            // initializes the socket for connection
            udpSocket = new DatagramSocket(udp_port);
            serverAddr = InetAddress.getByName(server_ip);
            // creates the packet for sending
            byte[] buf = ("alive?").getBytes(); // -> message to send to server
            packet = new DatagramPacket(buf, buf.length, serverAddr, udp_port);
            udpSocket.send(packet); // -> sends packet

            // receiving the packet from server
            byte[] message = new byte[8000]; // -> buffer rcvd
            packet = new DatagramPacket(message, message.length); // -> creates packet with buffer size
            Log.i("UDP client: ", "about to wait to receive"); // -> log
            // ToDo: check if thread still running
            udpSocket.setSoTimeout(500); // -> times out at .5 seconds. Error catch if timeout
            udpSocket.receive(packet); // -> start listenning for response from server
            
            // parsing recieved payload
            String text = new String(message, 0, packet.getLength()); // -> string with packet size  
            Log.d("Received text", text); // -> log
            parentView.setTextColor(Color.parseColor("#1D800E")); // set positive text color

            parentView.setText("Connected"); // -> signal the user

            Log.d("Test","Testing connection");
            // close the connection
            // ToDo: loop forever to constantly check the connection (maybe)
            udpSocket.close();

        } catch (SocketTimeoutException e) {
            // handling a timeout
            Log.e("Timeout Exception", "UDP Connection:", e); // -> log
            parentView.setTextColor(Color.parseColor("#FF0000")); // -> set negative text color
            parentView.setText("Check connection"); // -> signal user to check connection
            udpSocket.close(); // -> close socket
        } catch (IOException e) {
            Log.e(" UDP client IOException", "error: ", e);
            udpSocket.close();
        }
    }
}
