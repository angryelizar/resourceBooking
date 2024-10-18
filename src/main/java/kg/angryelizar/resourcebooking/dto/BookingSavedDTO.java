package kg.angryelizar.resourcebooking.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO для просмотра информации об успешном бронировании. Включает в себя информацию о сумме, необходимой для оплаты брони")
public record BookingSavedDTO(
        @Schema(description = "Название ресурса, на который назначена бронь", example = "Hyatt Regency Bishkek")
        String resourceName,
        @Schema(description = "Идентификатор бронирования", example = "2")
        Long bookingId,
        @Schema(description = "Дата старта бронирования", example = "2007-12-03T10:15:30:55.000000")
        LocalDateTime startDate,
        @Schema(description = "Дата окончания бронирования", example = "2007-12-03T18:15:30:55.000000")
        LocalDateTime endDate,
        @Schema(description = "Имя и фамилия человека, который осуществил бронирование",  example = "Камчыбек Жапаров")
        String author,
        @Schema(description = "Сумма, необходимая для оплаты бронирования и перевода isConfirmed = true")
        Double amountForPay,
        @Schema(description = "Статус подтверждения брони, true - бронь оплачена, false - не оплачено")
        Boolean isConfirmed
) {
}
