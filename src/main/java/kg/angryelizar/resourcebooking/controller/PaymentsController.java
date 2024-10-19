package kg.angryelizar.resourcebooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.angryelizar.resourcebooking.dto.PaymentBookingDTO;
import kg.angryelizar.resourcebooking.dto.PaymentMethodReadDTO;
import kg.angryelizar.resourcebooking.dto.PaymentReadDTO;
import kg.angryelizar.resourcebooking.dto.PaymentUpdateDTO;
import kg.angryelizar.resourcebooking.exceptions.ErrorResponseBody;
import kg.angryelizar.resourcebooking.service.PaymentMethodService;
import kg.angryelizar.resourcebooking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
@Tag(name = "Управление платежами", description = "Просмотр методов оплаты для всех пользователей, проведение оплат за бронирование для пользователей с ролью USER, а также просмотр, редактирование и удаление платежей для администраторов")
public class PaymentsController {
    private final PaymentMethodService paymentMethodService;
    private final PaymentService paymentService;

    @GetMapping
    @Operation(summary = "Просмотр всех платежей в системе", tags = "Payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос прошел успешно",
                    content = @Content(schema = @Schema(implementation = PaymentReadDTO.class))),
            @ApiResponse(responseCode = "400", description = "Произошла ошибка при выполнении запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - скорее всего вы не администратор",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class)))
    })
    public ResponseEntity<List<PaymentReadDTO>> payments(
            @Parameter(description = "Номер страницы (начинается от 0)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Размер страницы (по умолчанию - 5)") @RequestParam(defaultValue = "5") Integer size
    ) {
        return ResponseEntity.ok(paymentService.findAll(page, size));
    }

    @PutMapping("/{paymentId}")
    @Operation(summary = "Изменение информации о платеже (администратор)", tags = "Payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос прошел успешно",
                    content = @Content(schema = @Schema(implementation = PaymentMethodReadDTO.class))),
            @ApiResponse(responseCode = "400", description = "Произошла ошибка при выполнении запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "409", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - скорее всего вы не тот, кто это должен делать",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class)))
    })
    public ResponseEntity<PaymentReadDTO> editPayment(
            @PathVariable @Parameter(description = "Идентификатор платежа") Long paymentId,
            @Valid @RequestBody PaymentUpdateDTO paymentUpdateDTO,
            Authentication authentication){
        return ResponseEntity.ok(paymentService.edit(paymentId, paymentUpdateDTO, authentication));
    }


    @PostMapping("/bookings/{bookingId}")
    @Operation(summary = "Проведение оплаты за осуществленную бронь", tags = "Payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос прошел успешно",
                    content = @Content(schema = @Schema(implementation = PaymentMethodReadDTO.class))),
            @ApiResponse(responseCode = "400", description = "Произошла ошибка при выполнении запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - скорее всего вы не тот, кто это должен делать",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class)))
    })
    public ResponseEntity<PaymentReadDTO> bookings(
            @PathVariable @Parameter(description = "Идентификатор бронирования") Long bookingId,
            @Valid @RequestBody PaymentBookingDTO payment,
            Authentication authentication) {
        return ResponseEntity.ok(paymentService.makePayment(bookingId, payment, authentication));
    }

    @GetMapping("/methods")
    @Operation(summary = "Просмотр всех возможных методов оплаты", tags = "Payment method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос прошел успешно",
                    content = @Content(schema = @Schema(implementation = PaymentMethodReadDTO.class))),
            @ApiResponse(responseCode = "400", description = "Произошла ошибка при выполнении запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class)))
    })
    public ResponseEntity<List<PaymentMethodReadDTO>> methods(){
        return ResponseEntity.ok(paymentMethodService.findAll());
    }
}
