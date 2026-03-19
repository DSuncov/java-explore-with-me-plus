package ru.practicum.comment.service;

import org.springframework.data.domain.Page;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;

public interface CommentService {

    CommentDto create(CommentRequestDto commentRequestDto, Long commentatorId, Long eventId);

    CommentDto update(CommentRequestDto commentRequestDto, Long eventId, Long commentatorId, Long commentId);

    void delete(Long eventId, Long commentId, Long commentatorId);

    void deleteByAdmin(Long commentId);

    Page<CommentResponseDto> getCommentsByEvent(Long eventId, String  sort, int from, int size);
}