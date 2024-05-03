package com.hyunn.weather.controller;

import com.hyunn.weather.domain.Diary;
import com.hyunn.weather.service.DiaryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @ApiOperation(value = "일기 텍스트와 날씨를 이용해서 DB에 일기 저장합니다.", notes = "")
    @PostMapping("/create/diary")
    void createDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                     @RequestBody String text) {
        diaryService.createDiary(date, text);
    }

    @ApiOperation(value = "선택한 날짜의 모든 일기 데이터를 가져옵니다.", notes = "")
    @GetMapping("/read/diary")
    List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return diaryService.readDiary(date);
    }

    @ApiOperation(value = "선택한 기간 중의 모든 일기 데이터를 가져옵니다.", notes = "")
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                            @ApiParam(value = "조회할 기간의 " + "첫번째 날", example = "2020-02-22") LocalDate startDate,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                            @ApiParam(value = "조회할 기간의 " + "마지막 날", example = "2020-02-22") LocalDate endDate) {
        return diaryService.readDiaries(startDate, endDate);
    }

    @ApiOperation(value = "선택한 날짜의 첫 번째 일기의 텍스트를 수정합니다.", notes = "")
    @PutMapping("/update/diary")
    void updateDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                     @ApiParam(value = "수정할 날짜", example = "2020-02-22") LocalDate date,
                     @RequestBody String text) {
        diaryService.updateDiary(date, text);
    }

    @ApiOperation(value = "선택한 날짜의 모든 일기를 삭제합니다.", notes = "")
    @DeleteMapping("/delete/diary")
    void deleteDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                     @ApiParam(value = "삭제할 날짜", example = "2020-02-22") LocalDate date) {
        diaryService.deleteDiary(date);
    }
}
