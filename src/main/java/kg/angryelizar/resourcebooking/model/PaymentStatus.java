package kg.angryelizar.resourcebooking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_statuses")
public class PaymentStatus extends BaseEntity {
    private String status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "paymentStatus")
    private List<Payment> payments;
}
