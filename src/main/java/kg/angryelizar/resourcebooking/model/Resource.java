package kg.angryelizar.resourcebooking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "resources")
public class Resource extends BaseEntityAudit {
    private String title;
    private String description;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "hourly_rate")
    private BigDecimal hourlyRate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "resource")
    private List<Booking> bookings;
}
