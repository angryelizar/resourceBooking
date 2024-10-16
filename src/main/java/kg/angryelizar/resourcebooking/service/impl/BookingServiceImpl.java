package kg.angryelizar.resourcebooking.service.impl;

import kg.angryelizar.resourcebooking.model.Booking;
import kg.angryelizar.resourcebooking.model.Resource;
import kg.angryelizar.resourcebooking.repository.BookingRepository;
import kg.angryelizar.resourcebooking.repository.PaymentRepository;
import kg.angryelizar.resourcebooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public void deleteBookingsByResource(Resource resource) {
        List<Booking> bookings = bookingRepository.findByResource(resource);
        for (Booking booking : bookings) {
            paymentRepository.deleteAll(paymentRepository.findAllByBooking(booking));
            log.info("Удалены все платежи для брони c ID {}", booking.getId());
        }
        bookingRepository.deleteAll(bookings);
        log.info("Удалены все бронирования для ресурса {} (ID - {})", resource.getTitle(), resource.getId());
    }
}
