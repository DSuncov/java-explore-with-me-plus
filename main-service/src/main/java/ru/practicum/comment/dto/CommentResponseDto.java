package ru.practicum.comment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponseDto { //возвращаем это дто когда пользователь запросил все комментарии кого-либо эвента

    Long commentatorId;
    LocalDateTime created;
    String text;
}
