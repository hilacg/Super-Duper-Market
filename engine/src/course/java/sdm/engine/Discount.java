package course.java.sdm.engine;

import generatedClasses.SDMDiscount;
import generatedClasses.SDMOffer;

import java.util.ArrayList;
import java.util.List;

public class Discount {
    private  int storeSerial;
    private String name;
    private double quantity;
    private int itemId;
    private Operator operator;
    private List<Offer> offers = new ArrayList<>();

    public Discount(){}
    public Discount(SDMDiscount discount, int serial){
        this.storeSerial = serial;
        this.name = discount.getName();
        this.quantity = discount.getIfYouBuy().getQuantity();
        this.itemId = discount.getIfYouBuy().getItemId();
        String op = discount.getThenYouGet().getOperator();
        operator = op.equals("IRRELEVANT") ? Discount.Operator.IRRELEVANT:  op.equals("ONE-OF") ? Discount.Operator.ONE_OF : Discount.Operator.ALL_OR_NOTHING;
        for(SDMOffer offer: discount.getThenYouGet().getSDMOffer() ){
            offers.add(new Offer(offer));
        }
    }

    public String getName() {
        return name;
    }

    public int getStoreSerial() {
        return storeSerial;
    }

    public double getQuantity() {
        return quantity;
    }

    public int getItemId() {
        return itemId;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public enum Operator {
        IRRELEVANT{public String toString(){return "";}},
        ONE_OF{public String toString(){return "one of";}},
        ALL_OR_NOTHING{public String toString(){return "all or nothing of";}};

        public abstract String toString();
    }

}
