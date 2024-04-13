package zad1.Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientController implements ActionListener {

   private ClientView gui;

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    private void translateButtonAction() {
        String lang = gui.getLanguageListSelection();
        String word = gui.getToTranslation();

        if (lang != null && !word.isEmpty()){
            try {
                int port = server.createNewSocket();

                String requestMessage = "TRANSLATE@@@"+lang+"@@@"+port +"@@@" + word;
                server.sendRequest(server.middleServerIP, server.middleServerPort, requestMessage,false);
                String response = server.waitForResponse();

                String[] arr = response.split("@@@");

                // TRANSLATE@@@WORD_TO_TRANSLATE@@@TRANSLATED_WORD
                if (arr.length == 3){
                    if (arr[2].equals("null")) {
                        gui.setResultOfTranslation("Word is not in dictionary.", true);
                    }
                    else
                        gui.setResultOfTranslation(arr[2], false);
                }
                else
                    gui.setResultOfTranslation("Error with getting translation", true);

            } catch (IOException e) {
                server.middleServerIP = null;
                server.middleServerPort = 0;

                gui.setResultOfTranslation("Error with getting translation", true);
                System.err.println("Couldn't get translation" + e.getMessage());
            }
        }
    }
}
