package app;

import components.load.LoadController;
import course.java.sdm.engine.Engine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import components.main.SuperController;

import java.io.IOException;
import java.net.URL;

public class App extends Application {

        private static final String FXML_PATH = "/components/main/MainScene.fxml";
        private Engine engine;

        @Override
        public void start(Stage primaryStage) throws IOException {

            FXMLLoader fxmlLoader = getFXML();
            Parent superRoot = getSuperRoot(fxmlLoader);
            SuperController superController = getSuperController(fxmlLoader, primaryStage);


            Scene scene = new Scene(superRoot);
            primaryStage.setTitle("Super Duper");
            primaryStage.setScene(scene);
            primaryStage.show();



        }

    private FXMLLoader getFXML() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(FXML_PATH);
        fxmlLoader.setLocation(url);
        return fxmlLoader;
    }

    private SuperController getSuperController(FXMLLoader fxmlLoader, final Stage primaryStage) {
        SuperController superController = fxmlLoader.getController();
        this.engine = new Engine(superController);
        superController.setEngine(this.engine);
        superController.setPrimaryStage(primaryStage);
   /*     superController.finishedInit().addListener((source, oldValue, newValue) -> {
            if (newValue) {
                final SuperScene superScene = new SuperScene(engine);
                primaryStage.setScene(superScene);
            }
        });*/
        return superController;
    }

    private Parent getSuperRoot(FXMLLoader fxmlLoader) throws IOException {
        return (Parent) fxmlLoader.load(fxmlLoader.getLocation().openStream());
    }

    public static void main(String[] args) {
        launch(args);
    }
}