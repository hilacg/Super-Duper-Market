package components.order;

import course.java.sdm.engine.Discount;
import course.java.sdm.engine.Engine;
import course.java.sdm.engine.Offer;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import javax.swing.*;
import java.util.List;

public class DiscountController {

    private Engine engine;
    @FXML
    private Label bought;
    @FXML
    private Label operator;
    @FXML
    private FlowPane offersPane;
    @FXML
    private Label discountName;

    @FXML
    private GridPane discountPane;

    @FXML
    void selectDiscount(MouseEvent event) {
        if(discountPane.getStyleClass().contains("selected"))
            discountPane.getStyleClass().remove("selected");
        else
          discountPane.getStyleClass().add("selected");
    }

    public void setDetails(Discount discount, Engine engine) {
        this.engine = engine;
        bought.textProperty().bind(Bindings.concat(discount.getQuantity() + " " + engine.getProducts().get(discount.getItemId()).getName()+", "));
        operator.textProperty().bind(Bindings.concat(discount.getOperator().equals(Discount.Operator.IRRELEVANT) ? " " : discount.getOperator()));
        discountName.textProperty().bind(Bindings.concat(discount.getName()));
        this.setOffers(discount.getOffers(),discount.getOperator());

    }

    private void setOffers(List<Offer> offers, Discount.Operator operator) {
        ToggleGroup group = new ToggleGroup();
        offers.forEach(offer->{
            String of = (offer.getQuantity()+" "+ engine.getProducts().get(offer.getItemId()).getName()+ " for additional "+ offer.getForAdditional()+" Nis");
            switch (operator) {
                case ONE_OF:{
                    RadioButton radio = new RadioButton(of);
                    radio.setSelected(true);
                    radio.setId(offer.getItemId().toString());
                    radio.setToggleGroup(group);
                    offersPane.getChildren().add(radio);
                    break;
                }
                default:{
                    offersPane.getChildren().add(new Label(of));
                    break;
                }

            }
        });

    }


}
