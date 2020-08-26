package course.java.sdm.console;

import course.java.sdm.engine.*;

import java.awt.*;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

    public class UI {
        Scanner scanner = new Scanner(System.in);
        private final Engine engine = new Engine();
        private final Printer printer = new Printer(engine);

        public void start() {
            boolean exit = false;
            int input = 0;

            while(input != 6) {
                try {
                    printer.printMenu();
                    input = getInput();
                    executeCommand(input);
                }catch(Exception e){
                    System.out.println((e.getMessage()));
                }
           }
        }

        private int getInput() throws Exception {
            int input = 0;
            String str;
            try {
                input = Integer.parseInt(scanner.nextLine());
                return input;
            }catch(NumberFormatException exception) {
                throw new Exception("Invalid input, Please enter a number\n");
            }
        }

        private void executeCommand(int input) throws Exception {
            switch (input){
                    case 1:
                        this.loadXml();
                        break;
                    case 2:
                        printer.printStoreDetails();
                        break;
                    case 3:
                        printer.printProductDetails();
                        break;
                    case 4:
                        newOrder();
                        break;
                    case 5:
                        printOrderHistory();
                        break;
                    case 6:
                        updateProducts();
                        break;
                    case 7:
                        printer.goodbye();
                        break;
                    default:
                        throw new Exception("Invalid input, Please enter a number between 1 to 7\n");
            }
        }

        private void updateProducts() throws Exception {
            int input;
            Store chosenStore;
            try {
                if (engine.getisXMLLoaded()) {
                    System.out.println("Please choose a store serial number to update its' products");
                    printer.printStoreDetails();
                    chosenStore = getChosenStore();
                    printer.printProductUpdateChoices();
                    input = getInput();
                    executeUpdate(input,chosenStore);
                } else {
                    System.out.println("There is no XML loaded!\n");
                }
            }catch(NumberFormatException e) {
                throw new Exception("Invalid input, Please enter a number\n");
            }
        }

        private void executeUpdate(int input, Store chosenStore) throws Exception {
            switch (input) {
                case 1:
                    deleteProduct(chosenStore);
                    break;
                case 2:
                    addProduct(chosenStore);
                    break;
                case 3:
    //                updateProduct(chosenStore);
                    break;
                default:
                    throw new Exception("Invalid input, Please enter a number between 1 to 3\n");
            }
        }

        private void addProduct(Store chosenStore) throws Exception {
            int input;
            if (engine.getisXMLLoaded()) {
                System.out.println("Choose product serial number to add:");
                printer.printProductDetails();
                input = getInput();
            }
            else{
                System.out.println("There is no XML loaded!\n");
            }

        }

        private void deleteProduct(Store chosenStore) throws Exception {
            int chosenSerial;
            Product chosenProduct;
            if (engine.getisXMLLoaded()) {

                try {
                    System.out.println("Choose product serial number to remove:");
                    printer.printProducts(chosenStore);
                    chosenSerial = Integer.parseInt((scanner.nextLine()).replaceAll("\\s+", ""));
                    chosenProduct = engine.getChosenProduct(chosenStore, chosenSerial);
                    if (engine.productSoldInOtherStore(chosenProduct, chosenStore.getSerialNumber())) {
                        chosenStore.deleteProduct(chosenProduct.getSerialNumber());
                    } else
                        throw new Exception("Can't delete product, The chosen product is sold in the chosen store only.\n");
                } catch (NumberFormatException e) {
                    throw new Exception("Invalid input, Please enter a number\n");
                }
            }
            else{
                System.out.println("There is no XML loaded!\n");
            }

        }

        private void printOrderHistory() {
            if(engine.getisXMLLoaded()) {
                for (Order order : engine.getOrders()) {
                    printer.printOrderDetails(order);
                }
            }
            else{
                System.out.println("There is no XML loaded!\n");
            }
        }

        private void newOrder() throws Exception {
            Date deliveryDate;
            Store chosenStore = null;
            Point customerLocation;
            String confirm;
            Map<Integer, Float> productsToOrder = new HashMap<>();

            if(engine.getisXMLLoaded()) {
                try {
                    System.out.println("Please choose desired store serial number:\n ");
                    printer.printStoresList();
                    chosenStore = getChosenStore();
                    deliveryDate = getDeliveryDate();
                    customerLocation = getCustomerLocation();
                    productsToOrder = getOrderProducts(chosenStore);
                    Order newOrder = engine.setNewOrder(chosenStore,deliveryDate, productsToOrder, customerLocation);
                    printer.printNewOrder(newOrder);
                    System.out.println("Do you wish to confirm? (Y/N)");
                    while (scanner.hasNext()) {
                        confirm = scanner.nextLine();
                        if(confirm.equalsIgnoreCase("Y")) {
                            engine.addOrder(newOrder);
                            System.out.println("Order completed!");
                            break;
                        }
                        else if(confirm.equalsIgnoreCase("N")) {
                            System.out.println("Order is cancelled");
                            break;
                        }
                        else
                            System.out.println("Invalid input! please enter (Y/N)");

                    }


                } catch (NumberFormatException e) {
                    throw new Exception("Invalid input, Please enter a number\n");
                }catch(Exception e){
                    throw e;
                }

            }
            else{
                System.out.println("There is no XML loaded!\n");
            }
        }

        private Store getChosenStore() throws Exception {
            int input;
            Store chosenStore = null ;

            input = Integer.parseInt(scanner.nextLine());
            chosenStore = engine.getStores().get(input);
            if (chosenStore == null) {
                throw new Exception("Store doesn't exist\n");
            }
            return  chosenStore;
        }

        private Date getDeliveryDate() throws Exception {
            String dateInput;
            Date today = new Date();
            SimpleDateFormat date = new SimpleDateFormat () ;
            date.applyPattern("dd/MM-HH:mm");
            date.setLenient(false);

            try{
                System.out.println("Please enter delivery date (in dd/mm-hh:mm format)");
                dateInput = scanner.nextLine();
                Date deliveryDate = date.parse(dateInput);
                deliveryDate.setYear(today.getYear());
                if(deliveryDate.before(today)){
                    throw new Exception("Delivery date cannot be in the past\n");
                }
                return deliveryDate;
            } catch (ParseException e) {
                throw new Exception("Wrong date format entered\n");
            }
        }

        private Point getCustomerLocation() throws Exception {
            int x;
            int y;
            Point location;

            try{
                System.out.print("Please enter your location:\nx:");
                x = Integer.parseInt(scanner.nextLine());
                System.out.print("y:");
                y  = Integer.parseInt(scanner.nextLine());
                System.out.println("");
                if(SuperXML.checkLocationRange(x,y))
                    throw new Exception("Location is out of range!\n");
                location = new Point(x,y);
                for(Store store : engine.getStores().values())
                {
                    if(location.equals(store.getLocation()))
                        throw new Exception("You are in a Store!\n");
                }
                return location;
            } catch (Exception e) {
                throw e;
            }
        }

        private Map<Integer, Float> getOrderProducts(Store chosenStore) {
            int chosenSerial = 0;
            Product chosenProduct;
            float amount = 0;
            Map<Integer, Float> orderProducts = new HashMap<>();

            System.out.println("Please choose products and an amount to order. enter 'q' to finish.\n");
            printer.printProducts(chosenStore);
            System.out.println("Product serial number:");
            while (scanner.hasNextLine()) {
                try {
                    if (scanner.hasNextInt()) {
                        chosenSerial = Integer.parseInt((scanner.nextLine()).replaceAll("\\s+",""));
                        chosenProduct = engine.getChosenProduct(chosenStore, chosenSerial);
                        amount = getAmountToBuy(chosenProduct);
                        orderProducts.put(chosenProduct.getSerialNumber(), orderProducts.getOrDefault(chosenProduct.getSerialNumber(), 0f) + amount);
                    } else {
                        String input = scanner.nextLine();
                        if (input.equalsIgnoreCase("Q")) {
                            System.out.println("Order finished\n");
                            break;
                        } else {
                            System.out.println("You did not enter a valid value. Please enter a number or \"q\" to quit.");
                        }
                    }
                }catch (Exception exception) {
                    System.out.println(exception.getMessage());
                }
                System.out.println("Product serial number:");
            }
            return orderProducts;
        }
        private float getAmountToBuy(Product chosenProduct) throws Exception {
            String amount;
            Product.SellingMethod method;
            method = chosenProduct.getMethod();
            System.out.println("Enter " + method + ": ");
            amount = scanner.nextLine();
            return chosenProduct.getMethod().validateAmount(amount);
        }

        private void loadXml() {
            System.out.println("please enter full path name of an XML file");
            try {
                engine.loadXML(scanner.nextLine());
                System.out.println("XML file loaded successfully!");
            }catch (FileNotFoundException e){
                System.out.println("file does not exists");
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }

        }

    }
