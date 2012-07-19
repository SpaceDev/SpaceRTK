/*
 * This file is part of SpaceRTK (http://spacebukkit.xereo.net/).
 *
 * SpaceRTK is free software: you can redistribute it and/or modify it under the terms of the
 * Attribution-NonCommercial-ShareAlike Unported (CC BY-NC-SA) license as published by the Creative Common organization,
 * either version 3.0 of the license, or (at your option) any later version.
 *
 * SpaceRTK is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Attribution-NonCommercial-ShareAlike
 * Unported (CC BY-NC-SA) license for more details.
 *
 * You should have received a copy of the Attribution-NonCommercial-ShareAlike Unported (CC BY-NC-SA) license along with
 * this program. If not, see <http://creativecommons.org/licenses/by-nc-sa/3.0/>.
 */
package me.neatmonster.spacertk;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Pings the Module to ensure it is functioning correctly
 */
public class PingListener extends Thread {
    public static final int REQUEST_THRESHOLD = 60000; // Sixty seconds
    public static final int SLEEP_TIME = 30000; // Thirty seconds

    private boolean lostModule;

    private DatagramSocket socket;

    private AtomicBoolean running = new AtomicBoolean(false);
    
    private InetAddress localHost;

    /**
     * Creates a new PingListener
     */
    public PingListener() {
        super("Ping Listener Main Thread");
        try {
            this.localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            handleException(e, "Unable to get the Local Host!");
        }
        this.lostModule = false;
        try {
            this.socket = new DatagramSocket();
        } catch (IOException e) {
            handleException(e, "Unable to start the PingListener!");
        }
    }

    /**
     * Starts the Ping Listener
     */
    public void startup() {
        this.running.set(true);
        this.start();
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(REQUEST_THRESHOLD);
        } catch (SocketException e) {
            handleException(e, "Error setting the So Timeout!");
        }
        while (running.get()) {
            byte[] buffer = new byte[512];
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, localHost, SpaceRTK.getInstance().rPingPort);
                socket.send(packet);
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    handleException(e, "Error sleeping in the run() loop!");
                }
                socket.receive(packet);
            } catch (SocketTimeoutException e) {
                onModuleNotFound();
            } catch (IOException e) {
                handleException(e, "Error sending and receiving the RTK packet!");
            }
        }
    }

    /**
     * Shuts down the Ping Listener
     */
    public void shutdown() {
        this.running.set(false);
    }

    /**
     * Called when an exception is thrown
     * 
     * @param e
     *            Exception thrown
     */
    public void handleException(Exception e, String reason) {
        shutdown();
        System.err.println("[SpaceBukkit] Ping Listener Error!");
        System.err.println(reason);
        System.err.println("Error message:");
        e.printStackTrace();
    }

    /**
     * Called when the Module can't be found
     */
    public void onModuleNotFound() {
        if (lostModule) {
            return;
        }
        shutdown();
        System.err.println("[SpaceBukkit] Unable to ping the Module!");
        System.err
                .println("[SpaceBukkit] Please insure the correct ports are open");
        System.err
                .println("[SpaceBukkit] Please contact the forums (http://forums.xereo.net/) or IRC (#SpaceBukkit on irc.esper.net)");
        lostModule = true;
    }

}
