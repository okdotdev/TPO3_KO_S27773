package zad1.Proxy;

import javafx.util.Pair;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyServer implements Runnable {
    private final int serverPort;
    private ServerSocket server;
    private final ConcurrentHashMap<String, Pair<String, Integer>> langServers = new ConcurrentHashMap<>();


    public ProxyServer(int port) {
        this.serverPort = port;
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
                System.out.println("New client connected" + client.getInetAddress().getHostAddress());
                ConnectionHandler clientSock = new ConnectionHandler(this, client);
                new Thread(clientSock).start();

            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }


    public void addLanguageServer(String msg, Socket client) {
        String[] data = msg.split("@@@");
        String ip = client.getInetAddress().getHostAddress();
        int port = Integer.parseInt(data[1]);
        String lang = data[2];
        langServers.put(lang, new Pair<>(ip, port));
        System.out.println("Added " + lang + " from: " + ip + ":" + port + " to languageServers");
    }


    public void sendLanguagesList(PrintWriter out, Socket client) {
        refreshLangServers();
        StringBuilder languages = new StringBuilder();
        if (!langServers.isEmpty()) {
            for (String key : langServers.keySet())
                languages.append("@@@").append(key);
        }
        String response = "LANGUAGES" + languages;
        out.println(response);
        System.out.println("Sent languages list to: " + client.getInetAddress().getHostAddress() + "@@@" + client.getPort() + ": " + response);
    }

    public void translateMessage(String msg, PrintWriter out, Socket client) throws IOException {
        String[] data = msg.split("@@@");
        String lang = data[1];
        String clientIP = client.getInetAddress().getHostAddress();
        String clientPort = data[2];
        String word = data[3];

        if (langServers.containsKey(lang)) {
            Pair<String, Integer> langServer = langServers.get(lang);
            String ip = langServer.getKey();
            int port = langServer.getValue();
            sendTranslationRequest(ip, port, clientIP, clientPort, word);
        } else {
            out.println("TRANSLATE@@@ERROR_SERVER");
            System.out.println("No " + lang + " server available.");
        }
    }

    private void sendTranslationRequest(String ip, int port, String clientIP, String clientPort, String word) throws IOException {
        try (Socket socket = new Socket(ip, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("TRANSLATE@@@" + clientIP + "@@@" + clientPort + "@@@" + word);
            System.out.println("Send to: " + ip + ":" + port + ", Message: TRANSLATE@@@" + clientIP + "@@@" + clientPort + "@@@" + word);
        }
    }

    private void refreshLangServers() {
        for (Map.Entry<String, Pair<String, Integer>> entry : langServers.entrySet()) {
            String language = entry.getKey();
            Pair<String, Integer> serverInfo = entry.getValue();
            String ip = serverInfo.getKey();
            int port = serverInfo.getValue();

            boolean serverReachable = isServerReachable(ip, port);

            if (!serverReachable) {
                System.out.println("Removing unreachable language server for language: " + language);
                langServers.remove(language);
            }
        }
    }

    private boolean isServerReachable(String ip, int port) {
        try (Socket ignored = new Socket(ip, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
