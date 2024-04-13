package zad1.Proxy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final Socket socket;
    private final ProxyServer proxyServer;

    public ConnectionHandler(ProxyServer proxyServer, Socket socket) {
        this.proxyServer = proxyServer;
        this.socket = socket;
    }

    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            boolean running = true;

            while (running) {
                String message = in.readLine();

                if (message != null) {
                    System.out.println("Received message: " + message);
                    String[] tokens = message.split("@@@");
                    String command = tokens[0];

                    switch (command) {
                        case "PING":
                            out.println("PONG");
                            break;
                        case "ADD":
                            proxyServer.addLanguageServer(message, this);
                            break;
                        case "TRANSLATE":
                            proxyServer.sendTranslateMessageRequest(message, this);
                            break;
                        default:
                            running = false;
                            break;
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getIP() {
        return socket.getInetAddress().getHostAddress();
    }

    public int getPort() {
        return socket.getPort();
    }


}