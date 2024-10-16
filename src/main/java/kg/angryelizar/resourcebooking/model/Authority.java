package kg.angryelizar.resourcebooking.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "authorities")
public class Authority extends BaseEntity {
    private String authority;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "authority")
    @ToString.Exclude
    private List<User> users;
}
