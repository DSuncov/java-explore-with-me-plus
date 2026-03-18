package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.service.CommentPublicService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events/{eventId}/comments/{commentatorId}")
public class CommentPublicController {

    private final CommentPublicService commentPublicService;

    @PostMapping
    public CommentDto create(@RequestBody CommentRequestDto commentRequestDto,
                             @PathVariable("eventId") Long eventId,
                             @PathVariable("commentatorId") Long commentatorId) {
         return commentPublicService.create(commentRequestDto, commentatorId, eventId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@RequestBody CommentRequestDto commentRequestDto,
                             @PathVariable("eventId") Long eventId,
                             @PathVariable("commentatorId") Long commentatorId,
                             @PathVariable("commentId") Long commentId) {
        return commentPublicService.update(commentRequestDto, eventId, commentatorId, commentId);
    }

    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable("eventId") Long eventId,
                             @PathVariable("commentatorId") Long commentatorId,
                             @PathVariable("commentId") Long commentId) {
        commentPublicService.delete(eventId, commentId, commentatorId);
    }
}
