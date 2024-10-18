package kg.angryelizar.resourcebooking.service.impl;

import kg.angryelizar.resourcebooking.dto.PaymentProfileReadDTO;
import kg.angryelizar.resourcebooking.dto.PaymentReadDTO;
import kg.angryelizar.resourcebooking.exceptions.PaymentException;
import kg.angryelizar.resourcebooking.exceptions.UserException;
import kg.angryelizar.resourcebooking.model.Payment;
import kg.angryelizar.resourcebooking.model.User;
import kg.angryelizar.resourcebooking.repository.PaymentRepository;
import kg.angryelizar.resourcebooking.repository.UserRepository;
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
    private final Map<String, PaymentStrategy> paymentStrategies;
    private static final String NAME_PATTERN = "%s %s";

    public void executePayment(Long bookingId, String method, BigDecimal amount, String credentials) {
        PaymentStrategy paymentStrategy = paymentStrategies.get(method.toLowerCase());
        if (paymentStrategy != null) {
            paymentStrategy.pay(bookingId, method, credentials, amount);
            return;
        }
        log.error("Не найдена реализация для этого метода оплаты - {}", method);
        throw new PaymentException("Неизвестный метод оплаты - " + method);
    }

    @Override
    public List<PaymentProfileReadDTO> findAllForUser(Authentication authentication, Integer page, Integer size) {
        User author = userRepository.getByEmail(authentication.getName()).orElseThrow(() -> new UserException("Пользователь не найден!"));
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
