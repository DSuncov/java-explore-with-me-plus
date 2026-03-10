package ru.practicum.compilation.dto;

import lombok.Data;
import ru.practicum.event.annotations.MaxLength;
import ru.practicum.event.annotations.MinLength;

import java.util.List;

@Data
public class UpdateCompilationRequest {
    private List<Long> events;

    private Boolean pinned;

    @MinLength(value = 1, message = "Минимальная длина названия подборки 1 символ")
    @MaxLength(value = 50, message = "Максимальная длина названия подборки 50 символов")
    private String title;

}