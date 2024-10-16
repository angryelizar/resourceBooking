package kg.angryelizar.resourcebooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.angryelizar.resourcebooking.dto.ResourceCreateDTO;
import kg.angryelizar.resourcebooking.dto.ResourceReadDTO;
import kg.angryelizar.resourcebooking.exceptions.ErrorResponseBody;
import kg.angryelizar.resourcebooking.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
@Tag(name = "Работа с ресурсами",
        description = "Просмотр ресурсов для всех пользователей системы, а также создание, редактирование и удаление ресурса для администраторов")
public class ResourceController {
    private final ResourceService resourceService;

    @GetMapping
    @Operation(summary = "Просмотр всех ресурсов с фильтрацией по активным и неактивным, а также поддержкой пагинации", tags = "Resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос прошел успешно", content = @Content(schema = @Schema(implementation = ResourceReadDTO.class))),
            @ApiResponse(responseCode = "400", description = "Произошла ошибка при выполнении запроса", content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponseBody.class)))
    })
    public ResponseEntity<List<ResourceReadDTO>> getResources(
            @Parameter(description = "Номер страницы (начинается от 0)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Размер страницы (по умолчанию - 5)") @RequestParam(defaultValue = "5") Integer size,
            @Parameter(description = "Показать активные или неактивные ресурсы (по умолчанию показывает активные, isActive = true)") @RequestParam(defaultValue = "true") Boolean isActive) {
        return ResponseEntity.ok(resourceService.findAll(page, size, isActive));
    }

    @PostMapping
    @Operation(summary = "Создание нового ресурса (доступно только администраторам)", tags = "Resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос прошел успешно - ресурс создан", content = @Content(schema = @Schema(implementation = ResourceReadDTO.class))),
            @ApiResponse(responseCode = "409", description = "Произошла ошибка валидации", content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "400", description = "Произошла ошибка при выполнении запроса", content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponseBody.class)))
    })
    public ResponseEntity<ResourceReadDTO> createResource(@Valid @RequestBody ResourceCreateDTO resource, Authentication authentication) {
        return ResponseEntity.ok(resourceService.create(resource, authentication));
    }

}
