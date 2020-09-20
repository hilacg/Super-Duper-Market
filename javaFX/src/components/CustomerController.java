package components;

import course.java.sdm.engine.Customer;
import course.java.sdm.engine.Product;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CustomerController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label detailsLabel;

    public void setDetails(Customer customer) {
        nameLabel.textProperty().bind(Bindings.concat(customer.getName()));
        detailsLabel.textProperty().bind(Bindings.concat(customer.toString()));
    }

}
