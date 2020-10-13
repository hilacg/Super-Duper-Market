package components.order;

import course.java.sdm.engine.Engine;
import course.java.sdm.engine.Order;
import course.java.sdm.engine.Product;
import course.java.sdm.engine.Store;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Map;

public class SummaryContoller {

    Map.Entry<Integer, Map<Integer, Double>> storeOrder;
    Store store;
    Engine engine;

    @FXML
    private Label nameLabel;

    @FXML
    private Text detailsLabel;
    @FXML
    private VBox storePane;


    public void setDetails(Order order,int storeSerial, Engine engine) {
        this.engine = engine;
        store = engine.getStores().get(storeSerial);
        nameLabel.textProperty().bind(Bindings.format(store.getName()));
        detailsLabel.textProperty().bind(Bindings.format("ID: " + store.getSerialNumber() + "\nPPK: " + store.getPPK()
        + String.format("\nDistance from customer: %.2f \nDelivery price: %.2f" , order.getDistance(storeSerial),order.getDeliveryPrice())));
        order.getStoreProducts().get(storeSerial).forEach((productSerial,amount)->{
            Product product = engine.getProducts().get(productSerial);
            String s = getProductDetails(productSerial,product,amount);
            Text productText = new Text(s+"\nNot from Discount");
            productText.getStyleClass().add("text-id");
            storePane.getChildren().add(productText);

        });
        Map<Integer, Double> discountsProduct = order.getDiscountsProducts().get(storeSerial);
        if(discountsProduct!=null) {
            discountsProduct.forEach((productSerial, amount) -> {
                Product product = engine.getProducts().get(productSerial);
                String s = getProductDetails(productSerial, product, amount);
                Text productText = new Text(s + "\nFrom Discount");
                productText.getStyleClass().add("text-id");
                storePane.getChildren().add(productText);
            });
        }
        Text sumText = new Text(String.format("\nOrder price: %.2f  \nDelivery price: %.2f \nTotal order price: %.2f"
                ,order.getPrice() ,order.getDeliveryPrices().get(storeSerial), (order.getPrice() + order.getDeliveryPrices().get(storeSerial))));
        sumText.getStyleClass().add("text-id");
        storePane.getChildren().add(sumText);

    }

    private String getProductDetails(int productSerial,Product product,Double amount) {
        return "\n" + product.getName() + "\nSerial: " + product.getSerialNumber()
                + "\nBuying method: " + product.getMethod() + "\nAmount: " + amount + "\nPrice: " + store.getProductPrices().get(productSerial)
                + " \nTotal price: " + (store.getProductPrices().get(productSerial) * amount);
    }
}
