package com.hyunn.weather.service;

import com.hyunn.weather.domain.DateWeather;
import com.hyunn.weather.error.OpenWeatherException;
import com.hyunn.weather.repository.DateWeatherRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class OpenWeatherService {
    private static final Logger logger = LoggerFactory.getLogger(DiaryService.class);
    private final DateWeatherRepository dateWeatherRepository;

    @Value("${openweathermap.key}")
    private String apiKey;

    public OpenWeatherService(DateWeatherRepository dateWeatherRepository) {
        this.dateWeatherRepository = dateWeatherRepository;
    }


    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate() {
        getWeatherFromApi();
    }

    @Transactional
    public DateWeather getWeatherFromApi() {
        logger.info("API로부터 날씨 데이터 가져오기");
        // OpenWeatherMap 에서 날씨 데이터 가져오기
        String weatherData = getWeatherString();
        // 받아온 날씨 json 파싱하기
        Map<String, Object> parsedWeather = parseWeather(weatherData);
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((Double) parsedWeather.get("temp"));
        dateWeatherRepository.save(dateWeather);
        return dateWeather;
    }

    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            BufferedReader br;

            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            return response.toString();
        } catch (Exception e) {
            logger.error("failed to get response from open weather map");
            return "failed to get response";
        }
    }

    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = getJsonObject(jsonParser, jsonString);

        Map<String, Object> resultMap = new HashMap<>();

        JSONObject mainData = getJsonValue(jsonObject, "main", JSONObject.class);
        resultMap.put("temp", getJsonValue(mainData, "temp", Double.class));

        JSONArray weatherArray = getJsonValue(jsonObject, "weather", JSONArray.class);
        JSONObject weatherData = !weatherArray.isEmpty() ? (JSONObject) weatherArray.get(0) : new JSONObject();

        resultMap.put("main", getJsonValue(weatherData, "main", String.class));
        resultMap.put("icon", getJsonValue(weatherData, "icon", String.class));

        return resultMap;
    }

    private JSONObject getJsonObject(JSONParser jsonParser, String jsonString) {
        try {
            return (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new OpenWeatherException.JSONParsingException("Error parsing JSON: " + e.getMessage(), e);
        }
    }

    private <T> T getJsonValue(JSONObject jsonObject, String key, Class<T> type) {
        if (jsonObject.containsKey(key) && jsonObject.get(key) != null) {
            return type.cast(jsonObject.get(key));
        }
        if (type.equals(JSONObject.class)) {
            return type.cast(new JSONObject());
        }
        if (type.equals(JSONArray.class)) {
            return type.cast(new JSONArray());
        }
        throw new OpenWeatherException.JSONValueException("JSON value for key '" + key + "' is null or missing");
    }
}
