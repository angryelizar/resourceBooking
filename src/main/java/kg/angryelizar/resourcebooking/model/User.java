package kg.angryelizar.resourcebooking.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(authority.getAuthority()));
    }

    @Override
    public String getUsername() {
        return email;
    }
}
