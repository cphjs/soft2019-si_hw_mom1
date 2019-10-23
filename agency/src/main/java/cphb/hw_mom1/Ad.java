package cphb.hw_mom1;

import java.time.Instant;
import java.util.UUID;

/**
 * Ad
 */
public class Ad {

    private UUID uuid;
    private String campaign;
    private double price;
    private Instant endDate;

    public Ad(String campaign, double price, Instant endDate) {
        this.uuid = UUID.randomUUID();
        this.campaign = campaign;
        this.price = price;
        this.endDate = endDate;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
    
}