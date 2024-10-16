package kg.angryelizar.resourcebooking.service;

import kg.angryelizar.resourcebooking.model.Resource;
import org.springframework.stereotype.Service;

@Service
public interface BookingService {
    void deleteBookingsByResource(Resource resource);
}
