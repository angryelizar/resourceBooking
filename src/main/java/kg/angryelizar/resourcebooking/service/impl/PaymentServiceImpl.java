package kg.angryelizar.resourcebooking.service.impl;

import kg.angryelizar.resourcebooking.dto.PaymentBookingDTO;
import kg.angryelizar.resourcebooking.dto.PaymentProfileReadDTO;
import kg.angryelizar.resourcebooking.dto.PaymentReadDTO;
import kg.angryelizar.resourcebooking.exceptions.BookingException;
import kg.angryelizar.resourcebooking.exceptions.PaymentException;
import kg.angryelizar.resourcebooking.exceptions.ResourceException;
import kg.angryelizar.resourcebooking.exceptions.UserException;
import kg.angryelizar.resourcebooking.model.Booking;
import kg.angryelizar.resourcebooking.model.Payment;
import kg.angryelizar.resourcebooking.model.Resource;
import kg.angryelizar.resourcebooking.model.User;
import kg.angryelizar.resourcebooking.repository.BookingRepository;
import kg.angryelizar.resourcebooking.repository.PaymentRepository;
import kg.angryelizar.resourcebooking.repository.UserRepository;
import kg.angryelizar.resourcebooking.service.BookingService;
import kg.angryelizar.resourcebooking.service.PaymentService;
import kg.angryelizar.resourcebooking.strategy.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final BookingService bookingService;
    private final Map<String, PaymentStrategy> paymentStrategies;
    private static final String NAME_PATTERN = "%s %s";
    private final BookingRepository bookingRepository;

    public Payment executePayment(Long bookingId, String method, BigDecimal amount, String credentials) {

        PaymentStrategy paymentStrategy = paymentStrategies.get(method.toLowerCase());
        log.info("Payment strategy: {}", paymentStrategy);
        if (paymentStrategy != null) {
            return paymentStrategy.pay(bookingId, method.toLowerCase(), credentials, amount);
        }
        log.error("Не найдена реализация для этого метода оплаты - {}", method);
        throw new PaymentException("Неизвестный метод оплаты - " + method);
    }

    @Override
    public List<PaymentProfileReadDTO> findAllForUser(Authentication authentication, Integer page, Integer size) {
        User author = userRepository.getByEmail(authentication.getName()).orElseThrow(
                () -> new UserException("Пользователь не найден!"));
        return paymentRepository.findAllByBooking_Author(author, PageRequest.of(page, size)).getContent().stream()
                .map(this::toProfileDTO)
                .toList();
    }

    @Override
    public List<PaymentReadDTO> findAll(Integer page, Integer size) {
        return paymentRepository.findAll(PageRequest.of(page, size)).getContent()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public PaymentReadDTO makePayment(Long bookingId, PaymentBookingDTO payment, Authentication authentication) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingException("Бронирование не найдено!"));
        if (Boolean.FALSE.equals(bookingService.isAvailableForBooking(booking.getResource().getId()))) {
            throw new ResourceException(
                    String.format("Ресурс недоступен для бронирования (ID %s) - сущность деактивирована или недоступна",
                            bookingId)
            );
        }
        User user = userRepository.getByEmail(authentication.getName()).orElseThrow(() -> new UserException("Пользователь не найден!"));
        if (!user.getEmail().equals(booking.getAuthor().getEmail())) {
            log.error("Попытка оплатить чужую бронь с ID {} пользователем {}", bookingId, user.getEmail());
            throw new PaymentException("Нельзя оплатить чужое бронирование!");
        }

        if (Boolean.TRUE.equals(booking.getIsConfirmed())){
            throw new PaymentException("Бронь уже подтверждена (оплачена)");
        }

        Resource resource = booking.getResource();

        if (Boolean.FALSE.equals(bookingService.isBookingPossible(booking.getStartDate(), booking.getEndDate(), resource))){
            log.error(booking.toString());
            bookingRepository.deleteById(booking.getId());
            log.error("Удалена неоплаченная бронь с ID {} - внесен платеж на другую бронь, пересекающуюся по времени", bookingId);
            throw new BookingException("Бронь деактивирована - вы не успели внести оплату до того момента, как это время оплатил другой пользователь");
        }

        Payment finalPayment = executePayment(
                bookingId, payment.methodCode(),
                bookingService.getAmountForBooking(booking.getStartDate(), booking.getEndDate(), resource.getHourlyRate()),
                payment.credentials());
        booking.setIsConfirmed(true);
        bookingRepository.save(booking);
        return toDTO(finalPayment);
    }

    private PaymentProfileReadDTO toProfileDTO(Payment payment) {
        return new PaymentProfileReadDTO(payment.getBooking().getResource().getTitle(), payment.getPaymentMethod().getTitle(),
                payment.getPaymentStatus().getStatus(), payment.getBooking().getResource().getId(), payment.getBooking().getId(),
                payment.getCredentials(), payment.getAmount().doubleValue(), payment.getCreatedAt());
    }

    private PaymentReadDTO toDTO(Payment payment) {
        User updatedBy = payment.getUpdatedBy();
        String updatedAuthorName = String.format(NAME_PATTERN, updatedBy.getName(), updatedBy.getSurname());
        return new PaymentReadDTO(payment.getBooking().getResource().getTitle(), payment.getPaymentMethod().getTitle(),
                payment.getPaymentStatus().getStatus(), payment.getBooking().getResource().getId(), payment.getBooking().getId(),
                payment.getId(), payment.getCredentials(), payment.getAmount().doubleValue(), updatedAuthorName,
                payment.getCreatedAt(), payment.getUpdatedAt());
    }
}
