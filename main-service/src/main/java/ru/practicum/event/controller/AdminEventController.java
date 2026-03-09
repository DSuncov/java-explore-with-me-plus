package ru.practicum.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.PatchEventDto;
import ru.practicum.event.entityparam.AdminEventParam;
import ru.practicum.event.service.EventService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> findEventsBy(
            @RequestParam(value = "users", required = false) List<Long> users,
            @RequestParam(value = "states", required = false) List<String> states,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "rangeStart", required = false) String rangeStart,
            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        var param = new AdminEventParam(users, states, categories, rangeStart, rangeEnd, from, size);
        return ResponseEntity.ok(eventService.findEventsBy(param));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> patchEvent(
            @PathVariable("eventId") @Min(1) Long id,
            @Valid @RequestBody @NotNull PatchEventDto patchEventDto) {
        return ResponseEntity.ok(eventService.patchEvent(id, patchEventDto));
    }
}
