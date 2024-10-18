package kg.angryelizar.resourcebooking.service;

import kg.angryelizar.resourcebooking.dto.PaymentMethodReadDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PaymentMethodService {
    List<PaymentMethodReadDTO> findAll();
}
