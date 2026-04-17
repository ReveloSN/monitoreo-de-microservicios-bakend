package com.revelosn.proxymonitoring.exception;

import com.revelosn.proxymonitoring.dto.ErrorResponse;
import com.revelosn.proxymonitoring.util.RequestContextUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException exception) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), "RESOURCE_NOT_FOUND", Map.of());
    }

    @ExceptionHandler({UnsupportedServiceOperationException.class, IllegalArgumentException.class, InsufficientStockException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), "BAD_REQUEST", Map.of());
    }

    @ExceptionHandler(SimulatedPaymentFailureException.class)
    public ResponseEntity<ErrorResponse> handleSimulatedFailure(SimulatedPaymentFailureException exception) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage(), "SIMULATED_PAYMENT_FAILURE", Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        LinkedHashMap<String, Object> details = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return buildResponse(HttpStatus.BAD_REQUEST, "La solicitud enviada no es válida", "VALIDATION_ERROR", details);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno al procesar la solicitud", "INTERNAL_ERROR", Map.of());
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status,
                                                        String message,
                                                        String errorCode,
                                                        Map<String, Object> details) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.of(message, RequestContextUtils.currentRequestId(), errorCode, details));
    }
}

