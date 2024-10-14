package kg.angryelizar.resourcebooking.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PaymentStatuses {
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    CANCELLED("CANCELLED");

    private final String value;
}
