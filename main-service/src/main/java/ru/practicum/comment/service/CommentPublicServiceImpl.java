package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.entity.Comment;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentPublicServiceImpl implements CommentPublicService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDto create(CommentRequestDto commentRequestDto, Long commentatorId, Long eventId) {
        validateCommentCreate(commentRequestDto, commentatorId, eventId);

        Comment commentCreate = Comment.builder()
                .commentatorId(commentatorId)
                .eventId(eventId)
                .created(LocalDateTime.now())
                .text(commentRequestDto.getText())
                .build();

        Comment commentSaved = commentRepository.save(commentCreate);

        return commentMapper.toCommentDto(commentSaved);
    }

    @Override
    @Transactional
    public CommentDto update(CommentRequestDto commentRequestDto,
                             Long eventId,
                             Long commentatorId,
                             Long commentId) {
        validateCommentUpdate(commentRequestDto, eventId, commentatorId, commentId);

        Comment commentFindById = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("комментарий с id = " + commentId + "не найден")
        );

        if (!commentFindById.getCommentatorId().equals(commentatorId)) {
            throw new ConflictException("редактировать комментарий может только его автор");
        }

        commentFindById.setText(commentRequestDto.getText());
        Comment commentUpdate = commentRepository.save(commentFindById);

        return commentMapper.toCommentDto(commentUpdate);
    }

    @Override
    @Transactional
    public void delete(Long eventId, Long commentId, Long commentatorId) {
        if (commentId == null) {
            throw new ValidationException("id комментария не может быть равным null");
        }

        if (commentatorId == null) {
            throw new ValidationException("id комментатора не может быть равным null");
        }

        if (eventId == null) {
            throw new ValidationException("id мероприятия не может быть равным null");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("мероприятия с id = " + eventId + " не существует");
        }

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("комментария с id = " + commentId + " не существует")
        );

        if (!comment.getCommentatorId().equals(commentatorId)) {
            throw new ConflictException("комментарий может удалять только его автор либо администратор сайта");
        }
        commentRepository.deleteById(commentId);
    }

    private void validateCommentCreate(CommentRequestDto commentRequestDto, Long commentatorId, Long eventId) {
        if (commentRequestDto == null) {
            throw new ValidationException("тело запроса не может равняться null");
        }
        if (commentRequestDto.getText() == null || commentRequestDto.getText().isEmpty()) {
            throw new ValidationException("текст комментария не может быть пустым или равняться null");
        }

        if (commentatorId == null) {
            throw new ValidationException("id комментатора не может быть равным null");
        }
        if (!userRepository.existsById(commentatorId)) {
            throw new NotFoundException("несуществующий пользователь не может оставить комментарий");
        }

        if (eventId == null) {
            throw new ValidationException("id мероприятия не может равняться null");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("к несуществующему мероприятию невозможно оставить комментарий");
        }
    }

    private void validateCommentUpdate(CommentRequestDto commentRequestDto,
                                       Long eventId,
                                       Long commentatorId,
                                       Long commentId) {
        if (commentRequestDto == null) {
            throw new ValidationException("тело запроса не может равняться null");
        }
        if (commentRequestDto.getText() == null || commentRequestDto.getText().isEmpty()) {
            throw new ValidationException("текст комментария не может быть пустым или равняться null");
        }

        if (eventId == null) {
            throw new ValidationException("id мероприятия не может равняться null");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("мероприятия с id = " + eventId + " не существует");
        }

        if (commentatorId == null) {
            throw new ValidationException("id комментатора не может равняться null");
        }
        if (!userRepository.existsById(commentatorId)) {
            throw new NotFoundException("пользователя с id = " + commentatorId + " не существует");
        }

        if (commentId == null) {
            throw new ValidationException("id комментария не может равняться null");
        }
    }
}
