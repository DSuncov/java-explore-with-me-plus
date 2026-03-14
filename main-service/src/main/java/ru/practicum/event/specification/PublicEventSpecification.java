package ru.practicum.event.specification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.event.entity.Event;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PublicEventSpecification implements EventSpecification {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String text;
    private final List<Long> categories;
    private final Boolean paid;
    private final Boolean onlyAvailable;
    private final String rangeStart;
    private final String rangeEnd;

    @PersistenceContext
    private EntityManager entityManager;

    private PublicEventSpecification(Builder builder) {
        this.text = builder.text;
        this.categories = builder.categories;
        this.paid = builder.paid;
        this.onlyAvailable = builder.onlyAvailable;
        this.rangeStart = builder.rangeStart;
        this.rangeEnd = builder.rangeEnd;
    }

    @Override
    public Specification<Event> toSpecification() {
        Specification<Event> spec = Specification.where(null);
        spec.and(hasText());
        spec.and(hasCategories());
        spec.and(hasRange());
        spec.and(isPaid());
        spec.and(isOnlyAvailable());
        return spec;
    }

    private Specification<Event> hasText() {
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

    private Specification<Event> hasCategories() {
        if (categories == null || categories.isEmpty()) {
            return null;
        }

        return ((root, query, criteriaBuilder) -> root.get("category.id").in(categories));
    }

    private Specification<Event> isPaid() {
        if (paid == null) {
            return null;
        }

        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paid"), paid));
    }

    private Specification<Event> hasRange() {
        if (rangeStart == null & rangeEnd == null) {
            return null;
        }

        if (LocalDateTime.parse(rangeEnd, formatter).isBefore(LocalDateTime.parse(rangeStart, formatter))) {
            throw new ValidationException("Дата старта не может быть позже даты окончания.");
        }

        return ((root, query, criteriaBuilder) -> {

            if ((rangeStart != null && !rangeStart.isBlank()) && (rangeEnd != null && !rangeEnd.isBlank())) {
                return criteriaBuilder.between(root.get("createdOn"),
                        LocalDateTime.parse(rangeStart, formatter),
                        LocalDateTime.parse(rangeEnd, formatter));
            }

            if ((rangeStart != null && !rangeStart.isBlank()) && (rangeEnd == null || rangeEnd.isBlank())) {
                return criteriaBuilder.between(root.get("createdOn"),
                        LocalDateTime.parse(rangeStart, formatter),
                        LocalDateTime.now());
            }

            if ((rangeStart == null || rangeStart.isBlank()) && (rangeEnd != null && !rangeEnd.isBlank())) {
                return criteriaBuilder.between(root.get("createdOn"),
                        LocalDateTime.now(),
                        LocalDateTime.parse(rangeEnd, formatter));
            }

            if ((rangeStart == null || rangeStart.isBlank()) && (rangeEnd == null || rangeEnd.isBlank())) {
                return criteriaBuilder.greaterThan(root.get("createdOn"), LocalDateTime.now());
            }

            return criteriaBuilder.greaterThan(root.get("createdOn"), LocalDateTime.now());
        });
    }

    private Specification<Event> isOnlyAvailable() {
        if (onlyAvailable == null) {
            return null;
        }

        if (onlyAvailable) {
            return ((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThan(root.get("participantLimit"), root.get("confirmedRequests")));
        }

        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Slf4j
    @Component
    public static class Builder {
        private String text;
        private List<Long> categories;
        private Boolean paid;
        private Boolean onlyAvailable;
        private String rangeStart;
        private String rangeEnd;

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder categories(List<Long> categories) {
            this.categories = categories;
            return this;
        }

        public Builder paid(Boolean paid) {
            this.paid = paid;
            return this;
        }

        public Builder onlyAvailable(Boolean onlyAvailable) {
            this.onlyAvailable = onlyAvailable;
            return this;
        }

        public Builder rangeStart(String rangeStart) {
            this.rangeStart = rangeStart;
            return this;
        }

        public Builder rangeEnd(String rangeEnd) {
            this.rangeEnd = rangeEnd;
            return this;
        }

        public PublicEventSpecification build() {
            return new PublicEventSpecification(this);
        }
    }
}
