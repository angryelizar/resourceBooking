package kg.angryelizar.resourcebooking.strategy;

import java.math.BigDecimal;

public interface PaymentStrategy {
    void pay(Long bookingId, String method, String credentials, BigDecimal amount);
}
