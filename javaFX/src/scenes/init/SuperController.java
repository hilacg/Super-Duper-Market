package scenes.init;

import course.java.sdm.engine.Engine;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javafx.event.ActionEvent;

import java.io.File;
import java.io.FileNotFoundException;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SuperController {

    private Engine engine;
    private Stage primaryStage;
    private boolean isErrorMessageShown = false;
    private SimpleBooleanProperty finishedInit;

    public void setEngine(Engine engine) {
        this.engine = engine;
    }
    public void setPrimaryStage(Stage primaryStage) { this.primaryStage = primaryStage;}

    @FXML
    private Button loadBtn;

    @FXML
    private Button customersBtn;

    @FXML
    private Button storesBtn;

    @FXML
    private Button productsBtn;

    @FXML
    private Button orderBtn;

    @FXML
    private Button historyBtn;

    @FXML
    private Label errorMessage;
    @FXML
    protected void onContinue(javafx.event.ActionEvent event) {
        finishedInit.set(true);
    }

    public SimpleBooleanProperty finishedInit() {
        return finishedInit;
    }

    @FXML
    void loadXML(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open XML");
            File chosenFile = fileChooser.showOpenDialog(primaryStage);

            engine.loadXML(chosenFile.getPath());
      /*      VBox vbox = new VBox(30, loadBtn);
            Scene scene = new Scene(vbox, 800, 500);
            primaryStage.setScene(scene);
            primaryStage.show();*/

        }catch (Exception e){
            errorMessage.setText(e.getMessage());
        }



    }

}
