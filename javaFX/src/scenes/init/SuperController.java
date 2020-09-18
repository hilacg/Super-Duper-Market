package scenes.init;

import components.ProductController;
import course.java.sdm.engine.Engine;
import course.java.sdm.engine.Product;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javafx.event.ActionEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
    private FlowPane content;

    @FXML
    private Label errorMessage;

    @FXML
    private Label title;

    @FXML
    private VBox menu;

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
            menu.getChildren().forEach(menuBtn->menuBtn.setDisable(false));
      /*      VBox vbox = new VBox(30, loadBtn);
            Scene scene = new Scene(vbox, 800, 500);
            primaryStage.setScene(scene);
            primaryStage.show();*/

        }catch (Exception e){
            errorMessage.setText(e.getMessage());
        }

    }

    @FXML
    void showCustomers(ActionEvent event) {
        content.getChildren().clear();
        title.setText("Customers");

    }

    @FXML
    void showNewOrder(ActionEvent event) {

    }

    @FXML
    void showOrderHistory(ActionEvent event) {

    }

    @FXML
    void showProducts(ActionEvent event) {
        content.getChildren().clear();
        title.setText("Products");
        for (Product product : engine.getProducts().values()) {
            try {
                FXMLLoader loader = new FXMLLoader();
               loader.setLocation(getClass().getResource("/components/Product.fxml"));
                Parent root = loader.load();

                ProductController productController = loader.getController();
                productController.setDetails(product);

                content.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(product.toString() +
                    "\nSold in: " + product.getStoreCount()+" stores");
            System.out.printf("Average price: %.2f \nSold amount: %.2f\n\n",product.getAvgPrice(), product.getSoldAmount());
        }
    }

    @FXML
    void showStores(ActionEvent event) {

    }

}
