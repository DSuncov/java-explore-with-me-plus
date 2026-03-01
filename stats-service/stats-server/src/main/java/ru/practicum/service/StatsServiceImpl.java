package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.error.exception.ValidationException;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Override
    @Transactional
    public void addHit(EndpointHitDto endpointHitDto) {
        EndpointHit stat = statsMapper.toStat(endpointHitDto);
        statsRepository.save(stat);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end,
                                       List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new ValidationException("дата начала после даты окончания");
        }

        if (unique) {
            if (uris == null || uris.isEmpty()) {
                return statsRepository.findUniqueStats(start, end);
            } else {
                return statsRepository.findUniqueStatsByUris(start, end, uris);
            }
        } else {
            if (uris == null || uris.isEmpty()) {
                return statsRepository.findStats(start, end);
            } else {
                return statsRepository.findStatsByUris(start, end, uris);
            }
        }
    }
}