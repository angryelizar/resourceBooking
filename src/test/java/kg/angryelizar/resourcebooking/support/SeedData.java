package kg.angryelizar.resourcebooking.support;

import kg.angryelizar.resourcebooking.model.Resource;
import kg.angryelizar.resourcebooking.repository.ResourceRepository;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Данные, которые миграция 002_insert_test_data кладёт в базу, и помощники для тестов.
 */
public final class SeedData {
    public static final String ADMIN_EMAIL = "conovalov.elizar@gmail.com";
    public static final String USER_EMAIL = "saadanbekov@gmail.com";

    /** Активный ресурс, 100 за час. */
    public static final String ACTIVE_RESOURCE = "зал ololoPlanet";
    /** Деактивированный ресурс. */
    public static final String INACTIVE_RESOURCE = "Иссык-Куль";

    private SeedData() {
    }

    public static Authentication auth(String email) {
        return new TestingAuthenticationToken(email, null);
    }

    public static Resource resource(ResourceRepository repository, String title) {
        return repository.findAll().stream()
                .filter(r -> r.getTitle().equals(title))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Нет ресурса в тестовых данных: " + title));
    }

    /** Момент в будущем: через {@code days} дней, в {@code hour}:00. */
    public static LocalDateTime future(int days, int hour) {
        return LocalDate.now().plusDays(days).atTime(hour, 0);
    }
}
