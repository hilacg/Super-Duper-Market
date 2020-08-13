package course.java.sdm.console;

import course.java.sdm.engine.Engine;

import java.util.Scanner;

    public class UI {
        private Engine engine = new Engine();

        public void start() {
            boolean exit = false;
            int input = 0;
            Scanner scanner = new Scanner(System.in);

            while(input != 6) {
                Printer.printMenu();
                input = scanner.nextInt();
                if(validateInput(input)){
                    executeCommand(input);
                }
           }
        }

        private void executeCommand(int input) {
            switch (input){
                case 1:
                    break;
                case 2:
                    break;
                case 3:
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
        private boolean validateInput(int input) {
            return true;
        }
    }
