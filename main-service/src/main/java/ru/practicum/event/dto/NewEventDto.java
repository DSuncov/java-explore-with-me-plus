package ru.practicum.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.annotations.DateTimeStart;
import ru.practicum.event.entity.Location;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {

    @NotBlank(message = "Краткое описание события должно быть указано.")
    @Size(min = 20, max = 2000, message = "Минимальная длина аннотации 20 символов, максимальная - 2000 символов.")
    String annotation;

    @NotNull(message = "id категории, к которой относится событие, должно быть указано.")
    Long category;

    @NotBlank(message = "Полное описание события должно быть указано.")
    @Size(min = 20, max = 2000, message = "Минимальная длина аннотации 20 символов, максимальная - 7000 символов.")
    String description;

    @NotNull(message = "Дата и время на которые намечено событие должны быть указаны")
    @DateTimeStart(value = 2, message = "Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента")
    //Добавить регулярку
    String eventDate;

    @NotNull(message = "Широта и долгота места проведения события должны быть указаны.")
    Location location;

    Boolean paid;

    @Positive(message = "Количество участников должно быть положительным числом.")
    Integer participantLimit;

    Boolean requestModeration;

    @NotBlank(message = "Заголовок события должен быть указан.")
    @Size(min = 3, max = 120, message = "Минимальная длина аннотации 20 символов, максимальная - 2000 символов.")
    String title;
}
