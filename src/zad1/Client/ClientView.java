package zad1.Client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ClientView extends Application {

    private ComboBox<String> languageList;

    private TextField toTranslation;
    private TextField resultOfTranslation;

    private Button translateButton;
    private ClientController controller;


    @Override
    public void start(Stage primaryStage) {
        this.controller = new ClientController(this);
        createGUI(primaryStage);
        changeStateOfElements(false);
    }

    private void createGUI(Stage primaryStage) {
        BorderPane mainPane = new BorderPane();

        FlowPane settingsPane = new FlowPane();
        settingsPane.setAlignment(Pos.CENTER);
        settingsPane.setPadding(new Insets(10));
        settingsPane.setHgap(10);
        {
            languageList = new ComboBox<>();



            settingsPane.getChildren().addAll(languageList);
        }

        GridPane translatePane = new GridPane();
        translatePane.setAlignment(Pos.CENTER);
        translatePane.setPadding(new Insets(10));
        translatePane.setVgap(10);
        {
            toTranslation = new TextField();
            resultOfTranslation = new TextField();
            resultOfTranslation.setEditable(false);

            translatePane.add(toTranslation, 0, 0);
            translatePane.add(resultOfTranslation, 0, 1);
        }

        translateButton = new Button("Translate!");
        translateButton.setOnAction(e -> controller.translateButtonAction());

        mainPane.setTop(settingsPane);
        mainPane.setCenter(translatePane);
        mainPane.setBottom(translateButton);

        int SIZE_WIDTH = 400;
        int SIZE_HEIGHT = 150;
        Scene scene = new Scene(mainPane, SIZE_WIDTH, SIZE_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Translator - Not Connected");
        primaryStage.show();
    }

    void changeStateOfElements(boolean b) {
        toTranslation.setEditable(b);
        translateButton.setDisable(!b);
    }



    public String getToTranslation() {
        return toTranslation.getText();
    }

    public void setResultOfTranslation(String text, boolean isError) {
        if (isError) {
            resultOfTranslation.setStyle("-fx-text-fill: red;");
        } else {
            resultOfTranslation.setStyle("-fx-text-fill: black;");
        }
        resultOfTranslation.setText(text);
    }

    public String getLanguageListSelection() {
        return languageList.getValue();
    }
}
