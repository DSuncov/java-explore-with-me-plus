package ru.practicum.request.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateRequest {
    @NotNull(message = "id событий должны быть указаны")
    @Size(min = 1, message = "Должен быть указан хотя бы один ID запроса")
    List<Long> requestIds;

    @NotNull(message = "Статус ответа должен быть указан")
    String status;
}
