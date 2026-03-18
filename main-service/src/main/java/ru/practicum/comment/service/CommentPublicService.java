package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentRequestDto;

public interface CommentPublicService {

    CommentDto create(CommentRequestDto commentRequestDto, Long commentatorId, Long eventId);

    CommentDto update(CommentRequestDto commentRequestDto, Long eventId, Long commentatorId, Long commentId);

    void delete(Long eventId, Long commentId, Long commentatorId);
}
