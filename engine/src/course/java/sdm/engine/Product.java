package course.java.sdm.engine;

public class Product {
    private int serialNumber;
    private String name;
    private SellingMethod method;

public Product(int num, String j, SellingMethod e)
{
    serialNumber = num;
    name = j;
    method  = e;
}

    public enum SellingMethod {
        WEIGHT,
        AMOUNT
    }

}
