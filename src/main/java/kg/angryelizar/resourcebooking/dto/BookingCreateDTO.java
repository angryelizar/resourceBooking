package kg.angryelizar.resourcebooking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "DTO для запросов по бронированию ресурса")
public record BookingCreateDTO(
        @Schema(description = "Дата старта бронирования", example = "2007-12-03T10:15:30:55.000000")
        @Future(message = "Дата старта не может быть в прошлом!")
        @NotNull(message = "Дата начала не может быть null!")
        LocalDateTime startDate,
        @Schema(description = "Дата окончания бронирования", example = "2007-12-03T18:15:30:55.000000")
        @Future(message = "Дата окончания не может быть в прошлом!")
        @NotNull(message = "Дата окончания не может быть null!")
        LocalDateTime endDate) {
}
