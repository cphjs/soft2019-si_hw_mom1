package cphb.hw_mom1;

import java.util.UUID;

/**
 * Booking
 */
public class Booking {

    private UUID adId;
    private UUID customerId;

    public Booking(UUID adId, UUID customerId) {
        this.adId = adId;
        this.customerId = customerId;
    }

    public UUID getAdId() {
        return adId;
    }

    public void setAdId(UUID adId) {
        this.adId = adId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

}