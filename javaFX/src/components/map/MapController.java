package components.map;

import course.java.sdm.engine.Customer;
import course.java.sdm.engine.Engine;
import course.java.sdm.engine.Store;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;


import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MapController {

    Engine engine;


    @FXML
    private VBox details;
    @FXML
    private BorderPane mapPane;
    @FXML
    private Label nameLabel;

    @FXML
    private Label detailsLabel;
    @FXML
    private AnchorPane mapContainer;

    @FXML
    protected void initialize() {

    }

    public void setDetails(Engine engine) {
        this.engine = engine;
        setMap();
    }

    private void setMap() {
        GridPane map = new GridPane();
  //     map.setGridLinesVisible(true);
        map.setId("map");
        Point limits = engine.findMapLimits();
        buildMap(map,limits);
        addCustomers(map);
        addStores(map);
        mapContainer.getChildren().add(map);
    }

    private void addCustomers(GridPane map) {
        engine.getAllCustomers().values().forEach(customer->{
            Point location = customer.getLocation();
            Image image = new Image(this.getClass().getResourceAsStream("/resources/customer.png"));
            ImageView customerIcon = new ImageView(image);
            customerIcon.setOnMouseClicked(e->{
                nameLabel.textProperty().bind(Bindings.concat(customer.getName()));
                String customerInf = "Location: " + "(" + customer.getLocation().x + ", " + customer.getLocation().y + ")"
                       +"\nId: " + customer.getId() + "\nTotal orders: " + customer.getTotalOrders();
                detailsLabel.textProperty().bind(Bindings.format(customerInf));
            });
            customerIcon.setPreserveRatio(true);
            customerIcon.setFitHeight(50);
            map.add(customerIcon,(int)location.getX(),(int)location.getY());

        });
    }

    private void addStores(GridPane map) {
        engine.getStores().values().forEach(store->{
            Point location = store.getLocation();
            Image image = new Image(this.getClass().getResourceAsStream("/resources/store.png"));
            ImageView customerIcon = new ImageView(image);
            customerIcon.setOnMouseClicked(e->{
                nameLabel.textProperty().bind(Bindings.concat(store.getName()));
                String storeInf = "Location: " + "(" + store.getLocation().x + ", " + store.getLocation().y + ")"
                + "\nId: " + store.getSerialNumber() + "\nPPK: " + store.getPPK() + "\nTotal orders: " + store.getTotalOrders();
                detailsLabel.textProperty().bind(Bindings.concat(storeInf));
            });
            customerIcon.setPreserveRatio(true);
            customerIcon.setFitHeight(50);
            map.add(customerIcon,(int)location.getX(),(int)location.getY());
        });
    }


    private void buildMap(GridPane map,Point limits) {
        for(int i=0; i<= limits.x+1;i++){
            map.getColumnConstraints().add(new ColumnConstraints(50));
        }
        for(int j=0; j<= limits.y+1;j++){
            map.getRowConstraints().add(new RowConstraints(50));
        }
    }
}
