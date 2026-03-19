package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.service.CommentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events/{eventId}/comments/{commentatorId}")
@Validated
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping
    public CommentDto create(@Valid @RequestBody CommentRequestDto commentRequestDto,
                             @NotNull @Positive @PathVariable("eventId") Long eventId,
                             @NotNull @Positive @PathVariable("commentatorId") Long commentatorId) {
         return commentService.create(commentRequestDto, commentatorId, eventId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@Valid @RequestBody CommentRequestDto commentRequestDto,
                             @NotNull @Positive @PathVariable("eventId") Long eventId,
                             @NotNull @Positive @PathVariable("commentatorId") Long commentatorId,
                             @NotNull @Positive @PathVariable("commentId") Long commentId) {
        return commentService.update(commentRequestDto, eventId, commentatorId, commentId);
    }

    @DeleteMapping("/{commentId}")
    public void delete(@NotNull @Positive @PathVariable("eventId") Long eventId,
                             @NotNull @Positive @PathVariable("commentatorId") Long commentatorId,
                             @NotNull @Positive @PathVariable("commentId") Long commentId) {
        commentService.delete(eventId, commentId, commentatorId);
    }
}