package com.urlShortener.urlShortener;

import com.urlShortener.urlShortener.exceptions.ShortUrlExpiredException;
import com.urlShortener.urlShortener.exceptions.ShortUrlNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ShortUrlNotFoundException.class)
    String handleShortUrlNotFoundException(ShortUrlNotFoundException exception) {
        logger.error("Short URL not found exception occurred : {}", exception.getMessage());
        return "errors/404";
    }

    @ExceptionHandler(ShortUrlExpiredException.class)
    String handleShortUrlExpiredException(ShortUrlExpiredException exception) {
        logger.error("Short URL expired exception occurred : {}", exception.getMessage());
        return "errors/expired";
    }

    @ExceptionHandler(Exception.class)
    String handleGenericException(Exception exception) {
        logger.error("An unexpected error occurred : {}", exception.getMessage());
        return "errors/500";
    }
}
