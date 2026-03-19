package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.service.CommentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events/{eventId}/comments/{commentatorId}")
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping
    public CommentDto create(@RequestBody CommentRequestDto commentRequestDto,
                             @PathVariable("eventId") Long eventId,
                             @PathVariable("commentatorId") Long commentatorId) {
         return commentService.create(commentRequestDto, commentatorId, eventId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@RequestBody CommentRequestDto commentRequestDto,
                             @PathVariable("eventId") Long eventId,
                             @PathVariable("commentatorId") Long commentatorId,
                             @PathVariable("commentId") Long commentId) {
        return commentService.update(commentRequestDto, eventId, commentatorId, commentId);
    }

    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable("eventId") Long eventId,
                             @PathVariable("commentatorId") Long commentatorId,
                             @PathVariable("commentId") Long commentId) {
        commentService.delete(eventId, commentId, commentatorId);
    }
}