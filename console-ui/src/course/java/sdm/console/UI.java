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

        public void start() {
            boolean exit = false;
            int input = 0;

            while(input != 6) {
                try {
                    Printer.printMenu();
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
                if(input<0 || input > 6)
                    throw new Exception("Invalid input, Please enter a number between 1 to 6\n");
                return input;
            }catch(NumberFormatException exception) {
                throw new Exception("Invalid input, Please enter a number\n");
            }
        }

        private void executeCommand(int input) throws Exception {
            try {
            switch (input){
                    case 1:
                        this.loadXml();
                        break;
                    case 2:
                        Printer.printStoreDetails(engine);
                        break;
                    case 3:
                        Printer.printProductDetails(engine);
                        break;
                    case 4:
                        newOrder();
                        break;
                    case 5:
                        printOrderHistory();
                        break;
                    case 6:
                        Printer.goodbye();
                        break;
            }
            }catch(Exception e){
                throw  e;
            }
        }

        private void printOrderHistory() {
        }

        private void newOrder() throws Exception {
            Order newOrder;
            Date deliveryDate;
            Store chosenStore = null;
            Point customerLocation;
            Map<Integer, Float> productsToOrder = new HashMap<>();

            if(engine.getisXMLLoaded()) {
                try {
                    chosenStore = getDeliveryStore();
                    deliveryDate = getDeliveryDate();
                    customerLocation = getCustomerLocation();
                    productsToOrder = getOrderProducts(chosenStore);
 //                   newOrder = new Order(deliveryDate, productsToOrder);


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

        private Store getDeliveryStore() throws Exception {
            int input;
            Store chosenStore = null ;

            System.out.println("Please choose desired store serial number:\n ");
            Printer.printStoresList(engine);
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
                System.out.println("Please enter delivery date (in dd/mm-hh:mm format)\n");
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
                System.out.println("Please enter your location:\nx:");
                x = Integer.parseInt(scanner.nextLine());
                System.out.println("y:");
                y  = Integer.parseInt(scanner.nextLine());
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
            Integer price = 0;
            int chosenSerial = 0;
            Product chosenProduct;
            float amount = 0;
            Map<Integer, Float> orderProducts = new HashMap<>();

            System.out.println("Please choose products and an amount to order. enter 'q' to finish.\n");

            for (Product product : engine.getProducts().values()) {
                price = chosenStore.getProductPrices().get(product.getSerialNumber());
                System.out.printf("Serial number:%d\n Name:%s\n Selling method:%s\n Price:%d%n",
                        product.getSerialNumber(), product.getName(), product.getMethod(), price);
            }

            System.out.println("\nProduct serial number:\n");
            while (scanner.hasNext()) {
                try {
                    if (scanner.hasNextInt()) {
                        chosenSerial = Integer.parseInt(scanner.nextLine());
                        chosenProduct = getChosenProduct(chosenStore, chosenSerial);
                        amount = getAmountToBuy(chosenProduct);
                        orderProducts.put(chosenProduct.getSerialNumber(), orderProducts.getOrDefault(chosenProduct.getSerialNumber(), 0f) + amount);
                    } else {
                        String input = scanner.nextLine();
                        if (input.equalsIgnoreCase("Q")) {
                            System.out.println("Exiting");
                            break;
                        } else {
                            System.out.println("You did not enter a valid value. Please enter a number or \"q\" to quit.");
                        }
                    }
                }catch (Exception exception) {
                    System.out.println(exception.getMessage());
                }
                System.out.println("Product serial number:\n");
            }
            return orderProducts;
        }

        private Product getChosenProduct(Store chosenStore, int chosenSerial) throws Exception {
            if (chosenStore.getProductPrices().containsKey(chosenSerial))
                return engine.getProducts().get(chosenSerial);
            else
                throw new Exception("the chosen store doesn't sell this product\n");

        }

        private float getAmountToBuy(Product chosenProduct) throws Exception {
            String amount;
            float validAmount;
            Product.SellingMethod method;
            method = chosenProduct.getMethod();
            System.out.println("Enter " + method + ": ");
            amount = scanner.nextLine();
            return chosenProduct.getMethod().validateAmount(amount);
        }

        private void loadXml() {
            System.out.println("please enter full path name of an XML file\n");
            try {
                engine.loadXML(scanner.nextLine());
            }catch (FileNotFoundException e){
                System.out.println("file does not exists\n");
            }
            catch (Exception e){
                System.out.println(e.getMessage() + "\n");
            }

        }

    }
