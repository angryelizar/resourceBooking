package kg.angryelizar.resourcebooking.strategy;

import kg.angryelizar.resourcebooking.model.Payment;

import java.math.BigDecimal;

public interface PaymentStrategy {
    Payment pay(Long bookingId, String method, String credentials, BigDecimal amount);
}
