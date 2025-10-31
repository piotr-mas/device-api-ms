package com.piotr.network.deviceapims.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.piotr.network.deviceapims.generated.model.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String FAILED_VALIDATION = "Validation failed";

    /**
     * handleBadRequest
     * @param exception thrown during processing
     * @return ErrorResponse
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(InvalidRequestException exception) {
        var errorResponse = new ErrorResponse(exception.getStatus().toString(), exception.getMessage());
        return  ResponseEntity.status(exception.getStatus()).body(errorResponse);
    }

    /**
     * handleMethodArgumentNotValidException
     * @param exception thrown during processing
     * @return ErrorResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String errorMessage = Optional.ofNullable(exception)
                .map(MethodArgumentNotValidException::getBindingResult)
                .map(Errors::getFieldErrors)
                .filter(fieldErrors -> !fieldErrors.isEmpty())
                .map(fieldErrors -> fieldErrors.stream()
                        .map(err -> err.getField() + ": " + err.getDefaultMessage())
                        .findFirst()
                        .orElse(FAILED_VALIDATION))
                .orElse(FAILED_VALIDATION);
        var statusCode = exception != null ? exception.getStatusCode().toString() : "400";
        var errorResponse = new ErrorResponse(statusCode, errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * handleConstraintViolationException
     * @param exception thrown during processing
     * @return ErrorResponse
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException exception) {
        var errorMessage = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .findFirst()
                .orElse(FAILED_VALIDATION);

        // Map it to your OpenAPI ErrorResponse model
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * handleHttpMessageNotReadableException
     * @param exception thrown during processing
     * @return ErrorResponse
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        var errorMessage = new StringBuilder("JSON parser error.");
        Throwable cause = exception.getCause();
        if (cause instanceof JsonMappingException ex) {
            errorMessage.append(" ").append(ex.getCause().getMessage());
            var isFor = false;
            for (JsonMappingException.Reference ref : ex.getPath()) {
                if (!isFor) {
                    errorMessage.append(" for ").append(ref.getFieldName());
                    isFor = true;
                } else {
                    errorMessage.append(", ").append(ref.getFieldName());
                }
            }

        } else {
            errorMessage.append(exception.getMessage());
        }
        var errorResponse = new ErrorResponse("400", errorMessage.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * handleDataIntegrityViolationException
     * @param exception thrown during processing
     * @return ErrorResponse
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException (DataIntegrityViolationException exception) {
        var errorMessage = "";
        if (exception.getMessage().contains("duplicate key")) {
            errorMessage = exception.getCause().getMessage();
        } else {
            errorMessage =  exception.getMessage();
        }
        var errorResponse = new ErrorResponse("400", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * handleGenericException
     * @param exception thrown during processing
     * @return ErrorResponse
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {
        var errorResponse = new ErrorResponse("400", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
