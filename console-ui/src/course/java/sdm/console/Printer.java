package course.java.sdm.console;

public class Printer {
    public static void printMenu()
    {
        System.out.println("Welcome!\n" +
                "please choose your desired action:\n" +
                "1. Load File\n" +
                "2. Exhibit Stores Details\n" +
                "3. Exhibit Products Details\n" +
                "4. Make A New Order\n" +
                "5. Exhibit Orders History\n" +
                "6. Exit\n");
    }

    public static void goodbye() {
        System.out.println("Goodbye!");
    }
}
