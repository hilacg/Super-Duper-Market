package course.java.sdm.engine;

import generatedClasses.SDMOffer;

import javax.xml.bind.annotation.XmlAttribute;

public class Offer {

    protected double quantity;
    protected int itemId;
    protected int forAdditional;

    public Offer(){}
    public Offer(SDMOffer offer) {
        this.quantity = offer.getQuantity();
        this.itemId = offer.getItemId();
        this.forAdditional = offer.getForAdditional();
    }

    public int getItemId() {
        return itemId;
    }

}
