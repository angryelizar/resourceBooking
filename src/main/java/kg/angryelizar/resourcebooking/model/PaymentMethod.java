package kg.angryelizar.resourcebooking.model;

import jakarta.persistence.*;
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
@Table(name = "payment_methods")
public class PaymentMethod extends BaseEntity {
    private String title;
    private String description;
    @Column(name = "credentials_example")
    private String credentialsExample;
    private String code;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "paymentMethod")
    private List<Payment> payments;
}
