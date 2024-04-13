package zad1.Proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final Socket clientSocket;
    private final ProxyServer proxyServer;

    public ConnectionHandler(ProxyServer proxyServer, Socket socket) {
        this.proxyServer = proxyServer;
        this.clientSocket = socket;
    }

    public void run() {
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);


            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {

               // writing the received message from client
                System.out.printf(" Sent from the client: %s\n", line);
                handleClientRequest(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleClientRequest(Socket client) throws Exception {
        String clientID = client.getInetAddress().getHostAddress() + "@@@" + client.getPort();
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String msg = in.readLine();
        System.out.println(clientID + ": " + msg);
        processMessage(msg, out, client);
    }

    private void processMessage(String msg, PrintWriter out, Socket client) throws Exception {
        if (msg == null) return;
        if (msg.equals("PING")) {
            out.println("PONG");
        } else if (msg.startsWith("ADD")) {
            proxyServer.addLanguageServer(msg, client);
        } else if (msg.equals("GET_LANGUAGES")) {
            proxyServer.sendLanguagesList(out, client);
        } else if (msg.startsWith("TRANSLATE")) {
            proxyServer.translateMessage(msg, out, client);
        }


    }
}