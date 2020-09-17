package components;

import course.java.sdm.engine.Product;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProductController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label detailsLabel;

    public void setDetails(Product product) {
        nameLabel.textProperty().bind(Bindings.concat(product.getName()));
 //       detailsLabel.textProperty().bind(product.toString());
    }
}
