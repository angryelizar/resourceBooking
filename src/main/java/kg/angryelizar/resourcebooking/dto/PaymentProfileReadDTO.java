package kg.angryelizar.resourcebooking.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO для просмотра информации о платеже (личный профиль пользователя с ролью USER)")
public record PaymentProfileReadDTO(
        @Schema(description = "Название ресурса", example = "Hyatt Regency Bishkek")
        String resourceName,
        @Schema(description = "Код метода оплаты", example = "CARD")
        String paymentMethodCode,
        @Schema(description = "Статус оплаты", example = "CONFIRMED")
        String status,
        @Schema(description = "Идентификатор ресурса", example = "1")
        Long resourceId,
        @Schema(description = "Идентификатор бронирования", example = "2")
        Long bookingId,
        @Schema(description = "Идентификатор платежного средства", example = "4169963312341234")
        String credentials,
        @Schema(description = "Сумма платежа", example = "100.0")
        Double amount,
        @Schema(description = "Дата проведения платежа")
        LocalDateTime createdAt
) {
}
