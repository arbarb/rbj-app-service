package ajp.app.config;

import ajp.app.common.model.ApiResponse;
import ajp.app.common.model.ErrorMessage;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@Slf4j
@RestControllerAdvice(basePackages = "ajp.app")
public class ControllerAdvice {

    private static final String DELIMITER = "::";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleException(MethodArgumentNotValidException exception) {
        log.error("Error while processing request", exception);

        List<FieldError> errors = exception.getBindingResult().getFieldErrors();
        List<ErrorMessage> errorMessages = errors.stream()
                .map(e -> getErrorMessage(e.getField(), e.getDefaultMessage()))
                .toList();
        ApiResponse<Void> response = ApiResponse.from(errorMessages);
        return ResponseEntity.badRequest().body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException exception) {
        log.error("Error while processing request", exception);
        Set<ConstraintViolation<?>> errors = exception.getConstraintViolations();
        List<ErrorMessage> errorMessages = errors.stream().map(c -> {
            String field = c.getPropertyPath().toString().split("\\.")[1];
            return getErrorMessage(field, c.getMessage());
        }).toList();
        ApiResponse<Void> response = ApiResponse.from(errorMessages);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(ResponseStatusException exception) {
        log.error("Error while processing request", exception);
        ErrorMessage errorMessage = getErrorMessage(null, exception.getReason());
        ApiResponse<Void> response = ApiResponse.from(errorMessage);
        return ResponseEntity.status(exception.getStatusCode()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        log.error("Error while processing request", exception);
        ErrorMessage errorMessage = getErrorMessage(null, exception.getMessage());
        ApiResponse<Void> response = ApiResponse.from(errorMessage);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    private void mapErrorMessage(ErrorMessage errorMessage, String message) {
        message = trimToEmpty(message);
        if (message.contains(DELIMITER)) {
            String[] split = message.split(DELIMITER);
            errorMessage.setCode(split[0]);
            errorMessage.setMessage(split[1]);
        } else {
            errorMessage.setMessage(message);
        }
    }

    private ErrorMessage getErrorMessage(String field, String message) {
        var error = new ErrorMessage();
        error.setField(field);
        mapErrorMessage(error, message);
        return error;
    }

}
