package components.store;

import components.product.ProductController;
import course.java.sdm.engine.Engine;
import course.java.sdm.engine.Product;
import course.java.sdm.engine.Store;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class StoreController {

    private Stage primaryStage;
    private Engine engine;
    List<Product> products;
    @FXML
    private Label nameLabel;

    @FXML
    private Label detailsLabel;

    @FXML
    private FlowPane storeProducts;

    @FXML
    private Button addBtn;

    @FXML
    void addProduct(ActionEvent event) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("Set Amount");

        ScrollPane scroll  = new ScrollPane();
        scroll.setPannable(true);
        scroll.setPrefWidth(800);
        scroll.setPrefHeight(600);
        scroll.setFitToWidth(true);
        scroll.getStylesheets().addAll(primaryStage.getScene().getRoot().getStylesheets());

        FlowPane addProduct = new FlowPane();
        addProduct.setPrefWidth(800);

        VBox content = new VBox();
        addProduct.setPrefWidth(Region.USE_COMPUTED_SIZE);
        scroll.setContent(content);
        content.setSpacing(10);
        content.setId("content");
        content.setPadding(new Insets(10, 10, 10, 10));
        content.getChildren().add(new Text("Please chose product and amount to add"));

        TextField amount = new TextField();
        amount.setPrefWidth(Region.USE_COMPUTED_SIZE);
        amount.setPromptText("Amount");
        Label message = new Label();
        message.setTextFill(Paint.valueOf("Red"));
        Button confirm = new Button("Confirm");
        confirm.setOnAction(e->{

        });
        content.getChildren().add(amount);
        content.getChildren().add(message);
        content.getChildren().add(confirm);
        content.getChildren().add(addProduct);

        for (Product product : engine.getProducts().values()) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/components/product/Product.fxml"));
                Parent root = loader.load();

                ProductController productController = loader.getController();
                productController.setDetails(product,"");
                root.setOnMouseClicked(e->{
                    Node prevSelected = addProduct.lookup(".selected");
                    if(prevSelected!=null)
                        prevSelected.getStyleClass().remove("selected");
                    root.getStyleClass().add("selected");
                });
                addProduct.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Scene dialogScene = new Scene(scroll);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    public void setDetails(Store store, Map<Integer,Product> allProducts, Stage primaryStage,Engine engine) {
        this.primaryStage = primaryStage;
        this.engine = engine;
        nameLabel.textProperty().bind(Bindings.concat(store.getName()));
        detailsLabel.textProperty().bind(Bindings.concat(store.toString()));
        this.getProducts(store, allProducts);
    }

    private void getProducts(Store store, Map<Integer,Product> allProducts) {
        String res = "";
        for (Map.Entry<Integer, Integer> product : store.getProductPrices().entrySet()) {
            res = "";
            res += "\n  " + (allProducts.get(product.getKey()).getName()) + "\n     price: " + (product.getValue().toString()) +
                    "\n     amount sold: " + (store.getProductsSold().get(product.getKey()));

            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../product/Product.fxml"));
                Pane root = loader.load();
                HBox buttonsBox = new HBox();
                buttonsBox.setSpacing(20);
                Button updateBtn = new Button("update price");
                updateBtn.setLayoutX(60);
                updateBtn.setLayoutX(260);
                Button remove = new Button("remove");
                buttonsBox.getChildren().add(updateBtn);
                buttonsBox.getChildren().add(remove);
                root.getChildren().add(buttonsBox);

                ProductController productController = loader.getController();
                productController.setDetails(allProducts.get(product.getKey()), res);

                storeProducts.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
