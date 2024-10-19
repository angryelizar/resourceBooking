package kg.angryelizar.resourcebooking.configuration;

import kg.angryelizar.resourcebooking.strategy.CreditCardPaymentStrategy;
import kg.angryelizar.resourcebooking.strategy.PayPalPaymentStrategy;
import kg.angryelizar.resourcebooking.strategy.PaymentStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentStrategyConfig {
    @Bean
    public Map<String, PaymentStrategy> paymentStrategies(
            CreditCardPaymentStrategy creditCardPaymentStrategy,
            PayPalPaymentStrategy payPalPaymentStrategy) {

        return Map.of(
                "card", creditCardPaymentStrategy,
                "paypal", payPalPaymentStrategy
        );
    }
}
