package kg.angryelizar.resourcebooking.repository;

import kg.angryelizar.resourcebooking.model.Booking;
import kg.angryelizar.resourcebooking.model.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByResource(Resource resource);
    Page<Booking> findByIsConfirmed(Boolean isConfirmed, Pageable pageable);
    @Query("SELECT b FROM Booking b WHERE b.isConfirmed = :isConfirmed AND b.resource = :resource AND (b.startDate BETWEEN :startDate AND :endDate OR b.endDate BETWEEN :startDate AND :endDate OR :startDate BETWEEN b.startDate AND b.endDate)")
    List<Booking> findByIsConfirmedAndResource(Boolean isConfirmed, Resource resource, LocalDateTime startDate, LocalDateTime endDate);

}
