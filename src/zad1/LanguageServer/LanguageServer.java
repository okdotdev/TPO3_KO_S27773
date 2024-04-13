package zad1.LanguageServer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LanguageServer {
    private int serverPort;
    private Dictionary dictionary;
    private ServerSocket server;

    public LanguageServer(String proxyServerIp, int proxyServerPort, String languageCode) {
        loadDictionary(languageCode);
        createServer();
        connectToProxyServer(proxyServerIp, proxyServerPort);
    }

    private void loadDictionary(String languageCode) {
        try {
            String path = "./Dictionaries/" + languageCode + ".json";
            String json = loadStringFromFile(path);
            this.dictionary = parseTranslatorJson(json);
        } catch (IOException e) {
            handleError("Cannot load json dictionary file from: " + languageCode + ".json", 2);
        } catch (JsonSyntaxException e) {
            handleError("Incorrect json structure.", 3);
        }
    }

    private void createServer() {
        try {
            this.server = new ServerSocket(0);
            String languageServerIp = server.getInetAddress().getHostAddress();
            this.serverPort = server.getLocalPort();
            System.out.println("Starting language server for " + dictionary.getLang() + "-" + dictionary.getFullName() +
                    " on IP: " + languageServerIp + ", Port: " + serverPort);
            startServerThread();
        } catch (IOException e) {
            handleError("Cannot create server.", 4);
        }
    }

    private void startServerThread() {
        Thread serverThread = new Thread(() -> {
            while (true) {
                try {
                    Socket clientSocket = server.accept();
                    startConnectionThread(clientSocket);
                } catch (IOException e) {
                    System.err.println("Cannot accept incoming connection. IOException.");
                }
            }
        });
        serverThread.start();
    }

    private void startConnectionThread(Socket clientSocket) {
        Thread connectionThread = new Thread(() -> {
            try {
                handleClientRequest(clientSocket);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            } finally {
                closeConnection(clientSocket);
            }
        });
        connectionThread.start();
    }

    private void handleClientRequest(Socket clientSocket) throws Exception {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String clientID = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
        String msg = in.readLine();
        System.out.println(clientID + ": " + msg);

        if (msg.equals("PING")) {
            String response = "PONG@@@" + dictionary.getLang() + "-" + dictionary.getFullName();
            out.println(response);
            System.out.println(response);
        } else if (msg.startsWith("TRANSLATE")) {
            handleTranslationRequest(msg);
        }
    }

    private void handleTranslationRequest(String msg) throws Exception {
        String[] data = msg.split("@@@");
        String ip = data[1];
        int port = Integer.parseInt(data[2]);
        String word = data[3];
        String translation = dictionary.getTranslation(word);
        String response = "TRANSLATE@@@" + word + "@@@" + translation;
        sendResponse(ip, port, response);
        System.out.println("Translating word: " + word + ", Translation: " + translation);
    }

    private void sendResponse(String ip, int port, String msg) throws IOException {
        try (Socket clientSocket = new Socket(ip, port);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            out.println(msg);
            System.out.println("Send to: " + ip + ":" + port + ", Message: " + msg);
        }
    }

    private void connectToProxyServer(String proxyServerIp, int proxyServerPort) {
        try {
            send(proxyServerIp, proxyServerPort, "ADD@@@" + serverPort + "@@@" + dictionary.getLang() + "-" + dictionary.getFullName());
        } catch (IOException e) {
            handleError("Cannot connect to ProxyServer: " + proxyServerIp + ":" + proxyServerPort, 5);
        }
    }

    private void send(String ip, int port, String msg) throws IOException {
        try (Socket clientSocket = new Socket(ip, port);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            out.println(msg);
        }
    }

    private void closeConnection(Socket clientSocket) {
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Cannot close connection quietly.");
        }
    }

    private String loadStringFromFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private Dictionary parseTranslatorJson(String json) throws JsonSyntaxException {
        Gson gson = new Gson();
        return gson.fromJson(json, Dictionary.class);
    }

    private void handleError(String errorMessage, int exitCode) {
        System.err.println(errorMessage);
        System.exit(exitCode);
    }
}