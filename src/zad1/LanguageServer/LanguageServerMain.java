package zad1.LanguageServer;

import java.util.Scanner;

public class LanguageServerMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);



        String proxyServerIp = "127.0.0.1";
        int proxyServerPort = 8080;

        System.out.println("Choose language code (eg. en, de, es):");
        String languageCode = scanner.nextLine();

        new LanguageServer(proxyServerIp, proxyServerPort, languageCode);

        scanner.close();
    }
}
