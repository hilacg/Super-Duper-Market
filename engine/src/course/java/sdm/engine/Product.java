package course.java.sdm.engine;

import generatedClasses.SDMItem;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "SDM-item")
public class Product {
    private int serialNumber;
    private String name;
    private SellingMethod method;

    public Product(){

    }

    public Product(SDMItem item)
    {
        serialNumber = item.getId();
        name = item.getName();
        method  = item.getPurchaseCategory().equals("Weight") ? SellingMethod.WEIGHT : SellingMethod.Quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setMethod(SellingMethod method) {
        this.method = method;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public String getName() {
        return name;
    }

    public enum SellingMethod {
        WEIGHT("Weight", 1),
        Quantity("Quantity", 2);

        private String name;
        private int option;

        SellingMethod(String name, int option){
            name = name;
            option = option;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return serialNumber == product.serialNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNumber);
    }

    @Override
    public String toString() {
        return "serialNumber: " + serialNumber +
                "\nname: " + name  +
                "\nmethod: " + method ;
    }
}
