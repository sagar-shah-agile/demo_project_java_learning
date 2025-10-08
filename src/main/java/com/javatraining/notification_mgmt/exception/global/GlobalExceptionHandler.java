package com.javatraining.notification_mgmt.exception.global;

import com.javatraining.notification_mgmt.dto.response.ErrorResponseDto;
import com.javatraining.notification_mgmt.exception.custom.DuplicateEmailException;
import com.javatraining.notification_mgmt.exception.custom.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Utility method to build ErrorResponseDto
    private ErrorResponseDto buildErrorResponse(HttpStatus status, String message, String path) {
        return ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                //.traceId(MDC.get("traceId")) // pull traceId from MDC
                .build();
    }

    // 🔹 Handle all other exceptions (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllExceptions(Exception ex, HttpServletRequest request) {
        ErrorResponseDto error = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Oops...!!! Something went wrong...!!!",
                request.getRequestURI()
        );

        log.error("Unexpected Error: {}", error, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // 🔹 Handle validation errors (@Valid in DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex,
                                                                       HttpServletRequest request)
    {
        String messages = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponseDto error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                messages,
                request.getRequestURI()
        );

        log.error("Validation Error: {}", error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 🔹 Handle resource not found
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFound(UserNotFoundException ex,
                                                                   HttpServletRequest request)
    {
        ErrorResponseDto error = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );

        log.error("User Not Found: {}", error);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 🔹 Handle duplicate email
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateEmail(DuplicateEmailException ex,
                                                                 HttpServletRequest request)
    {
        ErrorResponseDto error = buildErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getRequestURI()
        );

        log.error("Duplicate Email Error: {}", error);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
