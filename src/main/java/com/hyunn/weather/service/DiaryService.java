package com.hyunn.weather.service;

import com.hyunn.weather.domain.DateWeather;
import com.hyunn.weather.domain.Diary;
import com.hyunn.weather.error.InvalidDateException;
import com.hyunn.weather.repository.DateWeatherRepository;
import com.hyunn.weather.repository.DiaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DiaryService {
    private static final Logger logger = LoggerFactory.getLogger(DiaryService.class);
    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;
    private final OpenWeatherService openWeatherService;
    @Value("${openweathermap.key}")
    private String apiKey;

    public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository,
                        OpenWeatherService openWeatherService) {
        this.diaryRepository = diaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
        this.openWeatherService = openWeatherService;
    }

    @Transactional
    public void createDiary(LocalDate date, String text) {
        logger.info("started to create diary");
        // 날씨 데이터 가져오기 (DB/API 에서 가져오기 )
        DateWeather dateWeather = getDateWeather(date);

        Diary nowDiary = new Diary();
        nowDiary.setDateWeather(dateWeather);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
        logger.info("end of to create diary");
    }

    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
        if (dateWeatherListFromDB.isEmpty()) {
            logger.info("DB에 날씨 데이터가 저장되어 있지 않음");
            return openWeatherService.getWeatherFromApi();
        } else {
            return dateWeatherListFromDB.get(0);
        }
    }

    public List<Diary> readDiary(LocalDate date) {
        logger.debug("read diary");
        if (date.isAfter(LocalDate.ofYearDay(3050, 1))) {
            throw new InvalidDateException();
        }
        return diaryRepository.findAllByDate(date);
    }

    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    @Transactional
    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
    }

    @Transactional
    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }
}
