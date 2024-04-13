package zad1.Client;

import java.io.IOException;

public class ClientController {

    private final ClientView gui;
    private final ClientConnection clientConnection;

    ClientController(ClientView gui) {
        this.gui = gui;
        this.clientConnection = new ClientConnection();
    }

    public void translateButtonAction() {
        String lang = gui.getLanguageListSelection();
        String word = gui.getToTranslation();

        if (lang != null && !word.isEmpty()) {
            try {
                int port = clientConnection.createNewSocket();

                String requestMessage = "TRANSLATE@@@" + lang + "@@@" + port + "@@@" + word;
                clientConnection.sendRequest(clientConnection.middleServerIP, clientConnection.middleServerPort, requestMessage, false);
                String response = clientConnection.waitForResponse();

                String[] arr = response.split("@@@");

                if (arr.length == 3) {
                    if (arr[2].equals("null")) {
                        gui.setResultOfTranslation("Word is not in dictionary.", true);
                    } else {
                        gui.setResultOfTranslation(arr[2], false);
                    }
                } else {
                    gui.setResultOfTranslation("Error with getting translation", true);
                }

            } catch (IOException e) {
                clientConnection.middleServerIP = null;
                clientConnection.middleServerPort = 0;

                gui.setResultOfTranslation("Error with getting translation", true);
                System.err.println("Couldn't get translation" + e.getMessage());
            }
        }
    }


}
