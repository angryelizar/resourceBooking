package kg.angryelizar.resourcebooking.service.impl;

import kg.angryelizar.resourcebooking.dto.ResourceReadDto;
import kg.angryelizar.resourcebooking.model.Resource;
import kg.angryelizar.resourcebooking.repository.ResourceRepository;
import kg.angryelizar.resourcebooking.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;

    @Override
    public List<ResourceReadDto> findAll(Integer page, Integer pageSize, Boolean active) {
        return toResourceReadDto(resourceRepository.findAllByIsActive(PageRequest.of(page, pageSize), active).getContent());
    }

    private List<ResourceReadDto> toResourceReadDto(List<Resource> resources) {
        List<ResourceReadDto> resourceReadDtos = new ArrayList<>();
        resources.forEach(resource -> {
            String author = String.format("%s %s", resource.getUpdatedBy().getName(),  resource.getUpdatedBy().getSurname());
            resourceReadDtos.add(new ResourceReadDto(resource.getId(), resource.getTitle(), resource.getDescription(),
                    resource.getIsActive(), resource.getHourlyRate().doubleValue(), resource.getCreatedAt(),
                    resource.getUpdatedAt(), author));
        });
        return resourceReadDtos;
    }
}
