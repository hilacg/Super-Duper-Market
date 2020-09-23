package components.order;

import java.awt.*;
import course.java.sdm.engine.Customer;
import course.java.sdm.engine.Engine;
import course.java.sdm.engine.Product;
import course.java.sdm.engine.Store;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


public class OrderController {

    private Engine engine;
    private Map<Integer, Customer> customers = new HashMap<>();
    private ObservableList<TableItem> productsList =FXCollections.observableArrayList();
    private Map<Integer, Float> productsToOrder = new HashMap<>();
    private SimpleBooleanProperty disableFinish;
    private SimpleStringProperty chosenCustomer;
    private SimpleStringProperty chosenType;
    private SimpleDateFormat chosenDate;
    private SimpleStringProperty chosenStore;
    private ComboBox<?> storeCombo;
    private DatePicker datePicker;

    @FXML
    private GridPane orderPane;
    @FXML
    private ComboBox customerCombo;
    @FXML
    private RadioButton staticRadio;
    @FXML
    private RadioButton dynamicRadio;
    @FXML
    private ToggleGroup orderType;
    @FXML
    private TableView<TableItem> products;
    @FXML
    private Button addBtn;
    @FXML
    private Button finishBtn;
    @FXML
    private TableColumn<TableItem, String> productId;
    @FXML
    private TableColumn<TableItem, String>  productName;
    @FXML
    private TableColumn<TableItem, String> productMethod;
    @FXML
    private TableColumn<TableItem, String> productPrice;


    public OrderController(){
        chosenCustomer = new SimpleStringProperty();
        chosenType = new SimpleStringProperty();
        chosenDate = new SimpleDateFormat();
      //  disableFinish = new SimpleBooleanProperty(customerCombo.getValue()==null || datePicker.getValue() == null || orderType.getSelectedToggle()==null);
    }
    public void setDetails(Engine engine){
        this.engine = engine;
        this.customers = engine.getAllCustomers();
        final ObservableList customerObservable = FXCollections.observableArrayList();
        for(Customer customer : customers.values()){
            String item = customer.getId() + " " + customer.getName()+ " (" + customer.getLocation().x+ ", " + customer.getLocation().y + ")";
            customerObservable.add(item);
        }
        customerCombo.setItems(customerObservable);

    }


    @FXML
    protected void initialize() {
        productId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        productName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        productMethod.setCellValueFactory(new PropertyValueFactory<>("Method"));
        productPrice.setCellValueFactory(new PropertyValueFactory<>("Price"));
        products.setItems(productsList);

      /*  chosenCustomer.bind(Bindings.concat(customerCombo.getSelectionModel()));
   //     chosenDate.bind(Bindings.concat(datePicker.getValue().toString()));
        chosenType.bind(Bindings.concat(orderType.selectedToggleProperty()));*/

       /* chosenCustomer.bind(Bindings.concat(customerCombo.getValue()));
        chosenType.bind(Bindings.concat(orderType.selectedToggleProperty()));
        chosenDate.bind(Bindings.concat(datePicker.getValue()));
        disableFinish.bind(Bindings.chosenCustomer!= null || chosenType!= null || chosenDate!= null);
        finishBtn.disableProperty().bind(disableFinish);*/
        datePicker = new DatePicker();
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0 );
            }
        });
        datePicker.setEditable(false);
        orderPane.add(datePicker,1,1);
    }


    @FXML
    void dynamicOrder(ActionEvent event) {
        productPrice.setVisible(false);
        productsList.clear();
        orderPane.getChildren().remove(storeCombo);
        for(Product product : engine.getProducts().values()){
            productsList.add(new TableItem(product.getSerialNumber(), product.getName(), product.getMethod().toString(), " "));
        }
    }


    @FXML
    void showStores(ActionEvent event) {
        productPrice.setVisible(true);
        productsList.clear();
        storeCombo = new ComboBox();
        final ObservableList storeObservable = FXCollections.observableArrayList();
        for(Store store : engine.getStores().values()){
            String item = store.getSerialNumber() + " " + store.getName()+ " (" + store.getLocation().x+ ", " + store.getLocation().y + ")";
            storeObservable.add(item);
        }
        storeCombo.setItems(storeObservable);
        storeCombo.setPromptText("Select Store");
        storeCombo.setOnAction((ActionEvent)->{
            productsList.clear();
            int serial =  Integer.parseInt(storeCombo.getValue().toString().split(" ")[0]);
            Store store = engine.getStores().get(serial);
            for(Map.Entry<Integer,Integer> productPrice : store.getProductPrices().entrySet()){
                Product product = engine.getProducts().get(productPrice.getKey());
                productsList.add(new TableItem(product.getSerialNumber(), product.getName(), product.getMethod().toString(), String.valueOf(productPrice.getValue())));
            }

        });
        orderPane.add(storeCombo,1,3);
        chosenStore.bind(Bindings.concat(storeCombo.getSelectionModel().getSelectedItem().toString().split(" ")[0]));
    }

    @FXML
    void addToCart(ActionEvent event) {
        TableItem productToAdd = products.getSelectionModel().getSelectedItem();
        productsToOrder.put(productToAdd.getSerial(), productsToOrder.getOrDefault(productToAdd.getSerial(), 0f) + 1);
    }
    @FXML
    void finishOrder(ActionEvent event) {
        datePicker.getValue();

  //      engine.setNewOrder(chosenStore.getValue(),chosenDate.getValue(), productsToOrder, new Point(4,6));
    }

public static class TableItem{
    private final SimpleIntegerProperty serial;
    private final SimpleStringProperty name;
    private final SimpleStringProperty price;
    private final SimpleStringProperty method;


        protected TableItem(int serial, String name, String method,String price){
         this.serial = new SimpleIntegerProperty(serial);
         this.name =new SimpleStringProperty( name);
         this.method =new SimpleStringProperty( method);
         this.price = new SimpleStringProperty(price);
        }

    public String getName() {
        return name.getValue();
    }

    public String getPrice() {
        return price.getValue();
    }

    public String getMethod() {
        return method.getValue();
    }

    public Integer getSerial() {
        return serial.getValue();
    }

    public void setMethod(String method) {
        this.method.set(method);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setPrice(String price) {
        this.price.set(price);
    }

    public void setSerial(int serial) {
        this.serial.set(serial);
    }

}
}
