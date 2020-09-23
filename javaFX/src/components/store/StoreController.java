package components.store;

import components.product.ProductController;
import course.java.sdm.engine.Product;
import course.java.sdm.engine.Store;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Map;

public class StoreController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label detailsLabel;

    @FXML
    private FlowPane storeProducts;

    public void setDetails(Store store, Map<Integer,Product> allProducts) {
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
                Button updateBtn = new Button("update price");
                updateBtn.setLayoutX(60);
                updateBtn.setLayoutX(260);
                Button remove = new Button("remove");
     /*           updateBtn.setLayoutX(90);
                updateBtn.setLayoutX(260);*/
                root.getChildren().add(updateBtn);
                root.getChildren().add(remove);

                ProductController productController = loader.getController();
                productController.setDetails(allProducts.get(product.getKey()), res);

                storeProducts.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
