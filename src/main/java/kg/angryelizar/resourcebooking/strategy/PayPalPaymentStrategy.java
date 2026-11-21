package kg.angryelizar.resourcebooking.strategy;

import kg.angryelizar.resourcebooking.model.Payment;
import kg.angryelizar.resourcebooking.service.impl.FakePaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("paypal")
@RequiredArgsConstructor
@Slf4j
public class PayPalPaymentStrategy implements PaymentStrategy {
    private final FakePaymentServiceImpl fakePaymentServiceImpl;
    private Long bookingId;
    private BigDecimal amount;

    @Override
    public Payment pay(Long bookingId, String method, String credentials, BigDecimal amount) {
        // Логика оплаты PayPal
        // Так как в рамках задания нет необходимости подключаться к API реального платежного шлюза - метод будет "заглушкой"
        // Но реализация этого функционала по паттерну "Стратегия" позволяет в наиболее оптимальной форме реализовывать разное поведение
        // для одного и того же действия
        this.bookingId = bookingId;
        this.amount = amount;
        log.info("Начата оплата по PayPal брони с ID {}", this.bookingId);
        Payment payment = fakePaymentServiceImpl.makePayment(this.bookingId, method, credentials, this.amount);
        log.error("Оплата по PayPal брони с ID {} на сумму {}", this.bookingId, this.amount);
        return payment;
    }
}
