package ru.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.event.annotations.MaxLength;
import ru.practicum.event.annotations.MinLength;

import java.util.List;

@Data
public class NewCompilationDto {
    private List<Long> events;

    private Boolean pinned;

    @NotBlank(message = "Название подборки должно быть указано")
    @MinLength(value = 1, message = "Минимальная длина названия подборки 1 символ")
    @MaxLength(value = 50, message = "Максимальная длина названия подборки 50 символов")
    private String title;
}
