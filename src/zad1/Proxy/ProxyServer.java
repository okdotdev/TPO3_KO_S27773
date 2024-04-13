package zad1.Proxy;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProxyServer implements Runnable {
    private final int serverPort;
    private ServerSocket server;
    private final List<ConnectionHandler> connections = new ArrayList<>();
    private final Map<String, ConnectionHandler> langServers;

    public ProxyServer(int port) {
        this.serverPort = port;
        this.langServers = new HashMap<>();
        try {
            server = new ServerSocket(serverPort);
            server.setReuseAddress(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Created Server Object.");
    }

    @Override
    public void run() {
        System.out.println("Running proxy server on port: " + serverPort);
        while (true) {
            try {

                Socket client = server.accept();
                System.out.println("New client connected " + client.getInetAddress().getHostAddress());
                ConnectionHandler clientSock = new ConnectionHandler(this, client);
                new Thread(clientSock).start();
                connections.add(clientSock);

            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }


    public void addLanguageServer(String msg, ConnectionHandler languageServer) {
        String[] data = msg.split("@@@");
        String lang = data[2];
        langServers.put(lang, languageServer);
        System.out.println("Added " + lang + " to Language Servers");
    }


    public void sendTranslateMessageRequest(String msg, ConnectionHandler client){
        String[] data = msg.split("@@@");
        String lang = data[1];
        String clientIP = client.getIP();
        String clientPort = data[2];
        String word = data[3];

        if (langServers.containsKey(lang)) {
            ConnectionHandler langServer = langServers.get(lang);
            String ip = langServer.getIP();
            int port = langServer.getPort();
            langServer.sendMessage("TRANSLATE@@@" + clientIP + "@@@" + clientPort + "@@@" + word);
            System.out.println("Send to: " + ip + ":" + port + ", Message: TRANSLATE@@@" + clientIP + "@@@" + clientPort + "@@@" + word);
        } else {
            System.out.println("Language Server not found for: " + lang);
        }
    }

}
