package ru.practicum.event.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.event.entity.Event;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class EventSpecification {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Specification<Event> hasUsers(List<Long> users) {
        if (users == null || users.isEmpty()) {
            return null;
        }

        return ((root, query, criteriaBuilder) -> root.get("initiator.id").in(users));
    }

    public static Specification<Event> hasText(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        String pattern = "%" + text.toLowerCase() + "%";

        Specification<Event> byAnnotation = ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), pattern));

        Specification<Event> byDescription = ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern));

        return byAnnotation.or(byDescription);
    }

    public static Specification<Event> hasCategories(List<Long> categories) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }

        return ((root, query, criteriaBuilder) -> root.get("category.id").in(categories));
    }

    public static Specification<Event> hasStates(List<String> states) {
        if (states == null || states.isEmpty()) {
            return null;
        }

        return ((root, query, criteriaBuilder) -> root.get("state").in(states));
    }

    public static Specification<Event> isPaid(Boolean paid) {
        if (paid == null) {
            return null;
        }

        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("paid"), paid));
    }

    public static Specification<Event> hasRange(String rangeStart, String rangeEnd) {
        if (rangeStart == null & rangeEnd == null) {
            return null;
        }

        if (LocalDateTime.parse(rangeEnd, formatter).isBefore(LocalDateTime.parse(rangeStart, formatter))) {
            throw new ValidationException("Дата старта не может быть позже даты окончания.");
        }

        return ((root, query, criteriaBuilder) -> {
            // Задана только верхняя граница (rangeEnd)
            if ((rangeEnd != null && !rangeEnd.isBlank()) && (rangeStart == null || rangeStart.isBlank())) {
                // Находим события от текущего времени до rangeEnd
                return criteriaBuilder.between(root.get("createdOn"), LocalDateTime.now(), LocalDateTime.parse(rangeEnd, formatter));
            }

            // Задана только нижняя граница (rangeStart)
            if ((rangeStart != null && !rangeStart.isBlank()) && (rangeEnd == null || rangeEnd.isBlank())) {
                // Находим события после rangeStart до текущего времени
                return criteriaBuilder.between(root.get("createdOn"), LocalDateTime.parse(rangeStart, formatter), LocalDateTime.now());
            }

            // Если диапазон дат не задан
            if ((rangeStart == null || rangeStart.isBlank()) && (rangeEnd == null || rangeEnd.isBlank())) {
                // Находим события после текущего времени
                return criteriaBuilder.greaterThan(root.get("createdOn"), LocalDateTime.now());
            }
            // Если диапазон дат задан (есть rangeStart и rangeEnd)
            return criteriaBuilder.between(root.get("createdOn"), LocalDateTime.parse(rangeStart, formatter), LocalDateTime.parse(rangeEnd, formatter));
            });
    }

    public static Specification<Event> isOnlyAvailable(Boolean onlyAvailable) {
        if (onlyAvailable == null) {
            return null;
        }

        if (onlyAvailable) {
            return ((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThan(root.get("participantLimit"), root.get("confirmedRequests")));
        }

        return null;
    }
}
