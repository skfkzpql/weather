package com.hyunn.weather.error;

public class OpenWeatherException extends Exception {

    public static class JSONParsingException extends RuntimeException {
        public JSONParsingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class JSONValueException extends RuntimeException {
        public JSONValueException(String message) {
            super(message);
        }
    }
}
