package kg.angryelizar.resourcebooking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "DTO для редактирования информации о бронировании (администратор)")
public record BookingUpdateDTO(
        @Schema(description = "Дата старта бронирования", example = "2007-12-03T10:15:30:55.000000")
        @Future(message = "Дата старта не может быть в прошлом!")
        @NotNull(message = "Дата начала не может быть null!")
        LocalDateTime startDate,
        @Schema(description = "Дата окончания бронирования", example = "2007-12-03T18:15:30:55.000000")
        @Future(message = "Дата старта не может быть в прошлом!")
        @NotNull(message = "Дата начала не может быть null!")
        LocalDateTime endDate,
        @Schema(description = "Статус подтверждения брони, true - бронь оплачена, false - не оплачено")
        @NotNull(message = "Статус подтверждения не может быть null!")
        Boolean isConfirmed) {
}
