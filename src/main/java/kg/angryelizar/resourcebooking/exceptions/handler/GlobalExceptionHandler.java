package kg.angryelizar.resourcebooking.exceptions.handler;

import kg.angryelizar.resourcebooking.exceptions.ErrorResponseBody;
import kg.angryelizar.resourcebooking.exceptions.UserException;
import kg.angryelizar.resourcebooking.service.ErrorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorService errorService;

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponseBody> userAlreadyRegistered(UserException exception) {
        return new ResponseEntity<>(errorService.makeResponse(exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseBody> validationHandler(MethodArgumentNotValidException exception) {
        return new ResponseEntity<>(errorService.makeResponse(exception.getBindingResult()), HttpStatus.CONFLICT);
    }
}
