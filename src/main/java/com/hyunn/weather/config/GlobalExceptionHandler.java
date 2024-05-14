package com.hyunn.weather.config;

import com.hyunn.weather.error.OpenWeatherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Exception handleAllExceptions() {
        log.error("error from GlobalExceptionHandler");
        return new Exception();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(OpenWeatherException.class)
    public Exception handleOpenWeatherException(OpenWeatherException ex) {
        log.error("OpenWeatherException occurred", ex);
        return ex;
    }
}
