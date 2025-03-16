package assignment;

import assignment.exception.InvalidEmployeeCodeException;
import assignment.exception.InvalidTokenException;
import assignment.exception.RecordNotFoundException;
import assignment.exception.ValidationException;
import io.jsonwebtoken.JwtException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTokenException(final InvalidTokenException invalidTokenException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(generateErrorMap(invalidTokenException.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> validationException(final ValidationException validationException) {
        return ResponseEntity.badRequest().body(generateErrorMap(validationException.getMessage()));
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<Map<String, Object>> recordNotFoundException(final RecordNotFoundException recordNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateErrorMap(recordNotFoundException.getMessage()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleValidationErrors(final BindException bindException) {
        List<String> allErrorMessages = bindException.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return ResponseEntity.badRequest().body(generateErrorMap(allErrorMessages));
    }

    @ExceptionHandler(InvalidEmployeeCodeException.class)
    public ResponseEntity<Map<String, Object>> handleRecordNotFound(final InvalidEmployeeCodeException invalidEmployeeCodeException) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(generateErrorMap(invalidEmployeeCodeException.getMessage()));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, Object>> handleJwtException(final JwtException jwtException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(generateErrorMap(jwtException.getMessage()));
    }

    private static Map<String, Object> generateErrorMap(final Object message) {
        return Map.of("message", message);
    }
}
