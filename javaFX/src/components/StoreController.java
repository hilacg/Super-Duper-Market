package components;

import course.java.sdm.engine.Store;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StoreController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label detailsLabel;

    public void setDetails(Store store) {
        nameLabel.textProperty().bind(Bindings.concat(store.getName()));
        detailsLabel.textProperty().bind(Bindings.concat(getDetails(store)));
    }

    public String getDetails( Store product){
     /*   String s =
                product.toString() +
                        "\nSold in: " + product.getStoreCount()+" stores\n";
        s = s.concat( String.format("Average price: %.2f \nSold amount: %.2f\n",product.getAvgPrice(), product.getSoldAmount())) ;
        return s;*/
        return  " ";
    }
}
