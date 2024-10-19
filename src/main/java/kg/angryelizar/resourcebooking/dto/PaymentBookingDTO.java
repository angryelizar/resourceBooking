package kg.angryelizar.resourcebooking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "DTO для отправки запроса на оплату бронирования (доступно пользователям с ролью USER)")
public record PaymentBookingDTO(
        @NotEmpty(message = "Код метода оплаты не может быть пустым!")
        @Schema(description = "Код метода оплаты, необходимый для проведения оплаты для подтверждения бронирования", example = "CARD")
        String methodCode,
        @NotEmpty(message = "Реквизиты для оплаты не могут быть пустыми!")
        @Schema(description = "Реквизиты для оплаты (зависят от метода оплаты)", example = "4169963312341234")
        String credentials
) {
}
