package course.java.sdm.engine;
import java.util.*;

public class Engine {
    private Set<Product> allProducts;
    private Set<Store> allStores;

    public void loadXML() {
        allProducts = new HashSet<>(2);
        allProducts.add(new Product(12,"Ketshup", Product.SellingMethod.AMOUNT));
        allProducts.add(new Product(1,"candy", Product.SellingMethod.WEIGHT));

        allStores = new HashSet<>(2);
        allStores.add(new Store(4,"Suprt",allProducts,3,20));

    }

    public void executeCommand() {
    }
}
