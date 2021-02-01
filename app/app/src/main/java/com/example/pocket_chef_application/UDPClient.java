package com.example.pocket_chef_application;

import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class UDPClient implements Runnable {

    TextView parentView;

    public UDPClient(TextView textView) {
        parentView = textView;
    }

    @Override
    public void run() {
        int udp_port = 3000;
        InetAddress serverAddr;
        DatagramSocket udpSocket = null;
        DatagramPacket packet;
        try {
            udpSocket = new DatagramSocket(udp_port);
            serverAddr = InetAddress.getByName("192.168.0.6");
            byte[] buf = ("hi").getBytes();
            packet = new DatagramPacket(buf, buf.length, serverAddr, udp_port);
            udpSocket.send(packet);

            byte[] message = new byte[8000];
            packet = new DatagramPacket(message, message.length);
            Log.i("UDP client: ", "about to wait to receive");
            udpSocket.setSoTimeout(1000);
            udpSocket.receive(packet);
            String text = new String(message, 0, packet.getLength());
            Log.d("Received text", text);
            parentView.setTextColor(Color.parseColor("#1D800E"));
            parentView.setText("Connected");

            udpSocket.close();
        } catch (SocketTimeoutException e) {
            Log.e("Timeout Exception", "UDP Connection:", e);
            parentView.setTextColor(Color.parseColor("#FF0000"));
            parentView.setText("Check connection");
            udpSocket.close();
        } catch (IOException e) {
            Log.e(" UDP client IOException", "error: ", e);
            udpSocket.close();
        }
    }
}
