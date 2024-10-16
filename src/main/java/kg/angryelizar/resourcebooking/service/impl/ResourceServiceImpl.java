package kg.angryelizar.resourcebooking.service.impl;

import kg.angryelizar.resourcebooking.dto.ResourceCreateDTO;
import kg.angryelizar.resourcebooking.dto.ResourceReadDTO;
import kg.angryelizar.resourcebooking.exceptions.UserException;
import kg.angryelizar.resourcebooking.model.Resource;
import kg.angryelizar.resourcebooking.model.User;
import kg.angryelizar.resourcebooking.repository.ResourceRepository;
import kg.angryelizar.resourcebooking.repository.UserRepository;
import kg.angryelizar.resourcebooking.service.ResourceService;
import kg.angryelizar.resourcebooking.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public List<ResourceReadDTO> findAll(Integer page, Integer pageSize, Boolean active) {
        return resourceRepository.findAllByIsActive(PageRequest.of(page, pageSize), active).getContent().stream().map(this::toResourceReadDto).toList();
    }

    @Override
    public ResourceReadDTO create(ResourceCreateDTO resource, Authentication authentication) {
        Optional<User> maybeAuthor = userRepository.getByEmail(authentication.getName());
        if (maybeAuthor.isEmpty()) {
            log.error("Пользователь {} не найден", authentication.getName());
            throw new UserException(String.format("Пользователь %s не найден", authentication.getName()));
        }

        if (Boolean.FALSE.equals(userService.isAdministrator(maybeAuthor.get()))){
            log.error("Пользователь {} не администратор и не может создать ресурс!", maybeAuthor.get().getEmail());
            throw new UserException("Вы не администратор для этого действия!");
        }

        Resource savedResource = Resource.resourceBuilder()
                .title(resource.title())
                .description(resource.description())
                .isActive(resource.isActive())
                .hourlyRate(BigDecimal.valueOf(resource.hourlyRate()))
                .build();
        savedResource.setUpdatedBy(maybeAuthor.get());
        ResourceReadDTO result = toResourceReadDto(resourceRepository.save(savedResource));
        log.info("Создан новый ресурс {}, автор - {}", result.title(), result.updatedByName());
        return result;
    }

    private ResourceReadDTO toResourceReadDto(Resource resource) {
            String author = String.format("%s %s", resource.getUpdatedBy().getName(),  resource.getUpdatedBy().getSurname());
        return new ResourceReadDTO(resource.getId(), resource.getTitle(), resource.getDescription(),
                    resource.getIsActive(), resource.getHourlyRate().doubleValue(), resource.getCreatedAt(),
                    resource.getUpdatedAt(), author);
    }
}
