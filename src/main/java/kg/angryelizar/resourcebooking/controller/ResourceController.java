package kg.angryelizar.resourcebooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.angryelizar.resourcebooking.dto.ResourceReadDto;
import kg.angryelizar.resourcebooking.exceptions.ErrorResponseBody;
import kg.angryelizar.resourcebooking.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @ApiResponse(responseCode = "200", description = "Запрос прошел успешно", content = @Content(schema = @Schema(implementation = ResourceReadDto.class))),
            @ApiResponse(responseCode = "400", description = "Произошла ошибка при выполнении запроса", content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponseBody.class)))
    })
    public ResponseEntity<List<ResourceReadDto>> getResources(
            @Parameter(description = "Номер страницы (начинается от 0)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Размер страницы (по умолчанию - 5)") @RequestParam(defaultValue = "5") Integer size,
            @Parameter(description = "Показать активные или неактивные ресурсы (по умолчанию показывает активные, isActive = true)") @RequestParam(defaultValue = "true") Boolean isActive) {
        return ResponseEntity.ok(resourceService.findAll(page, size, isActive));
    }
}
