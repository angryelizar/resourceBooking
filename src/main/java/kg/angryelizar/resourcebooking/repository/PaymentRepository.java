package kg.angryelizar.resourcebooking.repository;

import kg.angryelizar.resourcebooking.model.Booking;
import kg.angryelizar.resourcebooking.model.Payment;
import kg.angryelizar.resourcebooking.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByBooking(Booking booking);
    Page<Payment> findAllByBooking_Author(User user, Pageable pageable);
}
