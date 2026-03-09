package ru.practicum.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    Long id;
    String title;
    String annotation;
    CategoryDto category;
    Boolean paid;
    String eventDate;
    UserShortDto initiator;
    String description;
    Integer participantLimit;
    String state;
    String createdOn;
    LocationResponseDto location;
    Boolean requestModeration;
    Long confirmedRequests;
    String publishedOn;
    Long views;
}
