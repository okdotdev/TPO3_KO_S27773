package zad1.Client;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        ClientView clientView = new ClientView();
        ClientModel clientModel = new ClientModel(8080);
        ClientController clientController = new ClientController(clientView, clientModel);


    }
}
