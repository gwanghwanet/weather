package zerobase.weather.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import zerobase.weather.type.ErrorCode;

@Getter
@AllArgsConstructor
public class WeatherException extends RuntimeException {
    private static final String MESSAGE = "너무 과거 혹은 미래의 날짜입니다.";

    private ErrorCode errorCode;
    private String errorMessage;

    public WeatherException() {
        super(MESSAGE);
    }

    public WeatherException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getErrorMessage();
    }
}
