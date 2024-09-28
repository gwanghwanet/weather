package zerobase.weather.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zerobase.weather.domain.ErrorResponse;
import zerobase.weather.error.WeatherException;
import zerobase.weather.type.ErrorCode;

// 전역 예외처리를 핸들러 할 때 사용하는 어노테이션
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(WeatherException.class)
    public ErrorResponse handleAllException(WeatherException e) {
        logger.error("{} is occurred.", e.getErrorCode());

        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        logger.error("DataIntegrityViolationException is occurred.");

        return new ErrorResponse(ErrorCode.INVALID_REQUEST, ErrorCode.INVALID_REQUEST.getErrorMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error("MethodArgumentNotValidException is occurred.");

        return new ErrorResponse(ErrorCode.INVALID_REQUEST, ErrorCode.INVALID_REQUEST.getErrorMessage());
    }
}
