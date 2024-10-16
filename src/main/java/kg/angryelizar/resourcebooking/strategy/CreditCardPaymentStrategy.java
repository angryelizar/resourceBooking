package kg.angryelizar.resourcebooking.strategy;

import kg.angryelizar.resourcebooking.service.impl.FakePaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("creditCard")
@RequiredArgsConstructor
@Slf4j
public class CreditCardPaymentStrategy implements PaymentStrategy {
    private final FakePaymentServiceImpl fakePaymentServiceImpl;

    @Override
    public void pay(Long bookingId, String method, String credentials, BigDecimal amount) {
        // Логика оплаты картой
        // Так как в рамках задания нет необходимости подключаться к API реального платежного шлюза - метод будет "заглушкой"
        // Но реализация этого функционала по паттерну "Стратегия" позволяет в наиболее оптимальной форме реализовывать разное поведение
        // для одного и того же действия
        fakePaymentServiceImpl.makePayment(bookingId, method, credentials, amount);
        log.error("Оплата по карте брони с ID {} на сумму {}", bookingId, amount);
    }

}
