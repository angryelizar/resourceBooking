package kg.angryelizar.resourcebooking.service;

import kg.angryelizar.resourcebooking.dto.ResourceCreateDTO;
import kg.angryelizar.resourcebooking.dto.ResourceReadDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ResourceService {
    List<ResourceReadDTO> findAll(Integer page, Integer pageSize, Boolean active);

    ResourceReadDTO create(ResourceCreateDTO resource, Authentication authentication);
}
