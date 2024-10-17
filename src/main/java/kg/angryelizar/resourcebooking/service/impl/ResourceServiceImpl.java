package kg.angryelizar.resourcebooking.service.impl;

import kg.angryelizar.resourcebooking.dto.ResourceCreateEditDTO;
import kg.angryelizar.resourcebooking.dto.ResourceReadDTO;
import kg.angryelizar.resourcebooking.exceptions.ResourceException;
import kg.angryelizar.resourcebooking.model.Resource;
import kg.angryelizar.resourcebooking.model.User;
import kg.angryelizar.resourcebooking.repository.ResourceRepository;
import kg.angryelizar.resourcebooking.repository.UserRepository;
import kg.angryelizar.resourcebooking.service.BookingService;
import kg.angryelizar.resourcebooking.service.ResourceService;
import kg.angryelizar.resourcebooking.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final UserService userService;
    private final BookingService bookingService;

    @Override
    public List<ResourceReadDTO> findAll(Integer page, Integer pageSize, Boolean active) {
        return resourceRepository.findAllByIsActive(PageRequest.of(page, pageSize), active).getContent().stream().map(this::toResourceReadDto).toList();
    }

    @Override
    public ResourceReadDTO create(ResourceCreateEditDTO resource, Authentication authentication) {
      User author = userService.checkAdministrator(authentication);

        Resource savedResource = Resource.resourceBuilder()
                .title(resource.title())
                .description(resource.description())
                .isActive(resource.isActive())
                .hourlyRate(BigDecimal.valueOf(resource.hourlyRate()))
                .build();
        savedResource.setUpdatedBy(author);

        ResourceReadDTO result = toResourceReadDto(resourceRepository.save(savedResource));
        log.info("Создан новый ресурс {}, автор - {}", result.title(), result.updatedByName());

        return result;
    }

    @Override
    public ResourceReadDTO update(Long resourceId, ResourceCreateEditDTO resourceDTO, Authentication authentication) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(() -> new ResourceException("Этот ресурс не найден, ID " + resourceId));
        User author = userService.checkAdministrator(authentication);

        resource.setTitle(resourceDTO.title());
        resource.setDescription(resourceDTO.description());
        resource.setIsActive(resourceDTO.isActive());
        resource.setHourlyRate(BigDecimal.valueOf(resourceDTO.hourlyRate()));
        resource.setUpdatedBy(author);
        resourceRepository.save(resource);

        log.info("Ресурс {} обновлен, автор изменений - {} {}", resourceDTO.title(), author.getName(), author.getSurname());
        return toResourceReadDto(resource);
    }

    @Override
    public HttpStatus delete(Long resourceId, Authentication authentication) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(() -> new ResourceException("Этот ресурс не найден, ID " + resourceId));
        User author = userService.checkAdministrator(authentication);
        bookingService.deleteBookingsByResource(resource);
        resourceRepository.delete(resource);

        log.info("Ресурс {} ({}) удален, автор {} {}", resource.getTitle(), resourceId, author.getName(), author.getSurname());
        return HttpStatus.OK;
    }

    private ResourceReadDTO toResourceReadDto(Resource resource) {
            String author = String.format("%s %s", resource.getUpdatedBy().getName(),  resource.getUpdatedBy().getSurname());
        return new ResourceReadDTO(resource.getId(), resource.getTitle(), resource.getDescription(),
                    resource.getIsActive(), resource.getHourlyRate().doubleValue(), resource.getCreatedAt(),
                    resource.getUpdatedAt(), author);
    }
}
