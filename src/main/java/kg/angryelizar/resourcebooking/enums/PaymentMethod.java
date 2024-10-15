package kg.angryelizar.resourcebooking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    CARD("CARD"),
    PAYPAL("PAYPAL");

    private final String code;
}
