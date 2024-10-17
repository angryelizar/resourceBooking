package kg.angryelizar.resourcebooking.service.impl;

import kg.angryelizar.resourcebooking.dto.BookingCreateDTO;
import kg.angryelizar.resourcebooking.dto.BookingReadDTO;
import kg.angryelizar.resourcebooking.model.Booking;
import kg.angryelizar.resourcebooking.model.Resource;
import kg.angryelizar.resourcebooking.model.User;
import kg.angryelizar.resourcebooking.repository.BookingRepository;
import kg.angryelizar.resourcebooking.repository.PaymentRepository;
import kg.angryelizar.resourcebooking.repository.UserRepository;
import kg.angryelizar.resourcebooking.service.BookingService;
import kg.angryelizar.resourcebooking.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final static String NAME_PATTERN = "%s %s";

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

    @Override
    public ResponseEntity<BookingReadDTO> create(Long resourceId, BookingCreateDTO bookingCreateDTO, Authentication authentication) {
        // Проверка ресурса - есть ли такой вообще, доступен ли он для бронирования
        // Проверка дат - не одинаковые ли
        // Проверка доступности брони по времени
        return null;
    }

    @Override
    public List<BookingReadDTO> findAll(Integer page, Integer size, Boolean isConfirmed) {
        return bookingRepository.findByIsConfirmed(isConfirmed, PageRequest.of(page, size)).getContent().stream().map(this::toDTO).toList();
    }

    private BookingReadDTO toDTO(Booking booking) {
        User author = booking.getAuthor();
        User updatedBy = booking.getUpdatedBy();
        String authorName = String.format(NAME_PATTERN, author.getName(), author.getSurname());
        String updatedAuthorName = String.format(NAME_PATTERN, updatedBy.getName(), updatedBy.getSurname());
        return new BookingReadDTO(booking.getResource().getTitle(), booking.getResource().getId(), booking.getId(),
                booking.getStartDate(), booking.getEndDate(), booking.getIsConfirmed(), authorName, updatedAuthorName,
                booking.getCreatedAt(), booking.getUpdatedAt());
    }
}
