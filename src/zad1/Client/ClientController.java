package zad1.Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientController implements ActionListener {

    private final ClientView clientView;
    private final ClientModel clientModel;

    public ClientController(ClientView clientView, ClientModel clientModel) {
        this.clientView = clientView;
        this.clientModel = clientModel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        translateButtonAction();
    }

    private void translateButtonAction() {
        String lang = clientView.getLanguage();
        String word = clientView.getWordToTranslate();

        clientModel.sendTranslationRequest(word, lang);

        String response = clientModel.getTranslationResponse();

        String[] arr = response.split("@@@");

        if (arr.length == 3) {
            if (arr[2].equals("null")) {
                clientView.setResultOfTranslation("Word is not in dictionary.");
            } else
                clientView.setResultOfTranslation(arr[2]);
        } else
            clientView.setResultOfTranslation("Error with getting translation");


    }
}
