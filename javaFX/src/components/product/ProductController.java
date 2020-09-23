package components.product;

import course.java.sdm.engine.Product;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProductController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label detailsLabel;

    public void setDetails(Product product,String inStore) {
        nameLabel.textProperty().bind(Bindings.concat(product.getName()));
        if(inStore.length() > 0)
            detailsLabel.textProperty().bind(Bindings.concat(product.toString()).concat(inStore));
        else detailsLabel.textProperty().bind(Bindings.concat(getDetails(product)));
    }

    public String getDetails(Product product){
        String s =
             product.toString() +
                    "\nSold in: " + product.getStoreCount()+" stores\n";
             s = s.concat( String.format("Average price: %.2f \nSold amount: %.2f\n",product.getAvgPrice(), product.getSoldAmount())) ;
             return s;
    }
}
