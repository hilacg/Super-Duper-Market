package components.main;

import components.customer.CustomerController;
import components.order.OrderController;
import components.product.ProductController;
import components.load.LoadController;
import components.store.StoreController;
import course.java.sdm.engine.Customer;
import course.java.sdm.engine.Engine;
import course.java.sdm.engine.Product;
import course.java.sdm.engine.Store;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javafx.event.ActionEvent;

import java.io.File;

import java.io.IOException;
import java.net.URL;

import javafx.stage.Stage;

public class SuperController {

    private Engine engine;
    private Stage primaryStage;
    private LoadController loadComponentController;
    private String currentTheme="themes/default.css";


    public void setEngine(Engine engine) {
        this.engine = engine;
    }
    public void setPrimaryStage(Stage primaryStage) { this.primaryStage = primaryStage;}

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
    private FlowPane content;
    @FXML
    private ComboBox themeCombo;
    @FXML
    private ScrollPane root;
    @FXML
    protected void initialize() {
        themeCombo.setItems(FXCollections.observableArrayList("default", "dark","theme2"));
    }

    @FXML
    void changeTheme(ActionEvent event) {
        root.getStylesheets().remove(getClass().getResource(currentTheme).toExternalForm());
        currentTheme = "themes/" + themeCombo.getSelectionModel().getSelectedItem().toString() +".css";
        root.getStylesheets().add(getClass().getResource(currentTheme).toExternalForm());
        primaryStage.show();
    }


    @FXML
    private void showLoad() throws IOException {
        content.getChildren().clear();
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("../load/Load.fxml");
        fxmlLoader.setLocation(url);
        GridPane loadComponent = fxmlLoader.load(fxmlLoader.getLocation().openStream());
        loadComponentController = fxmlLoader.getController();
        loadComponentController.setMainController(this);
        content.getChildren().add(loadComponent);
        this.loadXML();

    }




    void loadXML() throws IOException {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open XML");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("XML Files", "*.xml"));
            File chosenFile = fileChooser.showOpenDialog(primaryStage);

            if(chosenFile != null) {
                engine.loadXML(chosenFile.getPath(),
                        () -> {
                            menu.getChildren().forEach(menuBtn -> menuBtn.setDisable(false));
                            engine.initMembers();
                        }
                );
            }


    }

    public void bindTaskToUIComponents(Task<Boolean> aTask, Runnable onFinish) {
        loadComponentController.bindTaskToUIComponents(aTask,onFinish);
    }

    @FXML
    void showCustomers(ActionEvent event) {
        content.getChildren().clear();
        title.setText("Customers");
        for (Customer customer : engine.getAllCustomers().values()) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/components/customer/Customer.fxml"));
                Parent root = loader.load();

                CustomerController customerController = loader.getController();
                customerController.setDetails(customer);

                content.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void showNewOrder(ActionEvent event) {
        content.getChildren().clear();
        title.setText("New Order");
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/components/order/newOrder.fxml"));
                Parent root = loader.load();

                OrderController orderController = loader.getController();
                orderController.setDetails(engine);

                content.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
               loader.setLocation(getClass().getResource("/components/product/Product.fxml"));
                Parent root = loader.load();

                ProductController productController = loader.getController();
                productController.setDetails(product,"");

                content.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void showStores(ActionEvent event) {
        content.getChildren().clear();
        title.setText("Stores");
        for (Store store : engine.getStores().values()) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/components/store/Store.fxml"));
                Parent root = loader.load();

                StoreController storeController = loader.getController();
                storeController.setDetails(store,engine.getProducts());

                content.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}