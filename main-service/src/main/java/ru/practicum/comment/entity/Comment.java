package ru.practicum.comment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    @Column(name = "commentator_id")
    Long commentatorId;

    @NotNull
    @Column(name = "event_id")
    Long eventId;

    @NotNull
    @PastOrPresent
    @Column(name = "created")
    LocalDateTime created;

    @Column(name = "text")
    @NotNull(message = "текст комментария не может быть null")
    @NotBlank(message = "текст комментария не может быть пустым")
    String text;
}
