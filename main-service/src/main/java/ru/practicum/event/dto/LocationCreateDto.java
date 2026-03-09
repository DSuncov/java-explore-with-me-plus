package ru.practicum.event.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationCreateDto {

    @NotNull(message = "Широта должна быть указана.")
    Float lat;

    @NotNull(message = "Долгота должна быть указана.")
    Float lon;
}
