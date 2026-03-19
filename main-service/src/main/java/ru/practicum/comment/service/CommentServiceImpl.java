package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.entity.Comment;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.entity.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.entity.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDto create(CommentRequestDto commentRequestDto, Long commentatorId, Long eventId) {
        log.info("Создание комментария: commentatorId={}, eventId={}, text={}",
                commentatorId, eventId, commentRequestDto != null ? commentRequestDto.getText() : null);

        validateCommentCreate(commentRequestDto, commentatorId, eventId);

        User commentator = userRepository.findById(commentatorId).orElseThrow(() -> {
            log.warn("Пользователь с id={} не найден при создании комментария", commentatorId);
            return new NotFoundException("Пользователь с id = " + commentatorId + " не найден");
        });

        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("Событие с id={} не найдено при создании комментария", eventId);
            return new NotFoundException("мероприятия с id = " + eventId + " не существует");
        });

        Comment commentCreate = Comment.builder()
                .commentator(commentator)
                .event(event)
                .created(LocalDateTime.now())
                .text(commentRequestDto.getText())
                .build();

        Comment commentSaved = commentRepository.save(commentCreate);
        log.info("Комментарий успешно создан с id: {}", commentSaved.getId());

        return commentMapper.toCommentDto(commentSaved);
    }

    @Override
    @Transactional
    public CommentDto update(CommentRequestDto commentRequestDto,
                             Long eventId,
                             Long commentatorId,
                             Long commentId) {
        log.info("Обновление комментария: commentId={}, eventId={}, commentatorId={}, text={}",
                commentId, eventId, commentatorId, commentRequestDto != null ? commentRequestDto.getText() : null);
        validateCommentUpdate(commentRequestDto, eventId, commentatorId, commentId);

        Comment commentFindById = commentRepository.findById(commentId).orElseThrow(() -> {
            log.warn("Комментарий с id={} не найден для обновления", commentId);
            return new NotFoundException("комментарий с id = " + commentId + " не найден");
        });

        if (!commentFindById.getCommentator().getId().equals(commentatorId)) {
            log.warn("Попытка редактирования чужого комментария: commentId={}, commentatorId={}, автор={}",
                    commentId, commentatorId, commentFindById.getCommentator().getId());
            throw new ConflictException("редактировать комментарий может только его автор");
        }

        commentFindById.setText(commentRequestDto.getText());
        Comment commentUpdate = commentRepository.save(commentFindById);
        log.info("Комментарий с id={} успешно обновлён", commentUpdate.getId());

        return commentMapper.toCommentDto(commentUpdate);
    }

    @Override
    @Transactional
    public void delete(Long eventId, Long commentId, Long userId) {
        log.info("Удаление комментария пользователем: eventId={}, commentId={}, userId={}",
                eventId, commentId, userId);

        if (commentId == null) {
            log.warn("Попытка удаления с null commentId");
            throw new ValidationException("id комментария не может быть равным null");
        }

        if (userId == null) {
            log.warn("Попытка удаления с null userId");
            throw new ValidationException("id пользователя не может быть равным null");
        }

        if (eventId == null) {
            log.warn("Попытка удаления с null eventId");
            throw new ValidationException("id мероприятия не может быть равным null");
        }

        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("Событие с id={} не найдено при удалении комментария", eventId);
            return new NotFoundException("мероприятия с id = " + eventId + " не существует");
        });

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            log.warn("Комментарий с id={} не найден при удалении", commentId);
            return new NotFoundException("комментария с id = " + commentId + " не существует");
        });

        if (!eventId.equals(comment.getEvent().getId())) {
            log.warn("Комментарий id={} не относится к событию id={}", commentId, eventId);
            throw new ConflictException("Комментарий с id = " + commentId + " не относится к событию с id = " + eventId);
        }

        if (!comment.getCommentator().getId().equals(userId) && !event.getInitiator().getId().equals(userId)) {
            log.warn("Пользователь id={} не является автором и не инициатор события для комментария id={}",
                    userId, commentId);
            throw new ConflictException("комментарий может удалять только его автор или инициатор события");
        }
        commentRepository.deleteById(commentId);
        log.info("Комментарий с id={} успешно удалён пользователем id={}", commentId, userId);
    }

    @Override
    @Transactional
    public void deleteByAdmin(Long commentId) {
        log.info("Удаление комментария администратором: commentId={}", commentId);

        if (commentId == null) {
            log.warn("Попытка удаления с null commentId администратором");
            throw new ValidationException("id комментария не может быть равным null");
        }

        if (!commentRepository.existsById(commentId)) {
            log.warn("Комментарий с id={} не найден при удалении администратором", commentId);
            throw new NotFoundException("комментария с id = " + commentId + " не существует");
        }

        commentRepository.deleteById(commentId);
        log.info("Комментарий с id={} успешно удалён администратором", commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getCommentsByEvent(Long eventId, String sortOrder, int from, int size) {
        log.info("Запрос комментариев события: eventId={}, sortOrder={}, from={}, size={}",
                eventId, sortOrder, from, size);

        if (!eventRepository.existsById(eventId)) {
            log.warn("Событие с id={} не найдено при запросе комментариев", eventId);
            throw new NotFoundException("мероприятия с id = " + eventId + " не существует");
        }

        Sort sort = Sort.by("created");
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        Pageable pageable = PageRequest.of(from, size, sort);
        Page<Comment> comments = commentRepository.findAllByEventId(eventId, pageable);
        log.debug("Найдено {} комментариев для события id={}", comments.getNumberOfElements(), eventId);
        return comments.map(commentMapper::toCommentResponseDto);
    }


    private void validateCommentCreate(CommentRequestDto commentRequestDto, Long commentatorId, Long eventId) {
        if (commentRequestDto == null) {
            log.warn("Попытка создать комментарий с null телом запроса");
            throw new ValidationException("тело запроса не может равняться null");
        }

        if (commentRequestDto.getText() == null || commentRequestDto.getText().isEmpty()) {
            log.warn("Попытка создать комментарий с пустым текстом: text={}", commentRequestDto.getText());
            throw new ValidationException("текст комментария не может быть пустым или равняться null");
        }

        if (commentatorId == null) {
            log.warn("Попытка создать комментарий с null commentatorId");
            throw new ValidationException("id комментатора не может быть равным null");
        }

        if (eventId == null) {
            log.warn("Попытка создать комментарий с null eventId");
            throw new ValidationException("id мероприятия не может равняться null");
        }
    }

    private void validateCommentUpdate(CommentRequestDto commentRequestDto,
                                       Long eventId,
                                       Long commentatorId,
                                       Long commentId) {
        if (commentRequestDto == null) {
            log.warn("Попытка обновить комментарий с null телом запроса");
            throw new ValidationException("тело запроса не может равняться null");
        }
        if (commentRequestDto.getText() == null || commentRequestDto.getText().isEmpty()) {
            log.warn("Попытка обновить комментарий с пустым текстом");
            throw new ValidationException("текст комментария не может быть пустым или равняться null");
        }

        if (eventId == null) {
            log.warn("Попытка обновить комментарий с null eventId");
            throw new ValidationException("id мероприятия не может равняться null");
        }

        if (commentatorId == null) {
            log.warn("Попытка обновить комментарий с null commentatorId");
            throw new ValidationException("id комментатора не может равняться null");
        }

        if (commentId == null) {
            log.warn("Попытка обновить комментарий с null commentId");
            throw new ValidationException("id комментария не может равняться null");
        }
    }
}