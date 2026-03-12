package ru.practicum.event.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.annotations.FutureOrPresent;
import ru.practicum.event.entity.Location;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PatchEventDto {

    @Size(min = 20, max = 2000, message = "Минимальная длина аннотации 20 символов, максимальная - 2000 символов.")
    String annotation;

    @Positive
    Long category;

    @Size(min = 20, max = 7000, message = "Минимальная длина аннотации 20 символов, максимальная - 2000 символов.")
    String description;

    @FutureOrPresent
    String eventDate;

    Location location;
    Boolean paid;

    @Positive
    Integer participantLimit;

    Boolean requestModeration;
    String stateAction;

    @Size(min = 3, max = 120, message = "Минимальная длина аннотации 20 символов, максимальная - 2000 символов.")
    String title;
}
