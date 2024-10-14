package kg.angryelizar.resourcebooking.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {
    private String name;
    private String surname;
    private String email;
    private String password;

    @JoinColumn(name = "authority_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Authority authority;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    private List<Booking> bookings;
}
