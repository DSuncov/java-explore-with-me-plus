package ru.practicum.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.annotations.DateTimeStart;
import ru.practicum.event.annotations.MaxLength;
import ru.practicum.event.annotations.MinLength;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {

    @NotBlank(message = "Краткое описание события должно быть указано.")
    @MinLength(value = 20, message = "Минимальная длина аннотации 20 символов.")
    @MaxLength(value = 2000, message = "Максимальная длина аннотации 2000 символов.")
    String annotation;

    @NotNull(message = "id категории, к которой относится событие, должно быть указано.")
    Long category;

    @MinLength(value = 20, message = "Минимальная длина описания 20 символов.")
    @MaxLength(value = 7000, message = "Максимальная длина описания 7000 символов.")
    @NotBlank(message = "Полное описание события должно быть указано.")
    String description;

    @NotNull(message = "Дата и время на которые намечено событие должны быть указаны")
    @DateTimeStart(value = 2, message = "Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента")
    //Добавить регулярку
    String eventDate;

    @NotNull(message = "Широта и долгота места проведения события должны быть указаны.")
    LocationCreateDto location;

    Boolean paid;

    @Positive(message = "Количество участников должно быть положительным числом.")
    Integer participantLimit;

    Boolean requestModeration;

    @MinLength(value = 3, message = "Минимальная длина заголовка 3 символа.")
    @MaxLength(value = 120, message = "Максимальная длина заголовка 120 символов.")
    @NotBlank(message = "Заголовок события должен быть указан.")
    String title;
}
