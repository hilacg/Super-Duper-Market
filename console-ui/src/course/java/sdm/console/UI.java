package course.java.sdm.console;

import course.java.sdm.engine.Engine;
import course.java.sdm.engine.Product;
import course.java.sdm.engine.Store;

import java.io.FileNotFoundException;
import java.util.Scanner;

    public class UI {
        Scanner scanner = new Scanner(System.in);
        private Engine engine = new Engine();

        public void start() {
            boolean exit = false;
            int input = 0;


            while(input != 6) {
                Printer.printMenu();
                input = Integer.parseInt(scanner.nextLine());
                if(validateInput(input)){
                    executeCommand(input);
                }
           }
        }

        private void executeCommand(int input) {
            switch (input){
                case 1:
                    this.loadXml();
                    break;
                case 2:
                    for(Store store : engine.getStores().values()){
                        System.out.println(store.toString() + '\n');
                    }
                    break;
                case 3:
                    for(Product product : engine.getProducts().values()){
                        System.out.println(product.toString() + '\n');
                    }
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    Printer.goodbye();
                    break;
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
