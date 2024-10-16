package kg.angryelizar.resourcebooking.service.impl;

import kg.angryelizar.resourcebooking.enums.PaymentStatus;
import kg.angryelizar.resourcebooking.exceptions.PaymentException;
import kg.angryelizar.resourcebooking.model.Booking;
import kg.angryelizar.resourcebooking.model.Payment;
import kg.angryelizar.resourcebooking.model.PaymentMethod;
import kg.angryelizar.resourcebooking.repository.BookingRepository;
import kg.angryelizar.resourcebooking.repository.PaymentMethodRepository;
import kg.angryelizar.resourcebooking.repository.PaymentRepository;
import kg.angryelizar.resourcebooking.repository.PaymentStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FakePaymentServiceImpl {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentStatusRepository paymentStatusRepository;

    public Payment makePayment(Long bookingId, String method, String credentials, BigDecimal amount) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        Optional<PaymentMethod> paymentMethod = paymentMethodRepository.findByCode(method.toUpperCase());
        if (booking.isEmpty()) {
            log.error("Оплата невозможна - бронирования с ID {} не существует!", bookingId);
            throw new PaymentException(String.format("Оплата невозможна - бронирования с ID %s не существует!", bookingId));
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Оплата невозможна - сумма не может быть меньше или равна нулю!");
            throw new PaymentException("Оплата невозможна - сумма не может быть меньше или равна нулю!");
        }
        return paymentRepository.save(Payment.paymentBuilder()
                .paymentStatus(paymentStatusRepository.findByStatus(PaymentStatus.CONFIRMED.getValue()))
                .booking(booking.get())
                .paymentMethod(paymentMethod.get())
                .amount(amount)
                .credentials(credentials)
                .updatedBy(booking.get().getAuthor())
                .build());
    }
}
