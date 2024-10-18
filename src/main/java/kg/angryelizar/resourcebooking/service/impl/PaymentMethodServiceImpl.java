package kg.angryelizar.resourcebooking.service.impl;

import kg.angryelizar.resourcebooking.dto.PaymentMethodReadDTO;
import kg.angryelizar.resourcebooking.model.PaymentMethod;
import kg.angryelizar.resourcebooking.repository.PaymentMethodRepository;
import kg.angryelizar.resourcebooking.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;

    @Override
    public List<PaymentMethodReadDTO> findAll() {
        return paymentMethodRepository.findAll().stream().map(this::toDTO).toList();
    }

    private PaymentMethodReadDTO toDTO(PaymentMethod paymentMethod) {
        return new PaymentMethodReadDTO(paymentMethod.getCode(), paymentMethod.getTitle(),
                paymentMethod.getDescription(), paymentMethod.getCredentialsExample());
    }
}
