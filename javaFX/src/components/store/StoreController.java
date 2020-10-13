package components.store;

import components.order.OrderController;
import components.product.ProductController;
import course.java.sdm.engine.Discount;
import course.java.sdm.engine.Engine;
import course.java.sdm.engine.Product;
import course.java.sdm.engine.Store;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StoreController {

    private Stage primaryStage;
    private Engine engine;
    private Store store;
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
        content.getChildren().add(new Text("Please choose a product and enter a price:"));
        TextField amount = new TextField();
        amount.setPrefWidth(Region.USE_COMPUTED_SIZE);
        amount.setPromptText("Amount");
        Label message = new Label();
        message.setTextFill(Paint.valueOf("Red"));
        Button confirm = new Button("Confirm");
        confirm.setOnAction(e->{
            try{
            String productPrice = amount.getText();
            Node selectedP = content.lookup(".selected");
            VBox selectedProductBox = (VBox) selectedP;

            //  Product selectedProduct = engine.getProducts().values().stream().filter(product-> product.getName().equals(selectedProductBox.getChildren().get(0).toString())).collect(Collectors.toList()).get(0);
              Label productChosenId = (Label)selectedProductBox.getChildren().get(1);
              String productSerial = productChosenId.getText().split(" ")[1].split("\\n")[0];
              engine.addProductToStore(Integer.parseInt(productSerial),this.store,Integer.parseInt(productPrice));
                showAlerts(Alert.AlertType.INFORMATION, "Adding New Product", "Product was Added Successfully!");
                dialog.close();
                storeProducts.getChildren().clear();
                getProducts(store, engine.getProducts());

            }
            catch(Exception exp){
                if(exp.getMessage() != null)
                  showAlerts(Alert.AlertType.ERROR, "Error adding product!", exp.getMessage());
                else
                    showAlerts(Alert.AlertType.ERROR, "Error adding product!","Please select an item and a price");
            }
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
        this.store = store;
        nameLabel.textProperty().bind(Bindings.concat(store.getName()));
        detailsLabel.textProperty().bind(Bindings.concat(store.toString()));
        this.getProducts(store, allProducts);
    }

    private void getProducts(Store store, Map<Integer,Product> allProducts) {
        String res = "";
        for (Map.Entry<Integer, Integer> product : store.getProductPrices().entrySet()) {
            res = "";
            res += "\n" + (allProducts.get(product.getKey()).getName()) + "\nprice: " + (product.getValue().toString()) +
                    "\namount sold: " + (store.getProductsSold().get(product.getKey()));

            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/components/product/Product.fxml"));
                Pane root = loader.load();
                ProductController productController = loader.getController();
                HBox buttonsBox = new HBox();
                buttonsBox.setSpacing(20);
                Button updateBtn = new Button("update price");
                updateBtn.setLayoutX(60);
                updateBtn.setLayoutX(260);
                Button remove = new Button("remove");
              remove.setOnAction(e->{
                  try {
                      engine.deleteProduct(store, allProducts.get(product.getKey()));
                      storeProducts.getChildren().remove(root);
                  }catch(Exception exp) {
                      showAlerts(Alert.AlertType.ERROR, "Error deleting product!", exp.getMessage());
                  }
                });
                updateBtn.setOnAction(e->{updateClicked(allProducts,product,store,root,productController);

                });
                buttonsBox.getChildren().add(updateBtn);
                buttonsBox.getChildren().add(remove);
                root.getChildren().add(buttonsBox);

                productController.setDetails(allProducts.get(product.getKey()), res);

                storeProducts.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void updateClicked(Map<Integer, Product> allProducts, Map.Entry<Integer, Integer> product, Store store, Pane root, ProductController productController){
        final Stage dialog = new Stage();

        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("Enter new price");
        VBox dialogVbox = new VBox(10);
        dialogVbox.getStylesheets().addAll(primaryStage.getScene().getRoot().getStylesheets());
        dialogVbox.setId("content");
        dialogVbox.setPadding(new Insets(10, 10, 10, 10));
        dialogVbox.getChildren().add(new Text("Please Enter a new price for " + allProducts.get(product.getKey()).getName() + ": "));
        Label errorLabel = new Label();
        errorLabel.setTextFill(Paint.valueOf("red"));
        TextField amount = new TextField();
        dialogVbox.getChildren().add(amount);
        Button confirm = new Button();
        confirm.setText("Confirm");
        confirm.setOnAction(e->{
            String input = amount.getText();
            try {
                engine.updateProductPrice(store, allProducts.get(product.getKey()), Integer.parseInt(input));
                dialog.close();
                showAlerts(Alert.AlertType.INFORMATION, "Product price", "Product price was changed successfully");
                storeProducts.getChildren().clear();
                getProducts(store, allProducts);

            }catch (Exception exp){
                errorLabel.setText("The amount must be a round number");
            }
        });
        dialogVbox.getChildren().add(errorLabel);
        dialogVbox.getChildren().add(confirm);
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }
    private void showAlerts(Alert.AlertType type, String title, String content){
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setContentText(content);
        a.setHeaderText(null);
        a.show();
    }
}
