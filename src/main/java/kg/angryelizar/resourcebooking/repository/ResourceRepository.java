package kg.angryelizar.resourcebooking.repository;

import kg.angryelizar.resourcebooking.model.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    Page<Resource> findAllByIsActive(Pageable pageable, Boolean isActive);
}
