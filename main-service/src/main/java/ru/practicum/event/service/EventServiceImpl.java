package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.entity.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.entity.Location;
import ru.practicum.event.entityparam.AdminEventParam;
import ru.practicum.event.entityparam.PublicEventParam;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.PatchEventDto;
import ru.practicum.event.entity.Event;
import ru.practicum.event.enums.SortType;
import ru.practicum.event.enums.State;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.EventSpecification;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.entity.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> findEventsBy(PublicEventParam param, HttpServletRequest httpServletRequest) {
        Specification<Event> spec = Specification.where(null);
        spec.and(EventSpecification.hasText(param.getText()));
        spec.and(EventSpecification.hasCategories(param.getCategories()));
        spec.and(EventSpecification.hasRange(param.getRangeStart(), param.getRangeEnd()));
        spec.and(EventSpecification.isPaid(param.getPaid()));
        spec.and(EventSpecification.isOnlyAvailable(param.getOnlyAvailable()));

        Pageable pageable = PageRequest.of(param.getFrom(), param.getSize());

        if (param.getSort() == null || param.getSort().isBlank()) {
            if (String.valueOf(SortType.EVENT_DATE).equals(param.getSort())) {
                Sort sort = Sort.by(Sort.Direction.DESC, param.getSort());
                pageable = PageRequest.of(param.getFrom(), param.getSize(), sort);
                Page<Event> events = eventRepository.findAll(spec, pageable);
                return events.stream()
                        .map(eventMapper::toShortDto)
                        .toList();
            }
        }

        saveHit(httpServletRequest);

        List<Event> events = eventRepository.findAll(spec, pageable).getContent();

        Map<Long, Long> viewsForEvents = getViews(events);

        List<EventShortDto> eventsDto = events.stream()
                .map(e -> {
                    EventShortDto eventShortDto = eventMapper.toShortDto(e);
                    eventShortDto.setViews(viewsForEvents.getOrDefault(e.getId(), 0L));
                    return eventShortDto;
                })
                .toList();

        if (String.valueOf(SortType.VIEWS).equals(param.getSort())) {
            eventsDto.sort(Comparator.comparing(EventShortDto::getViews).reversed());
        }

        return eventsDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> findEventsBy(AdminEventParam param) {
        Specification<Event> spec = Specification.where(null);
        spec.and(EventSpecification.hasUsers(param.getUsers()));
        spec.and(EventSpecification.hasStates(param.getStates()));
        spec.and(EventSpecification.hasCategories(param.getCategories()));
        spec.and(EventSpecification.hasRange(param.getRangeStart(), param.getRangeEnd()));

        Pageable pageable = PageRequest.of(param.getFrom(), param.getSize());

        Page<Event> events = eventRepository.findAll(spec, pageable);

        return events.stream()
                .map(eventMapper::toFullDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto findEventById(Long id, HttpServletRequest httpServletRequest) {
        log.info("Получение пользователя по id.");
        Event event = eventRepository.findPublishedEventById(id)
                .orElseThrow(() -> new NotFoundException(String.format("События с id = %d не существует.", id)));
        saveHit(httpServletRequest);
        log.info("Информация о событии получена.");
        EventFullDto eventFullDto = eventMapper.toFullDto(event);
        log.info("Получаем количество просмотров.");
        eventFullDto.setViews(getStats(event));
        log.info("Количество просмотров получено.");
        return eventFullDto;
    }

    @Transactional
    @Override
    public EventFullDto patchEvent(Long id, PatchEventDto patchEventDto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id = %d отсутствует.", id)));

        if (!event.getState().equals(State.PENDING)) {
            throw new ConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации.");
        }

        if (patchEventDto.getStateAction() != null) {
            switch (patchEventDto.getStateAction()) {
                case "PUBLISH_EVENT" -> {
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    eventRepository.save(event);
                }

                case "REJECT_EVENT" -> {
                    event.setState(State.CANCELED);
                    eventRepository.save(event);
                }
            }
        }

        if (event.getPublishedOn() != null) {
            if (event.getEventDate().plusHours(1).isBefore(event.getPublishedOn())) {
                throw new ConflictException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации. " +
                        "Дата события: " + event.getEventDate() + ", дата публикации: " + event.getPublishedOn());
            }
        }

        patchFieldValidation(event, patchEventDto);
        getStats(event);
        return eventMapper.toFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto patchEventByUser(Long userId, Long eventId, PatchEventDto patchEventDto) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователя с id = %d не существует.", userId));
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id = %d отсутствует.", eventId)));

        if (!(event.getState().equals(State.PENDING) || event.getState().equals(State.CANCELED))) {
            throw new ConflictException("События со статусом PUBLISHED не могут быть изменены.");
        }

        if (patchEventDto.getStateAction() != null) {
            switch (patchEventDto.getStateAction()) {
                case "CANCEL_REVIEW" -> {
                    event.setState(State.CANCELED);
                    eventRepository.save(event);
                }

                case "SEND_TO_REVIEW" -> {
                    event.setState(State.PENDING);
                    eventRepository.save(event);
                }
            }
        }

        patchFieldValidation(event, patchEventDto);
        return eventMapper.toFullDto(event);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> findEventsBy(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователя с id = %d не существует.", userId));
        }

        Pageable pageable = PageRequest.of(from, size);
        Page<Event> events = eventRepository.findAll(pageable);

        return events.stream()
                .map(eventMapper::toShortDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto findEventByIdAndUser(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователя с id = %d не существует.", userId));
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id = %d отсутствует.", eventId)));

        return eventMapper.toFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto saveNewEvent(Long userId, NewEventDto newEventDto) {
        Location location = locationRepository.save(eventMapper.toEntity(newEventDto.getLocation()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id = %d не существует.", userId)));

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(String.format("Категории с id = %d не существует.", newEventDto.getCategory())));

        Event event = eventMapper.toEntity(newEventDto, user, category, location);
        Event createdEvent = eventRepository.save(event);

        return eventMapper.toFullDto(createdEvent);
    }

    private void saveHit(HttpServletRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            statsClient.saveHit(new EndpointHitDto(
                    null,
                    "main-service",
                    request.getRequestURI(),
                    request.getRemoteAddr(),
                    LocalDateTime.now().format(formatter)
            ));
        } catch (Exception e) {
            // Тут добавить что-нибудь более осмысленное
            throw new RuntimeException(e);
        }
    }

    private Long getStats(Event event) {
        Long views;

        try {
            List<ViewStatsDto> stats = statsClient.getStats(
                    event.getPublishedOn(),
                    LocalDateTime.now(),
                    List.of("/events/" + event.getId()),
                    true);

            return stats.isEmpty() ? 0L : stats.getFirst().getHits();
        } catch (Exception e) {
            // Тут добавить что-нибудь более осмысленное
            throw new RuntimeException(e);
        }
    }

    private Map<Long, Long> getViews(List<Event> events) {
        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .toList();
        List<ViewStatsDto> stats = statsClient.getStats(
                LocalDateTime.now(),
                LocalDateTime.now(),
                uris,
                true);

        return stats.stream().collect(Collectors.toMap(s ->
                Long.parseLong(s.getUri().split("/")[s.getUri().split("/").length - 1]), ViewStatsDto::getHits));

    }

    private void patchFieldValidation(Event event, PatchEventDto patchEventDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (patchEventDto.getCategory() != null) {
            Category category = categoryRepository.findById(patchEventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format("Категории с id = %d не существует.", patchEventDto.getCategory())));
            event.setCategory(category);
        }

        if (patchEventDto.getLocation() != null) {
            Location location = event.getLocation();
            location.setLat(patchEventDto.getLocation().getLat());
            location.setLon(patchEventDto.getLocation().getLon());
            locationRepository.save(location);
            event.setLocation(location);
        }

        if (patchEventDto.getAnnotation() != null) {
            event.setAnnotation(patchEventDto.getAnnotation());
        }

        if (patchEventDto.getDescription() != null) {
            event.setDescription(patchEventDto.getDescription());
        }

        if (patchEventDto.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(patchEventDto.getEventDate(), formatter));
        }

        if (patchEventDto.getPaid() != null) {
            event.setPaid(patchEventDto.getPaid());
        }

        if (patchEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(patchEventDto.getParticipantLimit());
        }

        if (patchEventDto.getRequestModeration() != null) {
            event.setRequestModeration(patchEventDto.getRequestModeration());
        }

        if (patchEventDto.getTitle() != null) {
            event.setTitle(patchEventDto.getTitle());
        }
    }
}
