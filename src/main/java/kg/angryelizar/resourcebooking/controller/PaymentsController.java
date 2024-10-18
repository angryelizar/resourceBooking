package kg.angryelizar.resourcebooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.angryelizar.resourcebooking.dto.PaymentMethodReadDTO;
import kg.angryelizar.resourcebooking.exceptions.ErrorResponseBody;
import kg.angryelizar.resourcebooking.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
@Tag(name = "Управление платежами", description = "Просмотр методов оплаты для всех пользователей, а также просмотр, редактирование и удаление платежей")
public class PaymentsController {
    private final PaymentMethodService paymentMethodService;

    @GetMapping("/methods")
    @Operation(summary = "Просмотр всех возможных методов оплаты", tags = "Payment method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос прошел успешно", content = @Content(schema = @Schema(implementation = PaymentMethodReadDTO.class))),
            @ApiResponse(responseCode = "400", description = "Произошла ошибка при выполнении запроса", content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponseBody.class)))
    })
    public ResponseEntity<List<PaymentMethodReadDTO>> methods(){
        return ResponseEntity.ok(paymentMethodService.findAll());
    }
}
