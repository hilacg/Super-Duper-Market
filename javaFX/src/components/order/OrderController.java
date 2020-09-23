package components.order;


import components.product.ProductController;
import course.java.sdm.engine.*;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class OrderController {

    private Engine engine;
    private Stage primaryStage;
    private Map<Integer, Customer> customers = new HashMap<>();
    private ObservableList<TableItem> productsList =FXCollections.observableArrayList();
    private Map<Integer, Float> productsToOrder = new HashMap<>();
    private Map<Integer,  Map<Integer, Float>> storeProductsToOrder;
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
    public void setDetails(Engine engine, Stage primaryStage){
        this.engine = engine;
        this.primaryStage = primaryStage;
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
//        chosenStore.bind(Bindings.concat(storeCombo.getSelectionModel().getSelectedItem().toString().split(" ")[0]));
    }

    @FXML
    void addToCart(ActionEvent event) {
        TableItem productToAdd = products.getSelectionModel().getSelectedItem();
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("Set Amount");
        VBox dialogVbox = new VBox(10);
  //      dialogVbox.setStyle(primaryStage.getScene().getRoot().getStyle());
        dialogVbox.setPadding(new Insets(10, 10, 10, 10));
        dialogVbox.getChildren().add(new Text("Please Enter " + productToAdd.getName() + " Amount:"));
        Label errorLabel = new Label();
        errorLabel.setTextFill(Paint.valueOf("red"));
        TextField amount = new TextField();
        dialogVbox.getChildren().add(amount);
        Button confirm = new Button();
        confirm.setText("Confirm");
        confirm.setOnAction((evt)->{
            String input = amount.getText();
            try {
                engine.getProducts().get(productToAdd.getSerial()).getMethod().validateAmount(input);
                productsToOrder.put(productToAdd.getSerial(), productsToOrder.getOrDefault(productToAdd.getSerial(), 0f) + Integer.parseInt(input));
                dialog.close();
            }catch (Exception e){
                errorLabel.setText(e.getMessage());
            }

        });
        dialogVbox.getChildren().add(errorLabel);
        dialogVbox.getChildren().add(confirm);
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
    //   dialogScene.getStylesheets().add(primaryStage.getScene().getRoot().getStylesheets());
        dialog.setScene(dialogScene);
        dialog.show();
    }
    @FXML
    void finishOrder(ActionEvent event) {
      //  Order newOrder = engine.setNewOrder(chosenStore,deliveryDate, customerLocation);
        storeProductsToOrder = new HashMap<>();
        if(orderType.getSelectedToggle() == dynamicRadio){
            storeProductsToOrder = engine.findOptimalOrder(productsToOrder);
        }
        else{
            storeProductsToOrder.put(Integer.parseInt(storeCombo.getValue().toString().split(" ")[0]), productsToOrder);
        }
        FlowPane content = (FlowPane)primaryStage.getScene().lookup("#content");
        content.getChildren().clear();
        showDiscountsPage(content);
    }

    private void showDiscountsPage(FlowPane content) {
        VBox discountsBox = new VBox(10);
        discountsBox.setPadding(new Insets(10, 10, 10, 10));
        discountsBox.setPrefHeight(content.getPrefHeight());
        discountsBox.setPrefWidth(content.getPrefHeight());
        content.getChildren().add(discountsBox);

        addDiscounts(discountsBox);
    }

    private void addDiscounts(VBox discountsBox) {
        for(Map.Entry<Integer,  Map<Integer, Float>> storeAndProduct : storeProductsToOrder.entrySet()){
            Store store = engine.getStores().get(storeAndProduct.getKey());
            List<Discount> discounts = store.getDiscounts().stream()
                    .filter(discount-> storeAndProduct.getValue().containsKey(discount.getItemId())
                    &&storeAndProduct.getValue().get(discount.getItemId())>=discount.getQuantity())
                    .collect(Collectors.toList());
            discounts.forEach(discount->{
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/components/order/discount.fxml"));
                    Parent root = loader.load();

                    DiscountController discountController = loader.getController();
                    discountController.setDetails(discount,engine);

                    discountsBox.getChildren().add(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                    }
            );

        }
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
