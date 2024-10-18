package kg.angryelizar.resourcebooking.service.impl;

import kg.angryelizar.resourcebooking.dto.BookingCreateDTO;
import kg.angryelizar.resourcebooking.exceptions.PaymentException;
import kg.angryelizar.resourcebooking.service.PaymentService;
import kg.angryelizar.resourcebooking.strategy.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final Map<String, PaymentStrategy> paymentStrategies;

    public void executePayment(Long bookingId, String method, BigDecimal amount, String credentials) {
        PaymentStrategy paymentStrategy = paymentStrategies.get(method.toLowerCase());
        if (paymentStrategy != null) {
            paymentStrategy.pay(bookingId, method, credentials, amount);
            return;
        }
        log.error("Не найдена реализация для этого метода оплаты - {}", method);
        throw new PaymentException("Неизвестный метод оплаты - " + method);
    }
}
