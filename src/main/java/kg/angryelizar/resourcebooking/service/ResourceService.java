package kg.angryelizar.resourcebooking.service;

import kg.angryelizar.resourcebooking.dto.ResourceCreateEditDTO;
import kg.angryelizar.resourcebooking.dto.ResourceReadDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ResourceService {
    List<ResourceReadDTO> findAll(Integer page, Integer pageSize, Boolean active);

    ResourceReadDTO create(ResourceCreateEditDTO resource, Authentication authentication);

    ResourceReadDTO update(Long resourceId, ResourceCreateEditDTO resourceDTO, Authentication authentication);
}
