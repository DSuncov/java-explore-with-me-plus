package ru.practicum.repository;

import ru.practicum.model.EndpointHit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

}
