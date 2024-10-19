package kg.angryelizar.resourcebooking.service;

import kg.angryelizar.resourcebooking.dto.PaymentBookingDTO;
import kg.angryelizar.resourcebooking.dto.PaymentProfileReadDTO;
import kg.angryelizar.resourcebooking.dto.PaymentReadDTO;
import kg.angryelizar.resourcebooking.dto.PaymentUpdateDTO;
import kg.angryelizar.resourcebooking.model.Payment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface PaymentService {
    Payment executePayment(Long bookingId, String method, BigDecimal amount, String credentials);

    List<PaymentProfileReadDTO> findAllForUser(Authentication authentication, Integer page, Integer size);

    List<PaymentReadDTO> findAll(Integer page, Integer size);

    PaymentReadDTO makePayment(Long bookingId, PaymentBookingDTO payment, Authentication authentication);

    PaymentReadDTO edit(Long paymentId, PaymentUpdateDTO paymentUpdateDTO, Authentication authentication);

    HttpStatus delete(Long paymentId);
}
