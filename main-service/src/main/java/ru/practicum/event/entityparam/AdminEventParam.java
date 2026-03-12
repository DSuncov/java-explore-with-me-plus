package ru.practicum.event.entityparam;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminEventParam {
    List<Long> users;
    List<String> states;
    List<Long> categories;
    String rangeStart;
    String rangeEnd;
    Integer from;
    Integer size;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AdminEventParam that = (AdminEventParam) o;
        return Objects.equals(users, that.users)
                && Objects.equals(states, that.states)
                && Objects.equals(categories, that.categories)
                && Objects.equals(rangeStart, that.rangeStart)
                && Objects.equals(rangeEnd, that.rangeEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), users, states, categories, rangeStart, rangeEnd);
    }
}
