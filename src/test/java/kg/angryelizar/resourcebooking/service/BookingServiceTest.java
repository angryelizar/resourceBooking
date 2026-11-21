package kg.angryelizar.resourcebooking.service;

import kg.angryelizar.resourcebooking.dto.BookingCreateDTO;
import kg.angryelizar.resourcebooking.dto.BookingSavedDTO;
import kg.angryelizar.resourcebooking.exceptions.ResourceException;
import kg.angryelizar.resourcebooking.model.Booking;
import kg.angryelizar.resourcebooking.model.Resource;
import kg.angryelizar.resourcebooking.repository.BookingRepository;
import kg.angryelizar.resourcebooking.repository.ResourceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static kg.angryelizar.resourcebooking.support.SeedData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ResourceRepository resourceRepository;

    @Test
    void amountIsHoursTimesHourlyRate() {
        BigDecimal amount = bookingService.getAmountForBooking(
                future(5, 10), future(5, 10).plusMinutes(150), BigDecimal.valueOf(100));

        assertThat(amount).isEqualByComparingTo("250");
    }

    @Test
    void userBooksActiveResourceAndGetsAmountToPay() {
        Resource resource = resource(resourceRepository, ACTIVE_RESOURCE);

        BookingSavedDTO saved = bookingService.create(resource.getId(),
                new BookingCreateDTO(future(10, 10), future(10, 12)), auth(USER_EMAIL));

        assertThat(saved.bookingId()).isNotNull();
        assertThat(saved.amountForPay()).isEqualTo(200.0);
        assertThat(saved.isConfirmed()).isFalse();
    }

    @Test
    void inactiveResourceCannotBeBooked() {
        Resource resource = resource(resourceRepository, INACTIVE_RESOURCE);

        assertThatThrownBy(() -> bookingService.create(resource.getId(),
                new BookingCreateDTO(future(10, 10), future(10, 12)), auth(USER_EMAIL)))
                .isInstanceOf(ResourceException.class);
    }

    @Test
    void overlappingConfirmedBookingIsRejected() {
        Resource resource = resource(resourceRepository, ACTIVE_RESOURCE);
        confirmedBooking(resource, future(20, 10), future(20, 14));

        assertThatThrownBy(() -> bookingService.create(resource.getId(),
                new BookingCreateDTO(future(20, 12), future(20, 16)), auth(USER_EMAIL)))
                .isInstanceOf(ResourceException.class);
    }

    @Test
    void bookingRightAfterConfirmedOneIsAllowed() {
        Resource resource = resource(resourceRepository, ACTIVE_RESOURCE);
        confirmedBooking(resource, future(30, 10), future(30, 12));

        BookingSavedDTO saved = bookingService.create(resource.getId(),
                new BookingCreateDTO(future(30, 12), future(30, 14)), auth(USER_EMAIL));

        assertThat(saved.bookingId()).isNotNull();
    }

    @Test
    void bookingTooFarAheadIsRejected() {
        Resource resource = resource(resourceRepository, ACTIVE_RESOURCE);

        assertThatThrownBy(() -> bookingService.create(resource.getId(),
                new BookingCreateDTO(future(400, 10), future(400, 12)), auth(USER_EMAIL)))
                .isInstanceOf(ResourceException.class);
    }

    private void confirmedBooking(Resource resource, LocalDateTime start, LocalDateTime end) {
        BookingSavedDTO saved = bookingService.create(resource.getId(),
                new BookingCreateDTO(start, end), auth(USER_EMAIL));
        Booking booking = bookingRepository.findById(saved.bookingId()).orElseThrow();
        booking.setIsConfirmed(true);
        bookingRepository.saveAndFlush(booking);
    }
}
