package ru.practicum.event.dto;

import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.annotations.FutureOrPresent;
import ru.practicum.event.annotations.MaxLength;
import ru.practicum.event.annotations.MinLength;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PatchEventDto {

    @MinLength(value = 20, message = "Минимальная длина аннотации 20 символов.")
    @MaxLength(value = 2000, message = "Максимальная длина аннотации 2000 символов.")
    String annotation;

    @Positive
    Long category;

    @MinLength(value = 20, message = "Минимальная длина описания 20 символов.")
    @MaxLength(value = 7000, message = "Максимальная длина описания 7000 символов.")
    String description;

    @FutureOrPresent
    String eventDate;

    LocationCreateDto location;
    Boolean paid;

    @Positive
    Integer participantLimit;

    Boolean requestModeration;
    String stateAction;

    @MinLength(value = 3, message = "Минимальная длина заголовка 3 символа.")
    @MaxLength(value = 120, message = "Максимальная длина заголовка 120 символов.")
    String title;
}
