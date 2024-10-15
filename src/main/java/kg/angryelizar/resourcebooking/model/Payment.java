package kg.angryelizar.resourcebooking.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder(builderMethodName = "paymentBuilder")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment extends BaseEntityAudit {
    @JoinColumn(name = "booking_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Booking booking;
    @JoinColumn(name = "method_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PaymentMethod paymentMethod;
    @JoinColumn(name = "status_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PaymentStatus paymentStatus;
    private String credentials;
    private BigDecimal amount;

    public static class PaymentBuilder {
        private User updatedBy;
        public PaymentBuilder updatedBy(User updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }
    }

}
