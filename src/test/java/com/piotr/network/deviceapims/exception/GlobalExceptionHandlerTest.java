package com.piotr.network.deviceapims.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.validation.ConstraintViolationException;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void whenHandleBadRequest_thenReturnErrorResponseInvalidRequest() {
        final String errorMessage = "Invalid Request";
        //run tested method
        var testedObject = globalExceptionHandler.handleBadRequest(new InvalidRequestException(HttpStatus.BAD_REQUEST, errorMessage));
        //assertion
        assertEquals(HttpStatus.BAD_REQUEST, testedObject.getStatusCode());
        assertNotNull(testedObject.getBody());
        assertEquals(errorMessage, testedObject.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), testedObject.getBody().getCode());
    }

    @Test
    void whenHandleMethodArgumentNotValidException_thenReturnErrorResponseGenericException() {
        final var exception = Instancio.of(MethodArgumentNotValidException.class)
                .create();
        //run tested method
        var testedObject = globalExceptionHandler.handleMethodArgumentNotValidException(exception);
        //assertion
        assertEquals(HttpStatus.BAD_REQUEST, testedObject.getStatusCode());
        assertNotNull(testedObject.getBody());
        assertEquals("Validation failed", testedObject.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), testedObject.getBody().getCode());
    }

    @Test
    void whenHandleConstraintViolationException_thenReturnErrorResponseConstraintViolationException() {
        final var exception = Instancio.of(ConstraintViolationException.class)
                .create();
        //run tested method
        var testedObject = globalExceptionHandler.handleConstraintViolationException(exception);
        //assertion
        assertEquals(HttpStatus.BAD_REQUEST, testedObject.getStatusCode());
        assertNotNull(testedObject.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), testedObject.getBody().getCode());
    }

    @Test
    void whenHandleHttpMessageNotReadableException_thenReturnJsonSpecificError() {
        var innerCause = new IllegalArgumentException("Unexpected value");
        var jsonMappingException = new JsonMappingException(null, "Outer message", innerCause);
        jsonMappingException.prependPath(new JsonMappingException.Reference(Object.class, "deviceType"));
        jsonMappingException.prependPath(new JsonMappingException.Reference(Object.class, "otherJsonType"));
        final var exception = new HttpMessageNotReadableException("Mock exception message", jsonMappingException);
        //run tested method
        var testedObject = globalExceptionHandler.handleHttpMessageNotReadableException(exception);
        //assertion
        assertEquals(HttpStatus.BAD_REQUEST, testedObject.getStatusCode());
        assertNotNull(testedObject.getBody());
        assertEquals("JSON parser error. Unexpected value for otherJsonType, deviceType", testedObject.getBody().getMessage());
        assertEquals("400", testedObject.getBody().getCode());
    }

    @Test
    void whenHandleHttpMessageNotReadableException_thenReturnHttpMessageError() {
        final var exception = Instancio.of(HttpMessageNotReadableException.class)
                .create();
        //run tested method
        var testedObject = globalExceptionHandler.handleHttpMessageNotReadableException(exception);
        //assertion
        assertEquals(HttpStatus.BAD_REQUEST, testedObject.getStatusCode());
        assertNotNull(testedObject.getBody());
        assertTrue(testedObject.getBody().getMessage().contains("JSON parser error."));
        assertEquals("400", testedObject.getBody().getCode());
    }

    @Test
    void whenHandleDataIntegrityViolationException_thenReturnDataIntegrityViolationException() {
        final var exception = new DataIntegrityViolationException("Other Error");
        //run tested method
        var testedObject = globalExceptionHandler.handleDataIntegrityViolationException(exception);
        //assertion
        assertEquals(HttpStatus.BAD_REQUEST, testedObject.getStatusCode());
        assertNotNull(testedObject.getBody());
        assertEquals("Other Error", testedObject.getBody().getMessage());
        assertEquals("400", testedObject.getBody().getCode());
    }

    @Test
    void whenHandleDataIntegrityViolationException_thenReturnDuplicateKeyException() {
        final var errorMessage = "Duplicate key value violates unique constraint 'device_mac_address_key'";
        var innerCause = new Exception(errorMessage);
        final var exception = new DataIntegrityViolationException("Error executing query: duplicate key", innerCause);
        //run tested method
        var testedObject = globalExceptionHandler.handleDataIntegrityViolationException(exception);
        //assertion
        assertEquals(HttpStatus.BAD_REQUEST, testedObject.getStatusCode());
        assertNotNull(testedObject.getBody());
        assertEquals(errorMessage, testedObject.getBody().getMessage());
        assertEquals("400", testedObject.getBody().getCode());
    }



    @Test
    void whenHandleGenericException_thenReturnErrorResponseGenericException() {
        final String errorMessage = "Generic Exception";
        //run tested method
        var testedObject = globalExceptionHandler.handleGenericException(new Exception(errorMessage));
        //assertion
        assertEquals(HttpStatus.BAD_REQUEST, testedObject.getStatusCode());
        assertNotNull(testedObject.getBody());
        assertEquals(errorMessage, testedObject.getBody().getMessage());
        assertEquals("400", testedObject.getBody().getCode());
    }
}