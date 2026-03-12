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
public class PublicEventParam {
    String text;
    List<Long> categories;
    Boolean paid;
    String rangeStart;
    String rangeEnd;
    Boolean onlyAvailable;
    String sort;
    Integer from;
    Integer size;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PublicEventParam that = (PublicEventParam) o;
        return Objects.equals(text, that.text)
                && Objects.equals(categories, that.categories)
                && Objects.equals(paid, that.paid) && Objects.equals(rangeStart, that.rangeStart)
                && Objects.equals(rangeEnd, that.rangeEnd)
                && Objects.equals(onlyAvailable, that.onlyAvailable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text, categories, paid, rangeStart, rangeEnd, onlyAvailable);
    }
}
