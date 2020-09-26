package course.java.sdm.engine;

import generatedClasses.SDMOffer;

import javax.xml.bind.annotation.XmlAttribute;

public class Offer {

    protected double quantity;
    protected Integer itemId;
    protected Integer forAdditional;

    public Offer(){}
    public Offer(SDMOffer offer) {
        this.quantity = offer.getQuantity();
        this.itemId = offer.getItemId();
        this.forAdditional = offer.getForAdditional();
    }

    public Integer getItemId() {
        return itemId;
    }

    public double getQuantity() {
        return quantity;
    }

    public int getForAdditional() {
        return forAdditional;
    }
}
