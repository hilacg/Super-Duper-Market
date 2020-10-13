package components.order;


import components.main.SuperController;
import components.map.MapController;
import course.java.sdm.engine.*;
import javafx.animation.PathTransition;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public class OrderController {

    private Engine engine;
    private Stage primaryStage;
    private Customer selectedCustomer;
    private Order newOrder;
    FlowPane content;
    private List<Discount> discounts = new ArrayList<>();
    private Map<Integer, Customer> customers = new HashMap<>();
    private ObservableList<TableItem> productsList =FXCollections.observableArrayList();
    private ObservableList<TableItem> cartList =FXCollections.observableArrayList();
    private Map<Integer, Double> productsToOrder = new HashMap<>();
    private Map<Integer,  Map<Integer, Double>> storeProductsToOrder;
    private SimpleBooleanProperty isCartEmpty = new SimpleBooleanProperty(true);
    private ComboBox<?> storeCombo;
    private DatePicker datePicker;
    private PathTransition transition = new PathTransition();
    private SimpleBooleanProperty animation = new SimpleBooleanProperty(false);

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
    @FXML
    private TableView<TableItem> cartProducts;
    @FXML
    private TableColumn<TableItem, Integer>cartId;
    @FXML
    private TableColumn<TableItem, String> cartName;
    @FXML
    private TableColumn<TableItem, String> cartMethod;
    @FXML
    private TableColumn<TableItem, Integer> cartAmount;
    @FXML
    private ImageView productImg;
    @FXML
    private ImageView fillCartImg;
    @FXML
    private ImageView cartImg;
    @FXML
    private Button  enableAnimation;




    @FXML
    protected void initialize() {
        setAnimation();
        animation.addListener((observable, oldValue, newValue) -> {
            enableAnimation.setText("Animation ".concat(newValue ? "enabled" : "disabled"));
        });
        setPictures();
        fillCartImg.visibleProperty().bind(isCartEmpty.not());
        productId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        productName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        productMethod.setCellValueFactory(new PropertyValueFactory<>("Method"));
        productPrice.setCellValueFactory(new PropertyValueFactory<>("Price"));
        products.setItems(productsList);
        cartId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        cartName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        cartMethod.setCellValueFactory(new PropertyValueFactory<>("Method"));
        cartAmount.setCellValueFactory(new PropertyValueFactory<>("Amount"));
        cartProducts.setItems(cartList);
        addBtn.disableProperty().bind(products.getSelectionModel().selectedItemProperty().isNull());
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

        finishBtn.disableProperty().bind(customerCombo.valueProperty().isNull()
                .or(datePicker.valueProperty().isNull())
                .or(isCartEmpty));
    }

    private void setPictures() {
        Image image = new Image(this.getClass().getResourceAsStream("/resources/fillCart.png"));
        fillCartImg.setImage(image);
        image = new Image(this.getClass().getResourceAsStream("/resources/cart.png"));
        cartImg.setImage(image);
        image = new Image(this.getClass().getResourceAsStream("/resources/product.png"));
        productImg.setImage(image);
    }

    @FXML
    void enableAnimation(ActionEvent event) {
        animation.setValue(!animation.get());
        if(animation.get())
            setAnimation();
        else
        {

            transition = new PathTransition();
        }
    }

    private void setAnimation() {
        Polyline polyLine = new Polyline(-50.0,50.0,150.0,-100.0,330.0,0.0);
  //      Polyline polyLine = new Polyline(0.0,0.0,150,-350.0,380.0,-180.0);
        transition.setNode(productImg);
        transition.setDuration(Duration.seconds(2));
        transition.setPath(polyLine);
        transition.setCycleCount(1);
    }

    public void setDetails(Engine engine, Stage primaryStage){
        this.engine = engine;
        this.primaryStage = primaryStage;
        content = (FlowPane)primaryStage.getScene().lookup("#content");
        this.customers = engine.getAllCustomers();
        final ObservableList customerObservable = FXCollections.observableArrayList();
        for(Customer customer : customers.values()){
            String item = customer.getId() + " " + customer.getName()+ " (" + customer.getLocation().x+ ", " + customer.getLocation().y + ")";
            customerObservable.add(item);
        }
        customerCombo.setItems(customerObservable);

    }


    @FXML
    void dynamicOrder(ActionEvent event) {
        productsToOrder.clear();
        cartList.clear();
        isCartEmpty.set(true);
        productPrice.setVisible(false);
        productsList.clear();
        orderPane.getChildren().remove(storeCombo);
        for(Product product : engine.getProducts().values()){
            productsList.add(new TableItem(String.valueOf(product.getSerialNumber()), product.getName(), product.getMethod().toString(), " ",0.0));
        }
    }


    @FXML
    void showStores(ActionEvent event) {
        productsToOrder.clear();
        isCartEmpty.set(true);
        productPrice.setVisible(true);
        productsList.clear();
        cartList.clear();
        storeCombo = new ComboBox();
        final ObservableList storeObservable = FXCollections.observableArrayList();
        for (Store store : engine.getStores().values()) {
            String item = store.getSerialNumber() + " " + store.getName() + " (" + store.getLocation().x + ", " + store.getLocation().y + ")";
            storeObservable.add(item);
        }
        storeCombo.setItems(storeObservable);
        storeCombo.setPromptText("Select Store");
        storeCombo.setOnAction((ActionEvent) -> {
            productsList.clear();
            int serial = Integer.parseInt(storeCombo.getValue().toString().split(" ")[0]);
            Store store = engine.getStores().get(serial);
            for (Map.Entry<Integer, Integer> productPrice : store.getProductPrices().entrySet()) {
                Product product = engine.getProducts().get(productPrice.getKey());
                productsList.add(new TableItem(String.valueOf(product.getSerialNumber()), product.getName(), product.getMethod().toString(), String.valueOf(productPrice.getValue()), 0.0));
            }

        });
        orderPane.add(storeCombo, 1, 3);
    }

    @FXML
    void addToCart(ActionEvent event) {
        TableItem productToAdd = products.getSelectionModel().getSelectedItem();
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("Set Amount");
        VBox dialogVbox = new VBox(10);
        dialogVbox.getStylesheets().addAll(primaryStage.getScene().getRoot().getStylesheets());
        dialogVbox.setId("content");
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
                engine.getProducts().get(Integer.parseInt(productToAdd.getSerial())).getMethod().validateAmount(input);
                productsToOrder.put(Integer.parseInt(productToAdd.getSerial()), productsToOrder.getOrDefault(productToAdd.getSerial(), 0.0) + Double.parseDouble(input));
                cartList.add(new TableItem(productToAdd.getSerial(), productToAdd.getName(), productToAdd.getMethod(), " ",Double.parseDouble(input)));
                dialog.close();
                if(animation.getValue()) {
                    productImg.setVisible(true);
                    transition.play();
                    transition.setOnFinished(e -> {
                        productImg.setVisible(false);
                        isCartEmpty.set(false);
                    });
                }
                else
                    isCartEmpty.set(false);
            }catch (Exception e){
                errorLabel.setText(e.getMessage());
            }

        });
        dialogVbox.getChildren().add(errorLabel);
        dialogVbox.getChildren().add(confirm);
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();

    }
    @FXML
    void finishOrder(ActionEvent event) {
        storeProductsToOrder = new HashMap<>();
        selectedCustomer = engine.getAllCustomers().get(Integer.parseInt(customerCombo.getValue().toString().split(" ")[0]));
        if(orderType.getSelectedToggle() == dynamicRadio){
            storeProductsToOrder = engine.findOptimalOrder(productsToOrder);
            newOrder = engine.setNewOrder(selectedCustomer,storeProductsToOrder,datePicker.getValue());
            showOptimalOrder(newOrder);
        }
        else{
            storeProductsToOrder.put(Integer.parseInt(storeCombo.getValue().toString().split(" ")[0]), productsToOrder);
            newOrder = engine.setNewOrder(selectedCustomer,storeProductsToOrder,datePicker.getValue());
        }
        content.getChildren().clear();

        for(Map.Entry<Integer,  Map<Integer, Double>> storeAndProduct : storeProductsToOrder.entrySet()){
            Store store = engine.getStores().get(storeAndProduct.getKey());
            store.getDiscounts().forEach(discount -> {
                if(storeAndProduct.getValue().containsKey(discount.getItemId())){
                    double amountBought = storeAndProduct.getValue().get(discount.getItemId());
                    if (amountBought >= discount.getQuantity()) {
                        while (amountBought >= discount.getQuantity()) {
                            discounts.add(discount);
                            amountBought -= discount.getQuantity();
                        }
                    }
                }
            });
        }

        if(discounts.size()> 0)
             showDiscountsPage();
        else
            showOrderSum();
    }


    private void showOptimalOrder(Order newOrder) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("Order");

        ScrollPane scroll  = new ScrollPane();
        scroll.setPannable(true);
        scroll.setPrefWidth(500);
        scroll.setPrefHeight(700);
        scroll.setFitToWidth(true);


        scroll.getStylesheets().addAll(primaryStage.getScene().getRoot().getStylesheets());
        VBox dialogVbox = new VBox(10);
        dialogVbox.setPrefHeight(700);
        scroll.setContent(dialogVbox);
        dialogVbox.setPadding(new Insets(10));
        dialogVbox.setId("content");
        dialogVbox.getStylesheets().addAll(primaryStage.getScene().getRoot().getStylesheets());
        for(Map.Entry<Integer,  Map<Integer, Double>> storeAndProduct : storeProductsToOrder.entrySet()){
            Store store = engine.getStores().get(storeAndProduct.getKey());
            Label storeName = new Label(store.getName() + " serial: " + store.getSerialNumber());
            storeName.getStyleClass().add("subTitle");
            Label location = new Label("Store location: ("+ store.getLocation().x + ", "+ store.getLocation().y+")");
            Label delivery = new Label("PKK: " + store.getPPK() + String.format(", Distance: %.2f" , store.calculateDistance(selectedCustomer.getLocation()))
            + String.format(", Delivery price: %.2f" , store.getPPK()*store.calculateDistance(selectedCustomer.getLocation())));
            Label numOfTypes = new Label("Number of product types: " + storeAndProduct.getValue().size());
            newOrder.calculatePrice(engine.getStores());
            Label productsPrice = new Label(String.format("Products price: %.2f" , newOrder.calculateStorePrice(store.getSerialNumber())));
            dialogVbox.getChildren().addAll(storeName,location,delivery,numOfTypes,productsPrice);
        }
        Scene dialogScene = new Scene(scroll);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showDiscountsPage() {
        VBox discountsBox = new VBox(10);
        discountsBox.setPadding(new Insets(10, 10, 10, 10));
        content.getChildren().add(discountsBox);
        addDiscounts(discountsBox);
        Button confirm = new Button("Confirm");
        confirm.setOnAction(  e-> {
            Set<Node> discountNodes = discountsBox.lookupAll(".selected");
            for (Node node : discountNodes) {
                RadioButton chosenRadio = null;
                Label operatorLabel = (Label)node.lookup(".operator");
                if (operatorLabel.getText().equals("on of")) {
                    FlowPane offersPane = (FlowPane)node.lookup(".offersPane");
                    Set<Node> radioSet = offersPane.lookupAll(".radio-button");
                    for (Node radio : radioSet) {
                        RadioButton r = (RadioButton) radio;
                        if (r.isSelected())
                            chosenRadio = r;
                    }
                }
                GridPane discountPane = (GridPane) node;
                Label nameLabel = (Label)discountPane.getChildren().get(0).lookup("#discountName");
                Discount selectedDiscount = this.discounts.stream().filter(discount -> discount.getName().equals(nameLabel.getText())).collect(Collectors.toList()).get(0);
                newOrder.saveDiscounts(selectedDiscount, chosenRadio);
            }
            showOrderSum();
        });
        discountsBox.getChildren().add(confirm);
    }


    private void showOrderSum() {
        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(10, 10, 10, 10));
        contentBox.setPrefWidth(800);
        contentBox.setPrefHeight(600);


        FlowPane flowPane = new FlowPane();
        contentBox.setPadding(new Insets(10, 10, 10, 10));
        flowPane.setPrefWidth(800);
        flowPane.setPrefHeight(600);
        contentBox.getChildren().add(flowPane);

        newOrder.calculatePrice(engine.getStores());

        content.getChildren().clear();
        Button confirm = new Button("confirm");
        Button cancel = new Button("cancel");
        cancel.setOnAction(e->{
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Order Cancelled");
            a.setContentText("Order was cancelled!");
            a.setHeaderText(null);
            a.show();
            content.getChildren().clear();
        });
        confirm.setOnAction(e-> {
            newOrder.addDiscounts();
            engine.addOrder(newOrder);
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Order Confirmed");
            a.setContentText("Order was accepted successfully");
            a.setHeaderText(null);
            a.show();
            content.getChildren().clear();
        });
        contentBox.getChildren().add(confirm);
        contentBox.getChildren().add(cancel);
        for(Map.Entry<Integer, Map<Integer, Double>> storeOrder : newOrder.getStoreProducts().entrySet()){
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/components/order/OrderSummary.fxml"));
                Parent root = loader.load();

                SummaryContoller summaryContoller = loader.getController();
                summaryContoller.setDetails(newOrder, storeOrder.getKey(), engine);

                flowPane.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        content.getChildren().add(contentBox);
    }


    private void addDiscounts(VBox discountsBox) {
        for(Map.Entry<Integer,  Map<Integer, Double>> storeAndProduct : storeProductsToOrder.entrySet()){
            Store store = engine.getStores().get(storeAndProduct.getKey());
            store.getDiscounts().forEach(discount -> {
                if(storeAndProduct.getValue().containsKey(discount.getItemId())){
                    double amountBought = storeAndProduct.getValue().get(discount.getItemId());
                    if (amountBought >= discount.getQuantity()) {
                        while (amountBought >= discount.getQuantity()) {
                            discounts.add(discount);
                            amountBought -= discount.getQuantity();
                        }
                    }
                }
            });
        }
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

    public static class TableItem {
        private final SimpleStringProperty serial;
        private final SimpleStringProperty name;
        private final SimpleStringProperty price;
        private final SimpleStringProperty method;
        private final SimpleDoubleProperty amount;


        protected TableItem(String serial, String name, String method, String price, Double amount) {
            this.serial = new SimpleStringProperty(serial);
            this.name = new SimpleStringProperty(name);
            this.method = new SimpleStringProperty(method);
            this.price = new SimpleStringProperty(price);
            this.amount = new SimpleDoubleProperty(amount);
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

        public String getSerial() {
            return serial.getValue();
        }

        public Double getAmount() {
            return amount.get();
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

        public void setSerial(String serial) {
            this.serial.set(serial);
        }

        public void setAmount(Float amount) {
            this.amount.set(amount);
        }

    }
}
