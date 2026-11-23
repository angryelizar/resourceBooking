package kg.angryelizar.resourcebooking.service;

import jakarta.persistence.EntityManager;
import kg.angryelizar.resourcebooking.dto.BookingCreateDTO;
import kg.angryelizar.resourcebooking.dto.ResourceCreateEditDTO;
import kg.angryelizar.resourcebooking.dto.ResourceReadDTO;
import kg.angryelizar.resourcebooking.exceptions.UserException;
import kg.angryelizar.resourcebooking.model.Resource;
import kg.angryelizar.resourcebooking.repository.BookingRepository;
import kg.angryelizar.resourcebooking.repository.ResourceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static kg.angryelizar.resourcebooking.support.SeedData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ResourceServiceTest {

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private EntityManager entityManager;

    private static final ResourceCreateEditDTO NEW_RESOURCE =
            new ResourceCreateEditDTO("Переговорная 3", "Третий этаж", true, 150.0);

    @Test
    void adminCreatesResource() {
        ResourceReadDTO created = resourceService.create(NEW_RESOURCE, auth(ADMIN_EMAIL));

        assertThat(created.id()).isNotNull();
        assertThat(created.title()).isEqualTo("Переговорная 3");
        assertThat(created.isActive()).isTrue();
        assertThat(created.hourlyRate()).isEqualTo(150.0);
    }

    @Test
    void adminUpdatesResourceHourlyRate() {
        ResourceCreateEditDTO initial = new ResourceCreateEditDTO("Переговорная 3", "Третий этаж", true, 100.0);
        ResourceReadDTO created = resourceService.create(initial, auth(ADMIN_EMAIL));
        ResourceCreateEditDTO changes = new ResourceCreateEditDTO("Переговорная 3", "Третий этаж", true, 300.0);

        ResourceReadDTO updated = resourceService.update(created.id(), changes, auth(ADMIN_EMAIL));

        entityManager.flush();
        entityManager.clear();
        Resource saved = resourceRepository.findById(created.id()).orElseThrow();

        assertThat(updated.hourlyRate()).isEqualTo(300.0);
        assertThat(saved.getHourlyRate()).isEqualByComparingTo("300");
    }

    @Test
    void regularUserCannotCreateResource() {
        assertThatThrownBy(() -> resourceService.create(NEW_RESOURCE, auth(USER_EMAIL)))
                .isInstanceOf(UserException.class);
    }

    @Test
    void deletingResourceDeletesItsBookings() {
        ResourceReadDTO created = resourceService.create(NEW_RESOURCE, auth(ADMIN_EMAIL));
        bookingService.create(created.id(), new BookingCreateDTO(future(10, 10), future(10, 12)), auth(USER_EMAIL));

        resourceService.delete(created.id(), auth(ADMIN_EMAIL));

        assertThat(resourceRepository.findById(created.id())).isEmpty();
        assertThat(bookingRepository.findAll())
                .noneMatch(b -> b.getResource().getId().equals(created.id()));
    }
}
