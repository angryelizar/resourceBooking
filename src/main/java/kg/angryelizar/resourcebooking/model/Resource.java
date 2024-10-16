package kg.angryelizar.resourcebooking.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Builder(builderMethodName = "resourceBuilder")
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

    public static class ResourceBuilder {
        public Resource.ResourceBuilder updatedBy(User updatedBy) {
            return this;
        }
    }
}
