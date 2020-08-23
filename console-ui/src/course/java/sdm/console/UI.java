package course.java.sdm.console;

import course.java.sdm.engine.Engine;
import course.java.sdm.engine.Product;
import course.java.sdm.engine.Store;
import course.java.sdm.engine.SuperXML;

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

            Date deliveryDate;
            Store chosenStore = null;
            Point customerLocation;
            Map<Integer, Integer> productsToOrder = new HashMap<>();
            if(engine.getisXMLLoaded()) {
                try {
                    chosenStore = getDeliveryStore();
                    deliveryDate = getDeliveryDate();
                    customerLocation = getCustomerLocation();
                    System.out.println("Please choose products and an amount to order. enter 'q' to finish.\n");
                    productsToOrder = getOrderProducts(chosenStore);

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

        private Map<Integer, Integer> getOrderProducts(Store chosenStore) {
            String chosenSerial = " ";
            int serialNumber;
            for(Product product : engine.getProducts().values()){
                Integer price = chosenStore.getProductPrices().get(product);
                System.out.println(String.format("Serial number:%d\n Name:%s\n Selling method:%s\n Price:%d",
                        product.getSerialNumber(),product.getName(),product.getMethod(),price));
            }
            while(chosenSerial.equals('q')){
                chosenSerial = scanner.nextLine();
                if(!chosenSerial.equals('q')) {
                    serialNumber = Integer.parseInt(chosenSerial);
                    if(checkIfStoreSellsProduct(chosenStore,serialNumber)){
                        System.out.println(("Enter " + engine.getProducts().get(serialNumber).getMethod()) + ":");

                    }

                }

            }
return new HashMap<>();
        }

        private boolean checkIfStoreSellsProduct(Store chosenStore, int serialNumber) {
            return true;
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
                 if(SuperXML.checkLocation(x,y))
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

        private Store getDeliveryStore() throws Exception {
            int input;
            Store chosenStore = null ;
            try{
                Printer.printStoresList(engine);
                input = Integer.parseInt(scanner.nextLine());
                chosenStore = engine.getStores().get(input);
                if (chosenStore == null) {
                    throw new Exception("Store doesn't exist\n");
                }
                return  chosenStore;
            } catch (Exception e) {
                throw e;
            }
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

        private boolean validateInput(int input) {
            return true;
        }
    }
