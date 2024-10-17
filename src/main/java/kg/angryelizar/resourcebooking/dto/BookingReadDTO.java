package kg.angryelizar.resourcebooking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "DTO для просмотра информации о бронировании")
public record BookingReadDTO(
        @Schema(description = "Название ресурса, на который назначена бронь", example = "Hyatt Regency Bishkek")
        String resourceName,
        @Schema(description = "Идентификатор ресурса, на который назначена бронь", example = "1")
        Long resourceId,
        @Schema(description = "Идентификатор бронирования", example = "2")
        Long bookingId,
        @Schema(description = "Дата старта бронирования", example = "2007-12-03T10:15:30:55.000000")
        LocalDateTime startDate,
        @Schema(description = "Дата окончания бронирования", example = "2007-12-03T18:15:30:55.000000")
        LocalDateTime endDate,
        @Schema(description = "Статус подтверждения брони, true - бронь оплачена, false - не оплачено")
        Boolean isConfirmed,
        @Schema(description = "Имя и фамилия человека, который осуществил бронирование")
        String author,
        @Schema(description = "Имя пользователя, обновившего бронирование в последний раз")
        String updatedByName,
        @Schema(description = "Дата создания бронирования")
        LocalDateTime createdAt,
        @Schema(description = "Дата обновления бронирования")
        LocalDateTime updatedAt) {
}
