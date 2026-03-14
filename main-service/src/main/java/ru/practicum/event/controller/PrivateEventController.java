package ru.practicum.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.PatchEventDto;
import ru.practicum.event.service.EventService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> findEventsBy(
            @PathVariable("userId") @Min(1) Long id,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return ResponseEntity.ok(eventService.findEventsBy(id, from, size));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> findEventById(
            @PathVariable @Min(1) Long userId,
            @PathVariable @Min(1) Long eventId) {
        return ResponseEntity.ok(eventService.findEventByIdAndUser(userId, eventId));
    }

    @PostMapping
    public ResponseEntity<EventFullDto> saveNewEvent(
            @PathVariable("userId") @Min(1) Long id,
            @Valid @RequestBody @NotNull NewEventDto newEventDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.saveNewEvent(id, newEventDto));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> patchEventByInitiator(
            @PathVariable @Min(1) Long userId,
            @PathVariable @Min(1) Long eventId,
            @Valid @RequestBody @NotNull PatchEventDto patchEventDto) {
        return ResponseEntity.ok(eventService.patchEventByUser(userId, eventId, patchEventDto));
    }

    // Добавить обработку GET /users/{userId}/events/{eventId}/requests после реализации запросов

    // Добавить обработку PATCH /users/{userId}/events/{eventId}/requests после реализации запросов


}
