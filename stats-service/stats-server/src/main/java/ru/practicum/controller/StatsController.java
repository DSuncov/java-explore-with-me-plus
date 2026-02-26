package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.service.StatsService;

@RestController
@Validated
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;


}
