package kg.angryelizar.resourcebooking.service;

import kg.angryelizar.resourcebooking.dto.BookingCreateDTO;
import kg.angryelizar.resourcebooking.dto.BookingSavedDTO;
import kg.angryelizar.resourcebooking.dto.PaymentBookingDTO;
import kg.angryelizar.resourcebooking.dto.PaymentReadDTO;
import kg.angryelizar.resourcebooking.dto.UserDTO;
import kg.angryelizar.resourcebooking.exceptions.PaymentException;
import kg.angryelizar.resourcebooking.repository.BookingRepository;
import kg.angryelizar.resourcebooking.repository.ResourceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static kg.angryelizar.resourcebooking.support.SeedData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ResourceRepository resourceRepository;

    @Test
    void payingOwnBookingByCardConfirmsIt() {
        BookingSavedDTO booking = bookTwoHours(USER_EMAIL);

        PaymentReadDTO payment = paymentService.makePayment(booking.bookingId(),
                new PaymentBookingDTO("CARD", "4169963312341234"), auth(USER_EMAIL));

        assertThat(payment.amount()).isEqualTo(200.0);
        assertThat(payment.status()).isEqualTo("CONFIRMED");
        assertThat(bookingRepository.findById(booking.bookingId()).orElseThrow().getIsConfirmed()).isTrue();
    }

    @Test
    void cannotPaySomeoneElsesBooking() {
        BookingSavedDTO booking = bookTwoHours(USER_EMAIL);
        userService.create(new UserDTO("Айбек", "Тестов", "other@example.com", "qwerty123"));

        assertThatThrownBy(() -> paymentService.makePayment(booking.bookingId(),
                new PaymentBookingDTO("CARD", "4169963312341234"), auth("other@example.com")))
                .isInstanceOf(PaymentException.class);
        assertThat(bookingRepository.findById(booking.bookingId()).orElseThrow().getIsConfirmed()).isFalse();
    }

    private BookingSavedDTO bookTwoHours(String email) {
        Long resourceId = resource(resourceRepository, ACTIVE_RESOURCE).getId();
        return bookingService.create(resourceId,
                new BookingCreateDTO(future(15, 10), future(15, 12)), auth(email));
    }
}
