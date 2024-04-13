package zad1.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientModel implements Runnable {
    PrintWriter out;
    BufferedReader in;


    public ClientModel() {
        try (Socket socket = new Socket("localhost", 1500)) {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {

        Scanner sc = new Scanner(System.in);
        String line = null;
        while (!"exit".equalsIgnoreCase(line)) {

            line = sc.nextLine();

            out.println(line);
            out.flush();


        }

        // closing the scanner object
        sc.close();
    }

    public void receiveMessage() {
        try {
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
        out.flush();
    }
}



