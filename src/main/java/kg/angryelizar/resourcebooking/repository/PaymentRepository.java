package kg.angryelizar.resourcebooking.repository;

import kg.angryelizar.resourcebooking.model.Booking;
import kg.angryelizar.resourcebooking.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByBooking(Booking booking);
}
