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

    @ApiOperation("������ ��¥�� �ϱ� �����͸� DB�� �����մϴ�.")
    @PostMapping("/create/diary")
    void createDiary(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) @ApiParam(value = "������ ��¥", example = "2024-01-01") LocalDate date
                    , @RequestBody @ApiParam(value = "�ϱ� ����", example = "�ϱ� ����") String text) {
        if(date.isEqual(LocalDate.of(2024, 1, 1)) || text.isEmpty() || text.compareTo("�ϱ� ����") == 0) {
            throw new WeatherException(ErrorCode.IN_DATE_OR_IN_TEXT_EMPTY);
        }
        diaryService.createDiary(date, text);
    }

    @ApiOperation("������ ��¥�� ��� �ϱ� �����͸� �����ɴϴ�.")
    @GetMapping("/read/diary")
    List<Diary> readDiary(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) @ApiParam(value = "��ȸ�� ��¥", example = "2024-01-01")LocalDate date) {
        if((date.isAfter(LocalDate.of(2024,1,1))) && date.isBefore(LocalDate.of(2024, 12, 31))) {
            throw new WeatherException(ErrorCode.DATE_TOO_FAR_IN_THE_PAST_OR_IN_THE_FUTURE);
        }
        return diaryService.readDiary(date);
    }

    @ApiOperation("������ �Ⱓ ���� ��� �ϱ� �����͸� �����ɴϴ�.")
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) @ApiParam(value = "��ȸ�� �Ⱓ�� ù��° ��", example = "2024-01-01") LocalDate startDate,
                            @RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) @ApiParam(value = "��ȸ �Ⱓ�� ������ ��", example = "2024-01-01") LocalDate endDate) {

        if((startDate.isAfter(LocalDate.of(2024,1,1))) && endDate.isBefore(LocalDate.of(2024, 12, 31))) {
            throw new WeatherException(ErrorCode.DATE_TOO_FAR_IN_THE_PAST_OR_IN_THE_FUTURE);
        }
        return diaryService.readDiaries(startDate, endDate);
    }

    @ApiOperation("������ ��¥�� �ϱ⸦ �����մϴ�.")
    @PutMapping("/update/diary")
    void updateDiary (@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text) {
        if(date.isEqual(LocalDate.of(2024, 1, 1)) || text.isEmpty() || text.compareTo("�ϱ� ����") == 0) {
            throw new WeatherException(ErrorCode.IN_DATE_OR_IN_TEXT_EMPTY);
        }
        diaryService.updateDiary(date, text);
    }

    @ApiOperation("������ ��¥�� ��� �ϱ� �����͸� �����մϴ�.")
    @DeleteMapping ("/delete/diary")
    void updateDiary (@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate date) {
        if(date.isEqual(LocalDate.of(2024, 1, 1))) {
            throw new WeatherException(ErrorCode.IN_DATE_EMPTY);
        }
        diaryService.deleteDiary(date);
    }
}
