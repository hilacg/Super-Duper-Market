package components.order;

import course.java.sdm.engine.Discount;
import course.java.sdm.engine.Engine;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;

public class DiscountController {

    private Engine engine;

    @FXML
    private Label bought;

    @FXML
    private Label operator;

    @FXML
    private FlowPane offers;
    @FXML
    private Label discountName;

    @FXML
    void selectDiscount(MouseEvent event) {

    }

    public void setDetails(Discount discount, Engine engine) {
        this.engine = engine;
        bought.textProperty().bind(Bindings.concat(discount.getQuantity() + " " + engine.getProducts().get(discount.getItemId()).getName()));
        operator.textProperty().bind(Bindings.concat(discount.getOperator().equals(Discount.Operator.IRRELEVANT) ? " " : discount.getOperator()));
        discountName.textProperty().bind(Bindings.concat(discount.getName()));

    }
}
