package app;

import course.java.sdm.engine.Engine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scenes.init.SuperController;

import java.io.IOException;
import java.net.URL;

public class App extends Application {

        private static final String FXML_PATH = "/scenes/init/MainScene.fxml";
        private final Engine engine = new Engine();

        @Override
        public void start(Stage primaryStage) throws IOException {

            FXMLLoader fxmlLoader = getFXML();
            Parent superRoot = getSuperRoot(fxmlLoader);
            SuperController playersController = getSuperController(fxmlLoader, primaryStage);

            Scene scene = new Scene(superRoot, 800, 600);

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
        SuperController superController = (SuperController) fxmlLoader.getController();
        superController.setEngine(engine);
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