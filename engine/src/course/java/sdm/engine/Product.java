package course.java.sdm.engine;

import generatedClasses.SDMItem;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "SDM-item")
public class Product {
    private int serialNumber;
    private String name;
    private SellingMethod method;
    private int storeCount = 0;
    private double avgPrice = 0;
    private double soldAmount = 0;
    public Product(){

    }

    public Product(SDMItem item)
    {
        serialNumber = item.getId();
        name = item.getName();
        method  = item.getPurchaseCategory().equals("Weight") ? SellingMethod.WEIGHT: SellingMethod.QUANTITY;
    }

    public void setSoldAmount(double soldAmount) {
        this.soldAmount += soldAmount;
    }

    public void setAvgPrice(double price) {
        this.avgPrice = price/storeCount;
    }

    public void setStoreCount(int count) {
        this.storeCount = count;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public String getName() {
        return name;
    }

    public SellingMethod getMethod() {
        return method;
    }

    public double getAvgPrice() {
        return avgPrice;
    }

    public double getSoldAmount() {
        return soldAmount;
    }

    public int getStoreCount() {
        return storeCount;
    }

    public enum SellingMethod {
        WEIGHT{public float validateAmount(String amount) throws Exception {
            try {
                return Float.parseFloat(amount);
            } catch (NumberFormatException exception) {
                throw new Exception("Must be a decimal number\n");
            }
        }},

        QUANTITY{public float validateAmount(String amount) throws Exception{
            try {
                Integer.parseInt(amount);
                return Float.parseFloat(amount);
            } catch (NumberFormatException exception) {
                throw new Exception("Must be a round number\n");
            }
        }};

        public abstract float validateAmount(String amount) throws Exception;

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
