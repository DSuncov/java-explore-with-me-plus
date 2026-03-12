package ru.practicum.event.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.entity.Category;
import ru.practicum.event.enums.State;
import ru.practicum.user.entity.User;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Setter
@Table(name = "events")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Lob
    @Column(name = "annotation")
    String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @Column(name = "created_on")
    LocalDateTime createdOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    User initiator;

    @Embedded
    Location location;

    @Column(name = "event_date", nullable = false)
    LocalDateTime eventDate;

    @Lob
    @Column(name = "description", nullable = false)
    String description;

    @Column(name = "paid")
    Boolean paid;

    @Column(name = "participant_limit")
    Integer participantLimit;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    State state;

    @Column(name = "title", nullable = false)
    String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(annotation, event.annotation)
                && Objects.equals(createdOn, event.createdOn) && Objects.equals(location, event.location)
                && Objects.equals(eventDate, event.eventDate) && Objects.equals(description, event.description)
                && Objects.equals(paid, event.paid) && Objects.equals(participantLimit, event.participantLimit)
                && Objects.equals(publishedOn, event.publishedOn) && Objects.equals(requestModeration, event.requestModeration)
                && state == event.state && Objects.equals(title, event.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotation, createdOn, location, eventDate, description, paid,
                participantLimit, publishedOn, requestModeration, state, title);
    }
}
