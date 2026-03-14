package ru.practicum.event.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.category.entity.Category;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.event.dto.*;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.Location;
import ru.practicum.event.enums.State;
import ru.practicum.user.entity.User;
import ru.practicum.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventMapper {

    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Добавить views и confirmedRequests
    public EventFullDto toFullDto(Event event) {
        EventFullDto eventFullDto = EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .description(event.getDescription())
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .location(toDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .confirmedRequests(event.getConfirmedRequests())
                .requestModeration(event.getRequestModeration())
                .state(String.valueOf(event.getState()))
                .title(event.getTitle())
                .build();

        if (event.getCreatedOn() != null) {
            eventFullDto.setCreatedOn(formatter.format(event.getCreatedOn()));
        }

        if (event.getEventDate() != null) {
            eventFullDto.setEventDate(formatter.format(event.getEventDate()));
        }

        if (event.getPublishedOn() != null) {
            eventFullDto.setPublishedOn(formatter.format(event.getPublishedOn()));
        }

        return eventFullDto;
    }

    // Добавить views и confirmedRequests
    public EventShortDto toShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(formatter.format(event.getEventDate()))
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .confirmedRequests(event.getConfirmedRequests())
                .build();
    }

    public Event toEntity(NewEventDto newEventDto, User user, Category category, Location location) {
        Event event = Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .initiator(user)
                .location(location)
                .description(newEventDto.getDescription())
                .createdOn(LocalDateTime.now())
                .eventDate(LocalDateTime.parse(newEventDto.getEventDate(), formatter))
                .state(State.PENDING)
                .title(newEventDto.getTitle())
                .build();

        if (newEventDto.getPaid() != null) {
            event.setPaid(newEventDto.getPaid());
        } else {
            event.setPaid(false);
        }

        if (newEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(newEventDto.getParticipantLimit());
        } else {
            event.setParticipantLimit(0);
        }

        if (newEventDto.getRequestModeration() != null) {
            event.setRequestModeration(newEventDto.getRequestModeration());
        } else {
            event.setRequestModeration(true);
        }

        return event;
    }

    public LocationResponseDto toDto(Location location) {
        LocationResponseDto locationDto = new LocationResponseDto();
        locationDto.setId(location.getId());
        locationDto.setLat(location.getLat());
        locationDto.setLon(location.getLon());
        return locationDto;
    }

    public Location toEntity(LocationCreateDto locationCreateDto) {
        Location location = new Location();
        location.setLat(locationCreateDto.getLat());
        location.setLon(locationCreateDto.getLon());
        return location;
    }
}
