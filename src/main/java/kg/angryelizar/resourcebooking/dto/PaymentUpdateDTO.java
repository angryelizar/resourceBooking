package kg.angryelizar.resourcebooking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "DTO для обновления информации об оплате (администратор)")
public record PaymentUpdateDTO(
        @NotEmpty(message = "Код метода оплаты не может быть пустым!")
        @Schema(description = "Код метода оплаты, необходимый для проведения оплаты для подтверждения бронирования", example = "CARD")
        String methodCode,
        @NotEmpty(message = "Статус оплаты не может быть пустым!")
        @Schema(description = "Статус оплаты", example = "CONFIRMED")
        String status,
        @NotEmpty(message = "Реквизиты для оплаты не могут быть пустыми!")
        @Schema(description = "Реквизиты для оплаты (зависят от метода оплаты)", example = "4169963312341234")
        String credentials,
        @Positive(message = "Сумма платежа не может быть меньше или равна нулю!")
        @NotNull(message = "Сумма платежа не может быть пустой!")
        @Schema(description = "Сумма платежа", example = "100.0")
        Double amount
) {
}
