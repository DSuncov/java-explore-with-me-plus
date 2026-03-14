package ru.practicum.event.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.event.entity.Event;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class AdminEventSpecification implements EventSpecification {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final List<Long> users;
    private final List<String> states;
    private final List<Long> categories;
    private final String rangeStart;
    private final String rangeEnd;

    public AdminEventSpecification(Builder builder) {
        this.users = builder.users;
        this.states = builder.states;
        this.categories = builder.categories;
        this.rangeStart = builder.rangeStart;
        this.rangeEnd = builder.rangeEnd;
    }

    @Override
    public Specification<Event> toSpecification() {
        Specification<Event> spec = Specification.where(null);
        spec.and(hasUsers());
        spec.and(hasStates());
        spec.and(hasCategories());
        spec.and(hasRange());
        return spec;
    }

    public Specification<Event> hasUsers() {
        if (users == null || users.isEmpty()) {
            return null;
        }

        return ((root, query, criteriaBuilder) -> root.get("initiator").get("id").in(users));
    }


    public Specification<Event> hasCategories() {
        if (categories == null || categories.isEmpty()) {
            return null;
        }

        return ((root, query, criteriaBuilder) -> root.get("category.id").in(categories));
    }

    public Specification<Event> hasStates() {
        if (states == null || states.isEmpty()) {
            return null;
        }

        return ((root, query, criteriaBuilder) -> root.get("state").in(states));
    }

    public Specification<Event> hasRange() {
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

    public static Builder builder() {
        return new Builder();
    }

    @Component
    public static class Builder {
        private List<Long> users;
        private List<String> states;
        private List<Long> categories;
        private String rangeStart;
        private String rangeEnd;

        public Builder users(List<Long> users) {
            this.users = users;
            return this;
        }

        public Builder states(List<String> states) {
            this.states = states;
            return this;
        }

        public Builder categories(List<Long> categories) {
            this.categories = categories;
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

        public AdminEventSpecification build() {
            return new AdminEventSpecification(this);
        }
    }
}
