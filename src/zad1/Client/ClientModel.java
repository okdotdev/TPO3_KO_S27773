package zad1.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientModel {
    private PrintWriter out;
    private BufferedReader in;
    private final int port;

    Socket socket;

    public ClientModel(int serverPort) {
        this.port = serverPort;
        try {
            socket = new Socket("127.0.0.1", serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //DEBUG:
            sendTranslationRequest("dog", "EN");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTranslationRequest(String word, String language) {
        String requestMessage = "TRANSLATE@@@" + language.toUpperCase() + "@@@" + port + "@@@" + word;
        out.println(requestMessage);
    }


    public String getTranslationResponse() {

        try {
            String response = in.readLine();
            String[] arr = response.split("@@@");
            if (arr.length == 3) {
                if (arr[2].equals("null")) {
                    System.out.println("Word is not in dictionary.");
                } else
                    System.out.println(arr[2]);
            } else
                System.out.println("Error with getting translation");
        } catch (IOException e) {
            System.err.println("Couldn't get translation" + e.getMessage());

        }
        return null;
    }


}



