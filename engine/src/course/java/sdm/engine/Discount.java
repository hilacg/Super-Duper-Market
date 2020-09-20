package course.java.sdm.engine;

import generatedClasses.SDMDiscount;
import generatedClasses.SDMOffer;

import java.util.ArrayList;
import java.util.List;

public class Discount {
    private String name;
    private double quantity;
    private int itemId;
    private Operator operator;
    private List<Offer> offers = new ArrayList<>();

    public Discount(){}
    public Discount(SDMDiscount discount){
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

    public enum Operator {
        IRRELEVANT,
        ONE_OF,
        ALL_OR_NOTHING
 //       public abstract float validateAmount(String amount) throws Exception;
    }

}
