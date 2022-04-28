package org.firstinspires.ftc.teamcode.managers.telemetry.server;

import org.firstinspires.ftc.teamcode.auxilary.UpdatableWeakReference;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.WeakHashMap;

public class Server {
    public int port;
    private boolean loaded;
    private ServerThread thread;

    public boolean serverIsRunning = true;

    public Server(TelemetryManager dataSource) {
        //add a shutdown hook so the server can shut down itself when the opmode's over
        (new FratricideCommitterThread()).start();

        thread = new ServerThread(this, dataSource);
        thread.start();
    }




    public boolean blockUntilLoaded() {
        while(!loaded) {}
        return true;
    }

    private class ServerThread extends Thread {
        int port;
        ServerSocket serverSocket;
        TelemetryManager dataSource;

        public boolean running = true;

        private WeakHashMap<String, StreamHandler> streamRegistry = new WeakHashMap<>();

        public ServerThread(Server parentProcess, TelemetryManager dataSource) {
            this.dataSource = dataSource;
            this.port = 5564;
            while(port < 5664 && serverSocket == null) {
                try {
                    serverSocket = new ServerSocket(port);
                } catch (IOException err) {
                    port++;
                }
            }
            if(serverSocket == null) {
                FeatureManager.logger.log("Could not reserve TCP port");
            } else {
                parentProcess.port = this.port;
                FeatureManager.logger.log("Go to http://192.168.43.1:" + port);
            }

            parentProcess.loaded = true;
            
        }
        @Override
        public void run() {
            try {
                try {
                    while (serverIsRunning) {
                        Socket socket = serverSocket.accept();

                        RequestHandlerThread rht = new RequestHandlerThread(socket, dataSource, streamRegistry);
                        new Thread(rht).start();
                    }
                    serverSocket.close();
                } catch(Exception e) {
                    FeatureManager.logger.log("dashboard status" + e.toString());
                }
            } catch(Throwable t) {
                FeatureManager.logger.log("Silent error in 'Server'");
            }
        }
    }
    private class FratricideCommitterThread extends Thread {
        @Override
        public void run() {
            while(FeatureManager.isOpModeRunning) yield();
            serverIsRunning = false;
        }
    }
}
