package com.piotr.network.deviceapims.exception;

import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidRequestException extends RuntimeException {

    private final HttpStatus  status;

    public InvalidRequestException(HttpStatus status) {
        this.status = status;
    }
}
