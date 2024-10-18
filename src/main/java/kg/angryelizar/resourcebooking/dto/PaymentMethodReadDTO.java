package kg.angryelizar.resourcebooking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "DTO для просмотра информации о методе оплаты")
public record PaymentMethodReadDTO(
        @Schema(description = "Код метода оплаты, необходимый для проведения оплаты для подтверждения бронирования", example = "CARD")
        String code,
        @Schema(description = "Название метода оплаты", example = "Банковская карта")
        String title,
        @Schema(description = "Описание метода оплаты", example = "VISA, Mastercard, Элкарт")
        String description,
        @Schema(description = "Пример данных для метода оплаты", example = "4169963312341234")
        String credentialsExample
) {
}
