package zerobase.weather.type;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다."),
    INVALID_REQUEST("잘못된 요청입니다."),
    IN_DATE_EMPTY("날짜가 비어있습니다."),
    IN_DATE_OR_IN_TEXT_EMPTY("날짜 또는 텍스트가 비어있습니다."),
    DATE_TOO_FAR_IN_THE_PAST_OR_IN_THE_FUTURE("너무 과거 혹은 미래의 날짜 입니다."),
    GET_OPENWEATHERMAP_API_INFO_ERROR("OPENWEATHERMAP API 정보를 가져오기 오류가 발생했습니다.");

    private String errorMessage;
}
