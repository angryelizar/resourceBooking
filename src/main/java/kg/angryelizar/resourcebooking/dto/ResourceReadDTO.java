package kg.angryelizar.resourcebooking.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO для просмотра информации о ресурсе")
public record ResourceReadDTO(
        @Schema(description = "Идентификатор ресурса", example = "1")
        Long id,
        @Schema(description = "Название ресурса", example = "Hyatt Regency Bishkek")
        String title,
        @Schema(description = "Описание ресурса", example = "Конференц-зал")
        String description,
        @Schema(description = "Активный ли ресурс для брони или нет", example = "true")
        Boolean isActive,
        @Schema(description = "Стоимость бронирования за час", example = "100.0")
        Double hourlyRate,
        @Schema(description = "Дата создания ресурса")
        LocalDateTime createdAt,
        @Schema(description = "Дата обновления ресурса")
        LocalDateTime updatedAt,
        @Schema(description = "Имя пользователя, обновившего ресурс в последний раз", example = "Елизар Коновалов")
        String updatedByName) {
}
