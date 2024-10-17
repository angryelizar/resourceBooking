package kg.angryelizar.resourcebooking.service;

import kg.angryelizar.resourcebooking.dto.BookingCreateDTO;
import kg.angryelizar.resourcebooking.dto.BookingReadDTO;
import kg.angryelizar.resourcebooking.dto.ResourceReadDTO;
import kg.angryelizar.resourcebooking.model.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookingService {
    void deleteBookingsByResource(Resource resource);

    ResponseEntity<BookingReadDTO> create(Long resourceId, BookingCreateDTO bookingCreateDTO, Authentication authentication);

    List<BookingReadDTO> findAll(Integer page, Integer size, Boolean isConfirmed);
}
