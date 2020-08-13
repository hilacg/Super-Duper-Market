package course.java.sdm.engine;
import java.util.*;

public class Store {
    private int serialNumber;
    private String name;
    private Map<Product,Float> productPrices;
    private Map<Product,Float> ProductsSold;
    private List<Order> orders;
    private float PPK;
    private float deliveryEarnings;

    public Store(int n,String g,Set<Product> allProducts,float ppk,float d){
        serialNumber = n;
        name = g;
        productPrices = new HashMap<>(2);
        ProductsSold = new HashMap<>(2);
        for(Product pruduct : allProducts)
        {
            productPrices.put(pruduct,3.0f);
            ProductsSold.put(pruduct,8.0f);
        }
        PPK = ppk;
        deliveryEarnings = d;
        orders = new ArrayList<>();
    }
}
