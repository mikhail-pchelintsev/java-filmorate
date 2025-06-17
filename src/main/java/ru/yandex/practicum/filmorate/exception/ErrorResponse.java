package ru.yandex.practicum.filmorate.exception;

public class ErrorResponse {

    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    // геттер
    public String getError() {
        return error;
    }

    // сеттер (если нужно)
    public void setError(String error) {
        this.error = error;
    }
}

