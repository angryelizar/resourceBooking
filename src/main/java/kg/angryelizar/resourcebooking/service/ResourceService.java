package kg.angryelizar.resourcebooking.service;

import kg.angryelizar.resourcebooking.dto.ResourceReadDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ResourceService {
    List<ResourceReadDto> findAll(Integer page, Integer pageSize, Boolean active);
}
