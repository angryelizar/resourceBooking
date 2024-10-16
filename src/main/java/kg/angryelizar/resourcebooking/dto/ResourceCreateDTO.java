package kg.angryelizar.resourcebooking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "DTO для создания ресурса (доступно только администраторам)")
public record ResourceCreateDTO(
        @Schema(description = "Название ресурса", example = "Hyatt Regency Bishkek")
        @NotEmpty(message = "Название ресурса не может быть пустым!")
        @NotNull(message = "Название ресурса не может быть null!")
        String title,
        @Schema(description = "Описание ресурса", example = "Конференц-зал")
        @NotNull(message = "Описание ресурса не может быть null!")
        @NotEmpty(message = "Описание ресурса не может быть пустым!")
        String description,
        @Schema(description = "Активный ли ресурс для брони или нет", example = "true")
        @NotNull(message = "Это значение не может быть null!")
        Boolean isActive,
        @Schema(description = "Стоимость бронирования за час", example = "100.0")
        @NotNull(message = "Стоимость бронирования не может быть null!")
        @Positive(message = "Стоимость бронирования не может быть меньше или равно нулю")
        Double hourlyRate) {
}
