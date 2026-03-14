package ru.practicum.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateResult {
    @Builder.Default
    List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();

    @Builder.Default
    List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
}