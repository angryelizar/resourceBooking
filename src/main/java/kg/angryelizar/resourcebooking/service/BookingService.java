package kg.angryelizar.resourcebooking.service;

import kg.angryelizar.resourcebooking.dto.*;
import kg.angryelizar.resourcebooking.model.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface BookingService {
    void deleteBookingsByResource(Resource resource);

    BookingSavedDTO create(Long resourceId, BookingCreateDTO bookingCreateDTO, Authentication authentication);

    BigDecimal getAmountForBooking(LocalDateTime startDate, LocalDateTime endDate, BigDecimal hourlyRate);

    List<BookingReadDTO> findAll(Integer page, Integer size, Boolean isConfirmed);

    List<BookingProfileReadDTO> findAllForUser(Authentication authentication, Integer page, Integer size);

    Boolean isAvailableForBooking(Long resourceId);
    Boolean isBookingPossible(LocalDateTime start, LocalDateTime end, Resource resource);

    HttpStatus delete(Long bookingId, Authentication authentication);

    BookingReadDTO edit(BookingUpdateDTO bookingUpdateDTO, Authentication authentication, Long bookingId);
}
