package zerobase.weather.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.domain.Diary;
import zerobase.weather.error.WeatherException;
import zerobase.weather.service.DiaryService;
import zerobase.weather.type.ErrorCode;

import java.time.LocalDate;
import java.util.List;

@RestController
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @ApiOperation("선택한 날짜의 일기 데이터를 DB에 저장합니다.")
    @PostMapping("/create/diary")
    void createDiary(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) @ApiParam(value = "저장할 날짜", example = "2024-01-01") LocalDate date
                    , @RequestBody @ApiParam(value = "일기 내용", example = "일기 내용") String text) {
        if(date.isEqual(LocalDate.of(2024, 1, 1)) || text.isEmpty() || text.compareTo("일기 내용") == 0) {
            throw new WeatherException(ErrorCode.IN_DATE_OR_IN_TEXT_EMPTY);
        }
        diaryService.createDiary(date, text);
    }

    @ApiOperation("선택한 날짜의 모든 일기 데이터를 가져옵니다.")
    @GetMapping("/read/diary")
    List<Diary> readDiary(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) @ApiParam(value = "조회할 날짜", example = "2024-01-01")LocalDate date) {
        if((date.isAfter(LocalDate.of(2024,1,1))) && date.isBefore(LocalDate.of(2024, 12, 31))) {
            throw new WeatherException(ErrorCode.DATE_TOO_FAR_IN_THE_PAST_OR_IN_THE_FUTURE);
        }
        return diaryService.readDiary(date);
    }

    @ApiOperation("선택한 기간 중의 모든 일기 데이터를 가져옵니다.")
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) @ApiParam(value = "조회할 기간의 첫번째 날", example = "2024-01-01") LocalDate startDate,
                            @RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) @ApiParam(value = "조회 기간의 마지막 날", example = "2024-01-01") LocalDate endDate) {

        if((startDate.isAfter(LocalDate.of(2024,1,1))) && endDate.isBefore(LocalDate.of(2024, 12, 31))) {
            throw new WeatherException(ErrorCode.DATE_TOO_FAR_IN_THE_PAST_OR_IN_THE_FUTURE);
        }
        return diaryService.readDiaries(startDate, endDate);
    }

    @ApiOperation("선택한 날짜의 일기를 수정합니다.")
    @PutMapping("/update/diary")
    void updateDiary (@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text) {
        if(date.isEqual(LocalDate.of(2024, 1, 1)) || text.isEmpty() || text.compareTo("일기 내용") == 0) {
            throw new WeatherException(ErrorCode.IN_DATE_OR_IN_TEXT_EMPTY);
        }
        diaryService.updateDiary(date, text);
    }

    @ApiOperation("선택한 날짜의 모든 일기 데이터를 삭제합니다.")
    @DeleteMapping ("/delete/diary")
    void updateDiary (@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate date) {
        if(date.isEqual(LocalDate.of(2024, 1, 1))) {
            throw new WeatherException(ErrorCode.IN_DATE_EMPTY);
        }
        diaryService.deleteDiary(date);
    }
}
